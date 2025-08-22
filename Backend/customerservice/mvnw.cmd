@echo off
setlocal
set MAVEN_OPTS=%MAVEN_OPTS% -Xmx1024m
set MVNW_REPOURL=https://repo.maven.apache.org/maven2

set DIRNAME=%~dp0
set MVNW_CMD_LINE_ARGS=%*

if exist "%DIRNAME%\.mvn\wrapper\maven-wrapper.jar" (
    set WRAPPER_JAR="%DIRNAME%\.mvn\wrapper\maven-wrapper.jar"
) else (
    set WRAPPER_JAR="%DIRNAME%..\.mvn\wrapper\maven-wrapper.jar"
)

if exist %WRAPPER_JAR% (
    java %MAVEN_OPTS% -jar %WRAPPER_JAR% %MVNW_CMD_LINE_ARGS%
) else (
    echo Maven Wrapper jar not found. Please run 'mvn -N io.takari:maven:wrapper' to generate it.
    exit /b 1
)
