# apim-cli-odata

Converts OData metadata (`$metadata` / EDMX) into an OpenAPI 3 specification.

This project was split out of the [Axway API-Manager CLI](https://github.com/Axway-API-Management-Plus/apim-cli),
which no longer imports OData metadata directly. Convert the metadata with this tool, then import the resulting
OpenAPI file with `apim api import` as usual.

| OData version | Supported |
|---------------|-----------|
| V2            | yes       |
| V3            | no (detected and rejected) |
| V4            | yes       |

## Build

Requires Java 11+ and Maven.

```bash
mvn clean package
```

This creates `target/apim-cli-odata-<version>-cli.jar`, a self-contained executable jar.

## Usage

```bash
./apim-odata.sh convert -m <file|url> [-b <backend>] [-o <file>] [-u <user> -p <password>]
# or
java -jar target/apim-cli-odata-<version>-cli.jar convert -m <file|url> ...
```

| Option | Description |
|--------|-------------|
| `-m, --metadata` | OData metadata: a local file or an HTTP(S) URL (required) |
| `-b, --backendBasePath` | Backend of the OData service, used as `servers` entry of the OpenAPI file. If omitted and the metadata is read from an HTTP(S) URL, the URL without `/$metadata` is used. Otherwise the file has no server. |
| `-o, --output` | File to write the OpenAPI 3 specification (JSON) to. Default: standard output |
| `-u, --username`, `-p, --password` | HTTP Basic credentials to read the metadata from a URL |
| `-h, --help` | Print help |

Logs are written to stderr, so the specification on stdout can be piped. Set `LOG_LEVEL=debug` for more detail.

### Example

```bash
./apim-odata.sh convert -m examples/northwind-v2-metadata.xml \
    -b https://services.odata.org/V2/Northwind/Northwind.svc -o northwind-openapi.json
```

Then import the result into API-Manager:

```bash
apim api import -c my-api-config.json -s api-env
```

where `my-api-config.json` references `northwind-openapi.json` as its `apiSpecification.resource`.

APIs based on OData V2 still need the
[OData-Routing policy](https://github.com/Axway-API-Management-Plus/odata-routing-policy) in API-Manager.

### Exit codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | Invalid command or options |
| 2 | Metadata cannot be read or converted |

## Use as a library

```java
byte[] metadata = Files.readAllBytes(Paths.get("metadata.xml"));
ODataSpecification spec = ODataConverter.convert(metadata, "metadata.xml", "https://host/service");
byte[] openAPI = spec.getApiSpecificationContent(); // OpenAPI 3 JSON
```

## Differences to the former built-in OData support

* The `apiSpecification.filter` of the API config is not applied during conversion. It still works on the converted
  OpenAPI file when it is imported.
* There is no `ImportActionTest` special case anymore, which only existed for the former integration tests.
