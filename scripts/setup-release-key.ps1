param(
    [string]$KeyStorePath = (Join-Path $env:USERPROFILE '.auviora/signing/auviora-release.p12'),
    [string]$Alias = 'auviora-release'
)
$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Split-Path $PSScriptRoot -Parent))
$keyPath = [IO.Path]::GetFullPath($KeyStorePath)
if ($keyPath.StartsWith($repo + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) { throw 'Keep the permanent key outside the repository.' }
if (Test-Path -LiteralPath $keyPath) { throw 'Refusing to overwrite an existing signing identity.' }
if ($Alias -eq 'androiddebugkey') { throw 'A release identity cannot use the debug alias.' }
$keytool = (Get-Command keytool -ErrorAction Stop).Source
$folder = Split-Path $keyPath -Parent
if ((Test-Path -LiteralPath $folder) -and (Get-ChildItem -LiteralPath $folder -Force | Select-Object -First 1)) { throw 'Choose a new or empty dedicated signing directory; existing directories are not re-permissioned.' }
New-Item -ItemType Directory -Force -Path $folder | Out-Null
$sid = [Security.Principal.WindowsIdentity]::GetCurrent().User
$acl = [Security.AccessControl.DirectorySecurity]::new()
$acl.SetOwner($sid)
$acl.SetAccessRuleProtection($true, $false)
$rule = [Security.AccessControl.FileSystemAccessRule]::new($sid, 'FullControl', 'ContainerInherit,ObjectInherit', 'None', 'Allow')
$acl.AddAccessRule($rule)
Set-Acl -LiteralPath $folder -AclObject $acl
Write-Host 'Create the permanent Auviora update identity. Keep the key and password in separate secure backups.'
Write-Host 'Enter passwords only in this local terminal. Do not paste them into chat or repository files.'
$first = Read-Host 'New keystore password (at least 12 characters)' -AsSecureString
$second = Read-Host 'Confirm password' -AsSecureString
$one = [IntPtr]::Zero
$two = [IntPtr]::Zero
try {
    $one = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($first)
    $two = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($second)
    $password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($one)
    if ($password.Length -lt 12 -or $password -cne [Runtime.InteropServices.Marshal]::PtrToStringBSTR($two)) { throw 'Passwords differ or are too short.' }
    $env:AUVIORA_SETUP_PASSWORD = $password
    & $keytool -genkeypair -keystore $keyPath -storetype PKCS12 -alias $Alias -keyalg RSA -keysize 4096 -validity 10000 -dname 'CN=Auviora Android Release' -storepass:env AUVIORA_SETUP_PASSWORD -keypass:env AUVIORA_SETUP_PASSWORD
    if ($LASTEXITCODE -ne 0) { throw 'Key generation failed; inspect the local directory before retrying.' }
    $certificate = Join-Path $folder 'public-certificate.der'
    & $keytool -exportcert -keystore $keyPath -alias $Alias -storepass:env AUVIORA_SETUP_PASSWORD -file $certificate
    if ($LASTEXITCODE -ne 0) { throw 'Public certificate export failed.' }
    $fingerprint = (Get-FileHash -LiteralPath $certificate -Algorithm SHA256).Hash.ToLowerInvariant()
    @{ storeFile = $keyPath; keyAlias = $Alias; certificateSha256 = $fingerprint } | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $folder 'identity.json') -Encoding utf8
    Write-Host "Created persistent signing identity. Public certificate SHA-256: $fingerprint"
    Write-Host 'Back up the keystore securely and retain its password in a separate password manager before publishing.'
} finally {
    Remove-Item Env:AUVIORA_SETUP_PASSWORD -ErrorAction SilentlyContinue
    $password = $null
    if ($one -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($one) }
    if ($two -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($two) }
    $first.Dispose(); $second.Dispose()
}
