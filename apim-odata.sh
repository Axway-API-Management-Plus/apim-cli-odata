#!/bin/sh
# Converts OData V2/V4 metadata into an OpenAPI 3 specification.
# Usage: ./apim-odata.sh convert -m <file|url> [-b <backend>] [-o openapi.json]
DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$(ls "$DIR"/target/apim-cli-odata-*-cli.jar "$DIR"/apim-cli-odata-*-cli.jar 2>/dev/null | head -n 1)"
if [ -z "$JAR" ]; then
  echo "apim-cli-odata-<version>-cli.jar not found. Build it with: mvn clean package" >&2
  exit 1
fi
exec java -jar "$JAR" "$@"
