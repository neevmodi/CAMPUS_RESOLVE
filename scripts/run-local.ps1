param([string]$TomcatHome = 'C:\Tomcat\apache-tomcat-10.1.61')
$ErrorActionPreference = 'Stop'
if (-not (Test-Path "$TomcatHome\bin\startup.bat")) { throw "Tomcat startup script not found: $TomcatHome" }
if ([string]::IsNullOrWhiteSpace($env:CAMPUS_DB_USER)) { $env:CAMPUS_DB_USER = Read-Host 'Campus database user' }
if ([string]::IsNullOrWhiteSpace($env:CAMPUS_DB_PASSWORD)) {
    $secure = Read-Host 'Campus database password' -AsSecureString
    $env:CAMPUS_DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secure).Password
}
if ([string]::IsNullOrWhiteSpace($env:CAMPUS_DB_URL)) { $env:CAMPUS_DB_URL = 'jdbc:mysql://localhost:3306/campus_resolve?serverTimezone=UTC' }
$env:CATALINA_HOME = $TomcatHome
$env:CATALINA_BASE = $TomcatHome
if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME) -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    $jdk = Get-ChildItem 'HKLM:\SOFTWARE\JavaSoft\JDK' -ErrorAction SilentlyContinue |
        ForEach-Object { (Get-ItemProperty $_.PSPath).JavaHome } |
        Where-Object { $_ -and (Test-Path (Join-Path $_ 'bin\java.exe')) } |
        Select-Object -First 1
    if ([string]::IsNullOrWhiteSpace($jdk)) { throw 'A JDK was not found. Set JAVA_HOME to the folder containing bin\java.exe.' }
    $env:JAVA_HOME = $jdk
}
if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) {
    throw 'Port 8080 is already in use. Stop the existing Tomcat process before running this script again.'
}
& "$PSScriptRoot\deploy-local.ps1" -TomcatHome $TomcatHome
& "$TomcatHome\bin\startup.bat"
Write-Host 'Open http://localhost:8080/campus-resolve/ after Tomcat finishes deployment.'
