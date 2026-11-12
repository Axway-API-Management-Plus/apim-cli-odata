@echo off
rem Converts OData V2/V4 metadata into an OpenAPI 3 specification.
rem Usage: apim-odata.bat convert -m ^<file^|url^> [-b ^<backend^>] [-o openapi.json]
set "DIR=%~dp0"
set "JAR="
for %%f in ("%DIR%target\apim-cli-odata-*-cli.jar" "%DIR%apim-cli-odata-*-cli.jar") do if not defined JAR set "JAR=%%~f"
if not defined JAR (
  echo apim-cli-odata-^<version^>-cli.jar not found. Build it with: mvn clean package 1>&2
  exit /b 1
)
java -jar "%JAR%" %*
