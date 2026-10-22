$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try { mvn clean test package; if (-not (Test-Path '.\target\campus-resolve.war')) { throw 'WAR was not created.' } }
finally { Pop-Location }
