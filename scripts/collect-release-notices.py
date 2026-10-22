"""Collect pinned runtime notices from resolved Maven/native sources, without secrets."""
import hashlib
import io
import json
import os
import re
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def pom_licenses(path, depth=0):
    if depth > 8:
        raise ValueError("POM parent chain exceeds review limit")
    document = ET.parse(path)
    licenses = [{"name": n.findtext("m:name", "", NS), "url": n.findtext("m:url", "", NS)}
                for n in document.findall("./m:licenses/m:license", NS)]
    if licenses:
        return licenses
    parent = document.find("m:parent", NS)
    if parent is None:
        return []
    parts = [parent.findtext("m:" + field, "", NS) for field in ("groupId", "artifactId", "version")]
    matches = list(path.parents[4].joinpath(*parts).glob("*/*.pom"))
    if len(matches) != 1:
        raise ValueError(f"Cannot resolve parent license locally: {parts}")
    return pom_licenses(matches[0], depth + 1)


def archive_notices(data, label):
    found = []
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        for name in sorted(archive.namelist()):
            if not name.endswith("/") and re.search(r"(?:^|/)(?:license|notice|copying)[^/]*$", name, re.I):
                found.append((label + " / " + name, archive.read(name).decode("utf-8", errors="replace")))
            elif name == "classes.jar":
                found.extend(archive_notices(archive.read(name), label + " / classes.jar"))
    return found


def main():
    inventory = ROOT / "app/build/release-audit/dependencies.tsv"
    sections, catalog = [], []
    for row in inventory.read_text(encoding="utf-8").splitlines():
        group, name, version, pom_path = row.split("\t")
        pom = Path(pom_path)
        licenses = pom_licenses(pom)
        if not licenses or any("apache" not in n["name"].lower() for n in licenses):
            raise ValueError(f"Manual license review required: {group}:{name}:{version}: {licenses}")
        coordinate = f"{group}:{name}:{version}"
        artifacts = sorted(p for p in pom.parent.parent.rglob("*")
                           if p.suffix in (".aar", ".jar") and not re.search(r"-(sources|javadoc)\.", p.name))
        catalog.append({"coordinate": coordinate, "licenses": licenses,
                        "pom_sha256": hashlib.sha256(pom.read_bytes()).hexdigest(),
                        "artifacts": [{"name": p.name, "sha256": hashlib.sha256(p.read_bytes()).hexdigest()} for p in artifacts]})
        sections.append((coordinate, json.dumps(licenses)))
        for artifact in artifacts:
            sections.extend(archive_notices(artifact.read_bytes(), coordinate))
    for name in ("LICENSE", "docs/legal/REFERENCE_LICENSE.txt", "vendor/llama.cpp-omni/LICENSE",
                 "vendor/llama.cpp-omni/licenses/LICENSE-jsonhpp"):
        sections.append((name, (ROOT / name).read_text(encoding="utf-8")))
    for path in sorted((ROOT / "vendor/llama.cpp-omni/licenses").glob("LICENSE-*")):
        if path.name != "LICENSE-jsonhpp":
            sections.append((path.relative_to(ROOT).as_posix(), path.read_text(encoding="utf-8")))
    for directory in ("common", "ggml/src", "tools/mtmd"):
        for path in sorted((ROOT / "vendor/llama.cpp-omni" / directory).rglob("*")):
            if path.suffix.lower() in (".c", ".cpp", ".h", ".hpp", ".s"):
                source = path.read_text(encoding="utf-8", errors="replace")
                legal = [b for b in re.findall(r"/\*(.*?)\*/", source, re.S)
                         if "Permission is hereby granted" in b or "Redistribution and use" in b]
                if legal:
                    sections.append((path.relative_to(ROOT).as_posix() + " license blocks", "\n\n".join(legal)))
    for name in ("stb/stb_image.h", "miniaudio/miniaudio.h"):
        source = (ROOT / "vendor/llama.cpp-omni/vendor" / name).read_text(encoding="utf-8")
        blocks = re.findall(r"/\*(.*?)\*/", source, re.S)
        legal = [b for b in blocks if any(t in b for t in ("Permission is hereby granted", "Redistribution and use", "This software is available as"))]
        if not legal:
            raise ValueError(f"No embedded license blocks found: {name}")
        sections.append((name + " embedded license blocks", "\n\n".join(legal)))
    kleidi = ROOT / "inference/build/v86-cmake/arm64-v8a/_deps/kleidiai_download-src"
    for name in ("Apache-2.0.txt", "BSD-3-Clause.txt"):
        sections.append(("KleidiAI v1.24.0 / " + name, (kleidi / "LICENSES" / name).read_text(encoding="utf-8")))
    copyrights = set()
    for path in kleidi.rglob("*"):
        if path.suffix.lower() in (".c", ".h", ".s", ".cpp", ".md"):
            for line in path.read_text(encoding="utf-8", errors="replace").splitlines():
                if "SPDX-FileCopyrightText:" in line:
                    copyrights.add(line.split("SPDX-FileCopyrightText:", 1)[1].strip(" */"))
    sections.append(("KleidiAI source copyright inventory (conservative superset)", "\n".join(sorted(copyrights))))
    sdk = Path(os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT") or "")
    ndk = sdk / "ndk/27.0.12077973"
    # Preserve the complete pinned NDK notices rather than guessing which paragraphs apply.
    # This superset includes build tools not distributed in the APK; it does not relicense Auviora.
    for name in ("NOTICE", "NOTICE.toolchain"):
        sections.append(("NDK r27 / " + name + " (toolchain notice superset)", (ndk / name).read_text(encoding="utf-8")))
    header = "Auviora third-party notices\n\nModel weights are not bundled. NDK notices are a conservative toolchain superset, not a statement that all listed tools are linked into this application.\n"
    body = header + "".join(f"\n{'=' * 72}\n{label}\n{'=' * 72}\n{text}\n" for label, text in sections)
    output = ROOT / "docs/legal/apk-assets/third-party-notices.txt"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(body, encoding="utf-8")
    (ROOT / "docs/legal/runtime-dependencies.json").write_text(json.dumps(catalog, indent=2) + "\n", encoding="utf-8")
    print(f"Collected {len(catalog)} resolved module records; notices {output.stat().st_size} bytes")


if __name__ == "__main__":
    main()
