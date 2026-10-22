param([Parameter(Mandatory = $true)][string]$CMake)
$ErrorActionPreference = 'Stop'
$repo = Split-Path $PSScriptRoot -Parent
$fixture = Join-Path ([IO.Path]::GetTempPath()) ('minicpm-patch-' + [guid]::NewGuid())
function Invoke-Checked([string]$Program, [string[]]$Arguments) {
    & $Program @Arguments
    if ($LASTEXITCODE -ne 0) { throw "$Program failed ($LASTEXITCODE)" }
}
try {
    Invoke-Checked git @('clone', '--quiet', '--shared', '--no-checkout', (Join-Path $repo 'vendor/llama.cpp-omni'), $fixture)
    Invoke-Checked git @('-C', $fixture, 'checkout', 'HEAD', '--', 'src/llama-moe-slot.cpp')
    $source = Join-Path $fixture 'src/llama-moe-slot.cpp'
    $original = (Get-FileHash $source).Hash
    $apply = @("-DLLAMA_SRC=$fixture", '-P', (Join-Path $PSScriptRoot 'apply-native-patches.cmake'))
    Invoke-Checked $CMake $apply
    $patched = (Get-FileHash $source).Hash
    if ($patched -eq $original) { throw 'Patch did not change source' }
    Invoke-Checked $CMake $apply
    if ((Get-FileHash $source).Hash -ne $patched) { throw 'Patch is not idempotent' }
    Invoke-Checked git @('-C', $fixture, 'apply', '--reverse', (Join-Path $repo 'patches/llama.cpp-omni/0001-portable-expert-cache.patch'))
    if ((Get-FileHash $source).Hash -ne $original) { throw 'Reverse patch did not restore source' }
    [IO.File]::WriteAllText($source, ([IO.File]::ReadAllText($source).Replace('void apply_experts_nocache(bool on)', 'void conflicting_local_edit(bool on)')))
    $conflicting = (Get-FileHash $source).Hash
    $previousErrorActionPreference = $ErrorActionPreference
    try {
        # Windows PowerShell 5.1 turns redirected native stderr into error records.
        $ErrorActionPreference = 'Continue'
        $conflictOutput = & $CMake @apply 2>&1
        $conflictExitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    $conflictText = ($conflictOutput | ForEach-Object { $_.ToString() }) -join "`n"
    if ($conflictExitCode -ne 1 -or $conflictText -notmatch 'Native patch conflicts with checkout; preserving local changes') {
        throw "Expected the native patch conflict diagnostic and exit code 1; got $conflictExitCode. $conflictText"
    }
    if ((Get-FileHash $source).Hash -ne $conflicting) { throw 'Conflicting source was modified' }
    Write-Output 'PASS: clean application, idempotence, reversibility, conflict rejected without data loss'
} finally {
    $resolved = [IO.Path]::GetFullPath($fixture)
    $tempRoot = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
    if (!$resolved.StartsWith($tempRoot, [StringComparison]::OrdinalIgnoreCase)) { throw 'Unsafe fixture path' }
    if (Test-Path -LiteralPath $resolved) { Remove-Item -LiteralPath $resolved -Recurse -Force }
}
