$ErrorActionPreference = 'Stop'
$tomcatHome = 'C:\Tomcat\apache-tomcat-10.1.61'

Write-Host 'Campus Resolve environment check'
foreach ($command in @('java', 'mvn')) {
    if (Get-Command $command -ErrorAction SilentlyContinue) { & $command --version | Select-Object -First 1 } else { Write-Warning "$command was not found on PATH." }
}
if (Test-Path $tomcatHome) { Write-Host "Tomcat found: $tomcatHome" } else { Write-Warning "Tomcat was not found at $tomcatHome." }
$service = Get-Service -Name 'MySQL80' -ErrorAction SilentlyContinue
if ($service) { Write-Host "MySQL80 status: $($service.Status)" } else { Write-Warning 'MySQL80 service was not found.' }
foreach ($name in @('CAMPUS_DB_URL','CAMPUS_DB_USER','CAMPUS_DB_PASSWORD')) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name, 'Process'))) { Write-Warning "$name is not set for this PowerShell process." } else { Write-Host "$name is set." }
}
