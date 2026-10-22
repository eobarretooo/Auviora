# Controlled Android release process

Scope: local Windows release preparation and explicit GitHub draft/publication. Do not start Phase 3. The initial target is 0.1.0-alpha.1 / code 1; never replace a published version or tag. Use the existing [eobarretooo/Auviora](https://github.com/eobarretooo/Auviora) origin. APKs belong on GitHub Releases, never in Git history.

## Current status

Release configuration, unsigned candidate, runtime notices and validation infrastructure are prepared. Publication is blocked until the owner creates and backs up the permanent signing identity, commits its public certificate pin and completes signed-candidate verification. Building never creates a tag/release. See [Phase 2.2](PHASES/PHASE_02_2.md).

## Tools and GitHub access

Use Git, Python 3.10+, PowerShell, Android Studio/JDK 21 daemon, Gradle wrapper 9.3.1/AGP 9.1.1, SDK 36.1/Build Tools 36.0.0, NDK 27.0.12077973 and CMake 4.1.2. Configure ANDROID_HOME/JAVA_HOME locally; never commit SDK paths. Existing Foojay/daemon criteria select JDK 21. Investigate errors before upgrading dependencies.

GitHub CLI 2.103.0 was already installed through the official Windows package and authenticated through the credential store. If missing from PATH, open a fresh terminal or use its installed executable. An official installation command is `winget install --id GitHub.cli --exact`. If authentication is needed, use `gh auth login --web`; credentials stay in GitHub's local/browser flow, never chat or project files.

Inspect before releasing:

```powershell
git status --short
git log -5 --oneline
git remote -v
git tag -l
git ls-remote --heads --tags origin
gh auth status
gh repo view --json nameWithOwner,visibility,viewerPermission,defaultBranchRef
gh release list
```

Do not create duplicate repositories, change visibility/permissions or overwrite remote work. The current account has broader scopes; this workflow only reads the repository, performs normal source/tag pushes and operates releases. No organization/account settings are changed.

## Permanent signing identity

The owner runs [setup-release-key.ps1](../scripts/setup-release-key.ps1) in a local interactive terminal:

```powershell
./scripts/setup-release-key.ps1
```

The default directory is `.auviora/signing/` under the owner's home, outside the repository. The script refuses repository-local keys, existing key files, debug aliases and re-permissioning nonempty directories. A new/empty dedicated directory is restricted to the current Windows user. Two masked prompts collect the password locally. It generates a persistent RSA 4096 PKCS12 identity valid for 10,000 days and exports only a public certificate plus a password-free identity.json. Passwords reach keytool through a transient environment variable, never command-line literals/logs/source; transient values are cleared afterward.

Back up the keystore with encryption in at least two controlled locations. Retain its password separately in a password manager and test offline recovery without replacing the original. Loss prevents normal package updates; GitHub cannot recover the key. Never generate a fresh key per build or publish with a temporary identity. Private signing material is never a CI artifact.

After creation and backup, commit only the public fingerprint before building:

```powershell
$identityPath = Join-Path $env:USERPROFILE '.auviora/signing/identity.json'
$identity = Get-Content -Raw -LiteralPath $identityPath | ConvertFrom-Json
$identity.certificateSha256 | Set-Content docs/releases/signing-certificate.sha256 -Encoding ascii
git add docs/releases/signing-certificate.sha256
git commit -m 'build: pin the owner-managed release certificate'
```

All four Gradle signing variables are required together: AUVIORA_KEYSTORE, AUVIORA_KEY_ALIAS, AUVIORA_STORE_PASSWORD and AUVIORA_KEY_PASSWORD. The preparation script prompts locally and clears/restores them in finally. Its PKCS12 path uses the same store/key password, matching setup; other existing identities need their four secure environment values supplied explicitly. No debug fallback exists. Release/lintRelease work fails without signing unless the caller explicitly requests a non-distributable unsigned candidate.

## Version and committed source

1. Check that the target is not published or associated with an unexpected tag. Inspect existing drafts/tags; never silently replace them.
2. Use SemVer versionName and monotonically increasing versionCode. Ordinary documentation commits do not require a version increment. The initial target stays 0.1.0-alpha.1/code 1.
3. Keep Unreleased in the changelog; move selected entries into a dated, honestly labeled candidate section. Prepare English docs/releases/vVERSION.md from actual features and tests.
4. Review [binary notices](legal/RELEASE_AUDIT.md), model terms, update/data consequences and known limits.
5. Commit intended source/docs/public pin. Only the exact native patch may remain dirty. Never stage a changed vendor gitlink or discard the patch.

## Build and verify

```powershell
./scripts/prepare-release.ps1
```

Run in the owner's local terminal. It loads the public-pinned identity, asks for the password, inventories dependencies, collects notices, tests the native patch, builds release, runs five shared core JVM tests and all three release lint tasks, verifies the APK and stages a fresh timestamped directory under ignored artifacts/releases. It never tags, pushes, installs or publishes.

The build embeds assets/auviora-release-source.txt with the Git commit; stale binaries are rejected. Verification checks compiled package/version/SDK metadata, debuggable=false, the exact ten ARM64 libraries, 16 KB ELF LOAD alignment, ZIP alignment, absence of GGUF names/magic throughout the APK, packaged notices, apksigner success and the committed public certificate pin. Signed candidates require committed parent files. Existing candidate directories are never overwritten.

Output: Auviora-vVERSION-arm64-v8a.apk, SHA256SUMS.txt, THIRD_PARTY_NOTICES.txt and verification.json. The staged APK hash is recomputed. `apksigner verify --verbose --print-certs APK` exposes public certificate data only. Manual ZIP alignment must precede signing; do not edit a signed APK afterward.

Without a key, test infrastructure explicitly:

```powershell
./scripts/prepare-release.ps1 -UnsignedCandidate
python scripts/test-release.py -v
python scripts/check-docs.py
```

The filename ends in -UNSIGNED.apk, signed=false is recorded, and publication is rejected. This is a local candidate, not installable public distribution. Tests use the real unsigned APK and controlled hostile ZIP fixtures, not fake AI inference. Never upload that APK/checksum as a release.

Core exposes testDebugUnitTest rather than testReleaseUnitTest; it checks shared integrity/profile logic. Run debug source-regression hardware tests separately through [Testing](TESTING.md), recording that APK identity separately. Signed-release hardware testing needs a suitable separate device/profile; never erase the installed app automatically.

## Explicit draft and publication

All release gates must pass first. The publisher derives origin, checks repository/write access, exact committed source, signing, monotonic versionCode, notice/dependency review and native patch. It refuses existing published releases, stale candidates and unsigned/debug APKs.

```powershell
python scripts/publish-release.py draft artifacts/releases/CANDIDATE_DIRECTORY
python scripts/publish-release.py publish artifacts/releases/CANDIDATE_DIRECTORY
```

Draft creates an annotated vVERSION tag at the verified commit, performs normal fast-forward source/tag pushes, calls gh release create with --verify-tag/--draft/--prerelease/--notes-file, uploads only four verified files, downloads them and compares every hash. No tag is invented from an unchecked default-branch head. A later failure can leave a legitimate push/tag/draft: preserve it and inspect state before continuing. Duplicate draft creation is refused; an existing verified draft can proceed through publish.

Publish requires that draft and rechecks remote tag/asset bytes before explicitly making it a pre-release with latest=false. It reports actual URLs. Check public accessibility and re-download the public APK to verify SHA-256 before declaring distribution complete. Update README/changelog/handoff afterward in a separate documentation commit without retagging. Never invent a successful URL from a predicted path.

## Gates and recovery

- Permanent owner-managed key, committed public pin and recoverable backups.
- Correct unreleased version/code and exact committed source.
- Reproducible native pin/patch, build, relevant tests and lint without errors.
- Signed non-debug APK, correct metadata/ABI/alignment, no weights, current notices.
- Exact checksums, English Alpha notes and separate model-download disclosure.
- Annotated remote tag equals tested source; existing remote work is preserved.
- Draft assets re-download identically; public state and URLs checked after publication.

If a gate cannot pass, stop before distribution and retain a clearly labeled local candidate/draft with exact blockers. No forced installation, uninstall, history rewrite, tag replacement or automatic version bump.

The current development certificate is Android Debug, SHA-256 cb8cb08b661ad5772a6effe044c182ccee79b96cb60c0f2dac2dda7654107ebb. The permanent release certificate will differ. Android normally rejects an update with a different certificate. Uninstall deletes private conversations and downloaded GGUFs; backup/transfer is disabled and history export is absent. Keep the debug installation or use separate hardware/profile unless the owner explicitly chooses removal.

After a defective publication, clearly withdraw/mark it and ship a corrected new version/code with the same identity. Never swap assets behind an existing checksum/tag. Android generally blocks code downgrades; do not promise data-preserving downgrade. Recover signing material from verified backups, not Git.

## Automation boundary

Local Windows automation is implemented first because this native toolchain has actual Windows evidence. No GitHub-hosted signing job has been executed; no hosted compatibility claim or automatic publishing Actions file is added. Future Actions should use manual dispatch/protected release environments, pinned submodules/toolchains/actions, read-only defaults and contents:write only for publication, protected signing secrets, explicit tags and verified artifacts. Never expose signing to untrusted PRs or release on every push. Validate the native build on the chosen runner before enabling signed CI.

Official references: [Android signing](https://developer.android.com/studio/publish/app-signing), [apksigner](https://developer.android.com/tools/apksigner), [gh release create](https://cli.github.com/manual/gh_release_create), [GitHub releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository).
