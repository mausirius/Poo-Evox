@echo off
set "PROJECT_DIR=%~dp0"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
set "MAVEN_HOME=C:\Users\Mauri\.maven\apache-maven-3.9.9"
set "PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo No se encontro Java 25 en:
    echo %JAVA_HOME%
    pause
    exit /b 1
)

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo No se encontro Maven en:
    echo %MAVEN_HOME%
    pause
    exit /b 1
)

cd /d "%PROJECT_DIR%"
call "%MAVEN_HOME%\bin\mvn.cmd" -q exec:java -Dexec.mainClass=com.evoxspace.EvoxSpaceDesktopApplication
if errorlevel 1 pause
