param(
    [switch]$UnsignedCandidate,
    [string]$IdentityFile = (Join-Path $env:USERPROFILE '.auviora/signing/identity.json'),
    [string]$OutputDirectory
)
$ErrorActionPreference = 'Stop'
$repo = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $repo
if (!$env:ANDROID_HOME) { $env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android/Sdk' }
if (!$OutputDirectory) { $OutputDirectory = Join-Path $repo ('artifacts/releases/candidate-' + (Get-Date -Format 'yyyyMMdd-HHmmss')) }
$signingNames = @('AUVIORA_KEYSTORE','AUVIORA_KEY_ALIAS','AUVIORA_STORE_PASSWORD','AUVIORA_KEY_PASSWORD')
$previous = @{}
foreach ($name in $signingNames) { $previous[$name] = [Environment]::GetEnvironmentVariable($name) }
$passwordPointer = [IntPtr]::Zero
$secret = $null
try {
    if ($UnsignedCandidate) {
        foreach ($name in $signingNames) { [Environment]::SetEnvironmentVariable($name, $null) }
        $extra = @('-Pauviora.unsignedCandidate=true')
    } else {
        $identity = Get-Content -Raw -LiteralPath $IdentityFile | ConvertFrom-Json
        $pin = Join-Path $repo 'docs/releases/signing-certificate.sha256'
        if (!(Test-Path -LiteralPath $pin)) { throw 'First record and commit the public owner-approved certificate fingerprint under docs/releases/signing-certificate.sha256. Never commit the keystore.' }
        if ((Get-Content -Raw -LiteralPath $pin).Trim() -ne $identity.certificateSha256) { throw 'Local identity differs from the committed public certificate pin.' }
        $secret = Read-Host 'Auviora keystore password (local terminal only)' -AsSecureString
        $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
        $env:AUVIORA_KEYSTORE = $identity.storeFile
        $env:AUVIORA_KEY_ALIAS = $identity.keyAlias
        # The setup script uses one PKCS12 password for the store and key.
        $env:AUVIORA_STORE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
        $env:AUVIORA_KEY_PASSWORD = $env:AUVIORA_STORE_PASSWORD
        $extra = @()
    }
    & ./gradlew.bat :app:writeReleaseDependencyInventory @extra --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Dependency inventory failed.' }
    & python scripts/collect-release-notices.py
    if ($LASTEXITCODE -ne 0) { throw 'Notice collection requires investigation.' }
    & ./scripts/test-native-patches.ps1 -CMake "$env:ANDROID_HOME/cmake/4.1.2/bin/cmake.exe"
    & ./gradlew.bat :app:assembleRelease :core:testDebugUnitTest :app:lintRelease :core:lintRelease :inference:lintRelease @extra --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Release build/tests/lint failed.' }
    $metadata = Get-Content -Raw app/build/outputs/apk/release/output-metadata.json | ConvertFrom-Json
    if (@($metadata.elements).Count -ne 1) { throw 'Expected exactly one ARM64 APK.' }
    $apk = Join-Path 'app/build/outputs/apk/release' $metadata.elements[0].outputFile
    $verification = @()
    if ($UnsignedCandidate) { $verification = @('--allow-unsigned') } else { $verification = @('--certificate', $pin) }
    & python scripts/release.py $apk @verification --stage $OutputDirectory
    if ($LASTEXITCODE -ne 0) { throw 'Artifact verification failed; do not publish.' }
    Write-Host "Candidate staged in $OutputDirectory. No tag, push, installation or publication was performed."
} finally {
    foreach ($name in $signingNames) { [Environment]::SetEnvironmentVariable($name, $previous[$name]) }
    if ($passwordPointer -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer) }
    if ($secret) { $secret.Dispose() }
}
