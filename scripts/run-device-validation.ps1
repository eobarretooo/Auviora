param([Parameter(Mandatory = $true)][string]$Serial, [ValidateSet('Inference','Streaming')][string]$Suite = 'Inference')
$ErrorActionPreference = 'Stop'
$repo = Split-Path $PSScriptRoot -Parent
$adb = Join-Path $env:LOCALAPPDATA 'Android/Sdk/platform-tools/adb.exe'
$outDir = Join-Path $repo 'artifacts'
$testClass = if ($Suite -eq 'Streaming') { 'app.auviora.android.StreamingVisualTest' } else { 'app.auviora.android.RealInferenceTest#displayRealResponseCancelRecoverAndReload' }
$evidenceFile = if ($Suite -eq 'Streaming') { 'phase2-streaming.json' } else { 'phase2-inference.json' }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
function Invoke-Adb([string[]]$Arguments) {
    $result = & $adb -s $Serial @Arguments
    if ($LASTEXITCODE -ne 0) { throw "ADB command failed: $($Arguments -join ' ')" }
    return $result
}
$before = @{}
foreach ($setting in @('airplane_mode_on', 'wifi_on', 'mobile_data')) {
    $before[$setting] = (Invoke-Adb @('shell','settings','get','global',$setting)).Trim()
}
$before | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $outDir 'network-before.json') -Encoding utf8
try {
    Invoke-Adb @('shell','cmd','connectivity','airplane-mode','enable') | Out-Null
    Invoke-Adb @('shell','svc','wifi','disable') | Out-Null
    Invoke-Adb @('shell','svc','data','disable') | Out-Null
    $deadline = [DateTime]::UtcNow.AddSeconds(15)
    do {
        $network = Invoke-Adb @('shell','dumpsys','connectivity')
        if ($network -match 'Active default network: none') { break }
        Start-Sleep -Milliseconds 250
    } while ([DateTime]::UtcNow -lt $deadline)
    $network | Set-Content -LiteralPath (Join-Path $outDir 'offline-connectivity.txt')
    if (!($network -match 'Active default network: none')) { throw 'No-active-network condition not confirmed' }
    $result = Invoke-Adb @('shell','am','instrument','-w','-e','class',$testClass,'-e','realInference','true','app.auviora.android.test/androidx.test.runner.AndroidJUnitRunner')
    $result | Set-Content -LiteralPath (Join-Path $outDir "$Suite-test.log") -Encoding utf8
    $result | Write-Output
    if (($result -join "`n") -notmatch 'OK \(1 test\)') { throw 'Real inference test did not pass' }
    # Preserve raw UTF-8 bytes, avoiding the PowerShell console decoding pipeline.
    $env:AUVIORA_ADB = $adb
    $env:AUVIORA_DEVICE = $Serial
    $env:AUVIORA_EVIDENCE = Join-Path $outDir $evidenceFile
    $env:AUVIORA_REPORT = $evidenceFile
    @'
import os, subprocess
from pathlib import Path
result = subprocess.run([os.environ['AUVIORA_ADB'], '-s', os.environ['AUVIORA_DEVICE'], 'exec-out', 'run-as', 'app.auviora.android', 'cat', 'files/' + os.environ['AUVIORA_REPORT']], capture_output=True, check=True)
Path(os.environ['AUVIORA_EVIDENCE']).write_bytes(result.stdout)
'@ | python -
    if ($LASTEXITCODE -ne 0) { throw 'Evidence export failed' }
} finally {
    Invoke-Adb @('shell','cmd','connectivity','airplane-mode',$(if ($before.airplane_mode_on -eq '1') { 'enable' } else { 'disable' })) | Out-Null
    Invoke-Adb @('shell','svc','wifi',$(if ($before.wifi_on -eq '1') { 'enable' } else { 'disable' })) | Out-Null
    Invoke-Adb @('shell','svc','data',$(if ($before.mobile_data -eq '1') { 'enable' } else { 'disable' })) | Out-Null
}
