# Checks the tools this repo needs. Run from PowerShell:
#   powershell -ExecutionPolicy Bypass -File scripts\check-setup.ps1

$results = @()

function Get-Output([string]$commandLine) {
    # cmd /c merges stderr (java -version prints there) without PowerShell error noise
    return (cmd /c "$commandLine 2>&1" | Out-String).Trim()
}

function Add-Result([string]$status, [string]$name, [string]$detail) {
    $script:results += [pscustomobject]@{ Status = $status; Tool = $name; Detail = $detail }
}

function Has([string]$exe) { return [bool](Get-Command $exe -ErrorAction SilentlyContinue) }

# --- Java 21 -------------------------------------------------------------
if (Has 'java') {
    $v = Get-Output 'java -version'
    $first = ($v -split "`r?`n")[0]
    if ($v -match 'version "21') { Add-Result 'OK' 'Java 21' $first }
    else { Add-Result 'FIX' 'Java 21' "Found: $first  -> install JDK 21 and put it first on PATH / JAVA_HOME" }
} else { Add-Result 'FIX' 'Java 21' 'java not on PATH -> install JDK 21 (e.g. Temurin 21)' }
Add-Result 'INFO' 'JAVA_HOME' ($(if ($env:JAVA_HOME) { $env:JAVA_HOME } else { '(not set)' }))

# --- Maven (and which JDK Maven uses) -----------------------------------
if (Has 'mvn') {
    $m = Get-Output 'mvn -v'
    $mv = ($m -split "`r?`n")[0]
    $jv = (($m -split "`r?`n") | Where-Object { $_ -match '^Java version' }) -join ''
    if ($jv -match 'version: 21') { Add-Result 'OK' 'Maven' "$mv | $jv" }
    else { Add-Result 'FIX' 'Maven' "$mv | $jv  -> Maven must run on JDK 21 (set JAVA_HOME)" }
} else { Add-Result 'FIX' 'Maven' 'mvn not on PATH' }

# --- Git -----------------------------------------------------------------
if (Has 'git') {
    Add-Result 'OK' 'Git' (Get-Output 'git --version')
    $n = Get-Output 'git config --global user.name'
    $e = Get-Output 'git config --global user.email'
    if ($n -and $e) { Add-Result 'OK' 'Git identity' "$n <$e>" }
    else { Add-Result 'FIX' 'Git identity' 'git config --global user.name / user.email not set' }
} else { Add-Result 'FIX' 'Git' 'git not on PATH' }

# --- Docker --------------------------------------------------------------
if (Has 'docker') {
    Add-Result 'OK' 'Docker CLI' (Get-Output 'docker --version')
    $server = Get-Output 'docker info --format "{{.ServerVersion}}"'
    if ($LASTEXITCODE -eq 0 -and $server -notmatch 'error|cannot|failed') {
        Add-Result 'OK' 'Docker engine' "running (server $server)"
    } else { Add-Result 'FIX' 'Docker engine' 'not running -> start Docker Desktop (WSL2 backend)' }
    $c = Get-Output 'docker compose version'
    if ($c -match 'version') { Add-Result 'OK' 'Docker Compose' $c } else { Add-Result 'FIX' 'Docker Compose' $c }
} else { Add-Result 'FIX' 'Docker' 'docker not on PATH -> install Docker Desktop' }

# --- GitHub CLI ----------------------------------------------------------
if (Has 'gh') {
    Add-Result 'OK' 'GitHub CLI' ((Get-Output 'gh --version') -split "`r?`n")[0]
    $a = Get-Output 'gh auth status'
    if ($a -match 'Logged in') { Add-Result 'OK' 'GitHub login' 'gh is authenticated' }
    else { Add-Result 'FIX' 'GitHub login' 'run: gh auth login' }
} else { Add-Result 'OPTIONAL' 'GitHub CLI' 'not installed (handy for creating the repo; the website works too)' }

# --- Claude Code ---------------------------------------------------------
if (Has 'claude') { Add-Result 'OK' 'Claude Code' (Get-Output 'claude --version') }
else { Add-Result 'FIX' 'Claude Code' 'claude not on PATH' }

# --- k6 (needed from Day 2) ----------------------------------------------
if (Has 'k6') { Add-Result 'OK' 'k6' (Get-Output 'k6 version') }
else { Add-Result 'LATER' 'k6' 'not installed yet (needed Day 2)' }

# --- Ports the stack uses ------------------------------------------------
foreach ($p in 5432, 6379, 8081) {
    $busy = Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue
    if ($busy) {
        $proc = (Get-Process -Id ($busy[0].OwningProcess) -ErrorAction SilentlyContinue).ProcessName
        Add-Result 'CHECK' "Port $p" "in use by '$proc' (fine if it's this repo's Docker stack)"
    } else { Add-Result 'OK' "Port $p" 'free' }
}

# --- Report --------------------------------------------------------------
$colors = @{ OK = 'Green'; FIX = 'Red'; CHECK = 'Yellow'; LATER = 'Yellow'; OPTIONAL = 'Yellow'; INFO = 'Gray' }
Write-Host ''
foreach ($r in $results) {
    Write-Host ('[{0,-8}] {1,-15} {2}' -f $r.Status, $r.Tool, $r.Detail) -ForegroundColor $colors[$r.Status]
}
$fix = @($results | Where-Object Status -eq 'FIX').Count
Write-Host ''
if ($fix -eq 0) { Write-Host 'All required tools ready.' -ForegroundColor Green }
else { Write-Host "$fix item(s) to fix before Day 1." -ForegroundColor Red }
