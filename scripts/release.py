"""Verify and stage a release APK. Never signs, installs, tags, pushes or publishes."""
import argparse
import hashlib
import json
import os
import re
import shutil
import struct
import subprocess
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LIBRARIES = {"libandroidx.graphics.path.so", "libauviora_inference.so", "libggml-base.so", "libggml-cpu.so",
             "libggml-cpu-v86.so", "libggml.so", "libllama-common.so", "libllama.so", "libmtmd.so", "libomp.so"}


def run(*args, check=True):
    result = subprocess.run([str(a) for a in args], cwd=ROOT, capture_output=True, timeout=180)
    if check and result.returncode:
        raise RuntimeError(result.stderr.decode("utf-8", errors="replace") or result.stdout.decode("utf-8", errors="replace"))
    return result


def version():
    text = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")
    name = re.search(r'versionName\s*=\s*"([^"]+)"', text)[1]
    code = int(re.search(r"versionCode\s*=\s*(\d+)", text)[1])
    if not re.fullmatch(r"\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?", name):
        raise ValueError("Version name must be SemVer")
    return name, code


def verify(apk, sdk, certificate=None, allow_unsigned=False):
    name, code = version()
    tools = sdk / "build-tools/36.0.0"
    suffix = ".bat" if os.name == "nt" else ""
    badging = run(tools / ("aapt.exe" if os.name == "nt" else "aapt"), "dump", "badging", apk).stdout.decode("utf-8", errors="replace")
    match = re.search(r"package: name='([^']+)' versionCode='([^']+)' versionName='([^']+)'", badging)
    if not match or match.groups() != ("app.auviora.android", str(code), name):
        raise ValueError("APK identity/version differs from source")
    if "sdkVersion:'24'" not in badging or "targetSdkVersion:'36'" not in badging or "application-debuggable" in badging:
        raise ValueError("Unexpected SDK/debuggable metadata")
    native = re.search(r"native-code:\s*(.+)", badging)
    if not native or native[1].strip() != "'arm64-v8a'":
        raise ValueError("Only ARM64 distribution is supported")
    notices = ROOT / "docs/legal/apk-assets/third-party-notices.txt"
    commit = run("git", "rev-parse", "HEAD").stdout.decode().strip()
    with zipfile.ZipFile(apk) as archive:
        if archive.read("assets/auviora-release-source.txt").decode("ascii").strip() != commit:
            raise ValueError("APK embeds a different source commit; rebuild the committed release source")
        libraries = [n for n in archive.namelist() if n.startswith("lib/") and n.endswith(".so")]
        actual = {Path(n).name for n in libraries}
        if len(libraries) != len(LIBRARIES) or actual != LIBRARIES or any(not n.startswith("lib/arm64-v8a/") for n in libraries):
            raise ValueError(f"Unexpected native library inventory: {sorted(actual)}")
        for entry in libraries:
            data = archive.read(entry)
            if data[:5] != b"\x7fELF\x02":
                raise ValueError(f"Not ELF64: {entry}")
            offset = struct.unpack_from("<Q", data, 32)[0]
            size, count = struct.unpack_from("<HH", data, 54)
            for index in range(count):
                header = struct.unpack_from("<IIQQQQQQ", data, offset + index * size)
                if header[0] == 1 and header[-1] < 16384:
                    raise ValueError(f"Native LOAD alignment below 16KB: {entry}")
        for entry in archive.infolist():
            if not entry.is_dir():
                with archive.open(entry) as stream:
                    if entry.filename.lower().endswith(".gguf") or stream.read(4) == b"GGUF":
                        raise ValueError("Bundled model weights are prohibited")
        if archive.read("assets/third-party-notices.txt") != notices.read_bytes():
            raise ValueError("APK notices do not match the source notice bundle")
    run(tools / ("zipalign.exe" if os.name == "nt" else "zipalign"), "-c", "-P", "16", "4", apk)
    signature = run(tools / ("apksigner" + suffix), "verify", "--verbose", "--print-certs", apk, check=False)
    output = (signature.stdout + signature.stderr).decode("utf-8", errors="replace")
    signed = signature.returncode == 0
    fingerprint = None
    if signed:
        if "android debug" in output.lower() or "androiddebugkey" in output.lower():
            raise ValueError("Debug signing certificate rejected")
        fingerprints = re.findall(r"Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]+)", output)
        expected = certificate.read_text(encoding="utf-8-sig").strip().lower() if certificate else ""
        if len(fingerprints) != 1 or fingerprints[0].lower() != expected:
            raise ValueError("Signing certificate does not match the owner-approved public fingerprint")
        fingerprint = expected
    elif not allow_unsigned:
        raise ValueError("APK signature verification failed; do not distribute this candidate")
    elif "Missing META-INF/MANIFEST.MF" not in output and "No signatures" not in output:
        raise ValueError("Candidate has an unexpected signature failure; investigate before proceeding")
    changes = run("git", "status", "--porcelain", "--ignore-submodules=dirty").stdout.decode().strip()
    if signed and changes:
        raise ValueError("Commit intended source/docs changes before producing a signed release candidate")
    checks = {}
    for module in ("app", "core", "inference"):
        report = ET.parse(ROOT / module / "build/reports/lint-results-release.xml").getroot()
        issues = report.findall("issue")
        if any(i.get("severity") in ("Error", "Fatal") for i in issues):
            raise ValueError(f"Release lint errors: {module}")
        checks[module + "_lint_warnings"] = sum(i.get("severity") == "Warning" for i in issues)
    tests = [ET.parse(p).getroot() for p in (ROOT / "core/build/test-results/testDebugUnitTest").glob("TEST-*.xml")]
    if not tests or sum(int(t.get("tests", 0)) for t in tests) == 0 or any(int(t.get("failures", 0)) + int(t.get("errors", 0)) for t in tests):
        raise ValueError("Core JVM test reports missing or failing")
    checks["core_jvm_tests"] = sum(int(t.get("tests", 0)) for t in tests)
    return {"versionName": name, "versionCode": code, "package": "app.auviora.android", "abi": "arm64-v8a",
            "minSdk": 24, "targetSdk": 36, "signed": signed, "certificateSha256": fingerprint,
            "bytes": apk.stat().st_size, "sha256": hashlib.sha256(apk.read_bytes()).hexdigest(),
            "native_libraries": sorted(actual), "native_16kb_alignment": True, "zipalign": True,
            "bundled_weights": False, "notices_sha256": hashlib.sha256(notices.read_bytes()).hexdigest(),
            "source_commit": commit,
            "parent_worktree_dirty": bool(changes), "checks": checks}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=Path)
    parser.add_argument("--sdk", type=Path, default=os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT"))
    parser.add_argument("--certificate", type=Path)
    parser.add_argument("--allow-unsigned", action="store_true")
    parser.add_argument("--stage", type=Path)
    args = parser.parse_args()
    if not args.sdk:
        parser.error("Set ANDROID_HOME or pass --sdk")
    result = verify(args.apk.resolve(), args.sdk, args.certificate, args.allow_unsigned)
    if args.stage:
        destination = args.stage.resolve()
        destination.mkdir(parents=True, exist_ok=True)
        filename = f"Auviora-v{result['versionName']}-arm64-v8a" + ("" if result["signed"] else "-UNSIGNED") + ".apk"
        target = destination / filename
        if target.exists() or (destination / "verification.json").exists():
            raise FileExistsError("Use a fresh candidate directory; existing artifacts are never replaced")
        shutil.copyfile(args.apk, target)
        if hashlib.sha256(target.read_bytes()).hexdigest() != result["sha256"]:
            raise ValueError("Staged artifact differs from verified APK")
        (destination / "SHA256SUMS.txt").write_text(f"{result['sha256']}  {filename}\n", encoding="ascii")
        (destination / "THIRD_PARTY_NOTICES.txt").write_bytes((ROOT / "docs/legal/apk-assets/third-party-notices.txt").read_bytes())
        (destination / "verification.json").write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
