"""Explicit local draft/publish workflow; refuses unverified candidates and existing published releases."""
import argparse
import hashlib
import json
import os
import re
import shutil
import subprocess
import tempfile
from pathlib import Path

import release

ROOT = release.ROOT


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("action", choices=("draft", "publish"))
    parser.add_argument("candidate", type=Path)
    parser.add_argument("--sdk", type=Path, default=os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT"))
    args = parser.parse_args()
    if not args.sdk:
        parser.error("Set ANDROID_HOME or pass --sdk")
    gh = shutil.which("gh") or str(Path(os.environ.get("ProgramFiles", "")) / "GitHub CLI/gh.exe")

    def command(*items, check=True):
        result = subprocess.run([str(i) for i in items], cwd=ROOT, capture_output=True, timeout=300)
        if check and result.returncode:
            raise RuntimeError(result.stderr.decode("utf-8", errors="replace"))
        return result

    def git(*items):
        return command("git", *items).stdout.decode().strip()

    def github(*items):
        return json.loads(command(gh, *items).stdout)

    origin = git("remote", "get-url", "origin")
    match = re.fullmatch(r"(?:https://github.com/|git@github.com:)([^/]+/[^/]+?)(?:\.git)?", origin)
    if not match:
        raise ValueError("Use the existing canonical GitHub origin; unsupported remote URL")
    repo = match[1]
    info = github("repo", "view", repo, "--json", "nameWithOwner,viewerPermission,visibility,defaultBranchRef")
    if info["nameWithOwner"].lower() != repo.lower() or info["viewerPermission"] not in ("ADMIN", "MAINTAIN", "WRITE"):
        raise ValueError("Repository identity or write permission failed")
    if info["visibility"] != "PUBLIC":
        raise ValueError("Public distribution workflow will not change repository visibility")
    candidate = args.candidate.resolve()
    recorded = json.loads((candidate / "verification.json").read_text(encoding="utf-8"))
    if not recorded.get("signed") or recorded.get("parent_worktree_dirty"):
        raise ValueError("Only a signed candidate from committed source can enter the draft/publication workflow")
    name, _ = release.version()
    apk = candidate / f"Auviora-v{name}-arm64-v8a.apk"
    current = release.verify(apk, args.sdk, ROOT / "docs/releases/signing-certificate.sha256")
    if current != recorded or not current["signed"] or current["parent_worktree_dirty"]:
        raise ValueError("Candidate evidence is stale, unsigned or differs from the current committed source")
    audit = json.loads((ROOT / "docs/legal/release-audit.json").read_text(encoding="utf-8"))
    if audit["binary_distribution_review"] != "complete" or audit["unresolved_binary_blockers"]:
        raise ValueError("Binary redistribution review is not complete")
    if audit["notices_sha256"] != current["notices_sha256"]:
        raise ValueError("Notice bundle changed after review")
    if audit["maven_inventory_sha256"] != digest(ROOT / "docs/legal/runtime-dependencies.json"):
        raise ValueError("Dependency inventory changed after review")
    expected_sum = f"{current['sha256']}  {apk.name}\n"
    if (candidate / "SHA256SUMS.txt").read_text(encoding="ascii") != expected_sum:
        raise ValueError("Checksum file differs from the exact verified APK")
    if digest(candidate / "THIRD_PARTY_NOTICES.txt") != current["notices_sha256"]:
        raise ValueError("Staged notices differ from APK/source")
    tag = "v" + name
    commit = current["source_commit"]
    notes = ROOT / "docs/releases" / (tag + ".md")
    if not notes.exists():
        raise ValueError("Version-specific English release notes missing")
    pin = "96bbf61710d44421ea54e63f5194f9b17c20d877"
    if git("rev-parse", "HEAD:vendor/llama.cpp-omni") != pin:
        raise ValueError("Native public pin differs from reviewed baseline")
    command("git", "-C", "vendor/llama.cpp-omni", "apply", "--reverse", "--check", "../../patches/llama.cpp-omni/0001-portable-expert-cache.patch")
    if git("-C", "vendor/llama.cpp-omni", "diff", "--name-only") != "src/llama-moe-slot.cpp":
        raise ValueError("Unexpected native working-tree changes")
    actual_patch = git("-C", "vendor/llama.cpp-omni", "diff", "--no-ext-diff")
    expected_patch = (ROOT / "patches/llama.cpp-omni/0001-portable-expert-cache.patch").read_text(encoding="utf-8").strip()
    if actual_patch != expected_patch:
        raise ValueError("Native diff differs from the exact reviewed portability patch")
    releases = github("release", "list", "--repo", repo, "--limit", "100", "--json", "tagName,isDraft")
    for previous in (r for r in releases if not r["isDraft"]):
        with tempfile.TemporaryDirectory(prefix="auviora-version-") as temporary:
            command(gh, "release", "download", previous["tagName"], "--repo", repo, "--pattern", "verification.json", "--dir", temporary)
            old = json.loads((Path(temporary) / "verification.json").read_text(encoding="utf-8"))
            if current["versionCode"] <= old["versionCode"]:
                raise ValueError("versionCode must exceed every prior published release; inspect legacy releases manually")
    existing = next((r for r in releases if r["tagName"] == tag), None)
    if existing and (not existing["isDraft"] or args.action == "draft"):
        raise ValueError("Existing release will not be replaced")
    if args.action == "publish" and not existing:
        raise ValueError("First create and verify a draft")
    if args.action == "draft":
        command("git", "fetch", "origin", info["defaultBranchRef"]["name"])
        branch = info["defaultBranchRef"]["name"]
        command("git", "merge-base", "--is-ancestor", "origin/" + branch, "HEAD")
        remote_tag = git("ls-remote", "--tags", "origin", "refs/tags/" + tag, "refs/tags/" + tag + "^{}")
        local_tag = command("git", "rev-parse", "--verify", "refs/tags/" + tag, check=False)
        if local_tag.returncode == 0:
            if git("cat-file", "-t", "refs/tags/" + tag) != "tag" or git("rev-parse", tag + "^{}") != commit:
                raise ValueError("Existing local tag differs; never overwrite it")
        elif remote_tag:
            raise ValueError("Inspect the existing remote tag before continuing; it will not be overwritten")
        else:
            command("git", "tag", "-a", tag, commit, "-m", "Auviora " + tag)
        if remote_tag and commit not in remote_tag:
            raise ValueError("Remote tag does not identify the tested source")
        command("git", "push", "origin", "HEAD:refs/heads/" + branch)
        command("git", "push", "origin", "refs/tags/" + tag)
        command(gh, "release", "create", tag, str(apk), str(candidate / "SHA256SUMS.txt"),
                str(candidate / "THIRD_PARTY_NOTICES.txt"), str(candidate / "verification.json"),
                "--repo", repo, "--verify-tag", "--draft", "--prerelease", "--latest=false",
                "--title", "Auviora " + tag, "--notes-file", str(notes))
    remote_tag = git("ls-remote", "--tags", "origin", "refs/tags/" + tag + "^{}")
    if not remote_tag.startswith(commit + "\t"):
        raise ValueError("Remote annotated tag does not point at the tested commit")
    assets = (apk.name, "SHA256SUMS.txt", "THIRD_PARTY_NOTICES.txt", "verification.json")
    with tempfile.TemporaryDirectory(prefix="auviora-release-") as temporary:
        command(gh, "release", "download", tag, "--repo", repo, "--dir", temporary)
        for filename in assets:
            if digest(Path(temporary) / filename) != digest(candidate / filename):
                raise ValueError("Downloaded release asset differs: " + filename)
    state = github("release", "view", tag, "--repo", repo, "--json", "isDraft,isPrerelease,url,assets,name,tagName")
    if {a["name"] for a in state["assets"]} != set(assets) or state["name"] != "Auviora " + tag or state["tagName"] != tag:
        raise ValueError("Draft title/tag/asset inventory differs from the verified candidate")
    if not state["isDraft"] or not state["isPrerelease"]:
        raise ValueError("Expected a verified draft pre-release")
    if args.action == "publish":
        command(gh, "release", "edit", tag, "--repo", repo, "--draft=false", "--prerelease", "--latest=false")
        state = github("release", "view", tag, "--repo", repo, "--json", "isDraft,isPrerelease,url,assets")
        if state["isDraft"] or not state["isPrerelease"]:
            raise ValueError("Published pre-release state not confirmed")
    print(json.dumps(state, indent=2))


if __name__ == "__main__":
    main()
