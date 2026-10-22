param([string]$TomcatHome = 'C:\Tomcat\apache-tomcat-10.1.61')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
& "$PSScriptRoot\build.ps1"
if (-not (Test-Path "$TomcatHome\webapps")) { throw "Tomcat webapps folder not found: $TomcatHome\webapps" }
Copy-Item "$projectRoot\target\campus-resolve.war" "$TomcatHome\webapps\campus-resolve.war" -Force
Write-Host 'WAR copied. Restart Tomcat after confirming its process/service receives CAMPUS_DB_* variables.'
