package com.axway.apim.odata.spec;

import com.axway.apim.odata.ODataConversionException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class of the OData to OpenAPI 3 converters.
 */
public abstract class ODataSpecification {
    private static final Logger LOG = LoggerFactory.getLogger(ODataSpecification.class);

    protected OpenAPI openAPI;
    /**
     * Where the metadata was read from (file or URL). Used for the description and to derive the backend.
     */
    protected String apiSpecificationFile;
    protected byte[] apiSpecificationContent;

    public String getApiSpecificationFile() {
        return apiSpecificationFile;
    }

    public void setApiSpecificationFile(String apiSpecificationFile) {
        this.apiSpecificationFile = apiSpecificationFile;
    }

    /**
     * Parses the given OData metadata (EDMX) and creates the OpenAPI specification.
     *
     * @param apiSpecificationContent the OData metadata
     * @return true, if the metadata is valid for this OData version
     */
    public abstract boolean parse(byte[] apiSpecificationContent);

    /**
     * Configures the backend server of the generated OpenAPI specification.
     *
     * @param backendBasePath the backend URL or null to derive it from the location of the metadata
     * @throws ODataConversionException if the backend is invalid or cannot be derived
     */
    public void configureBasePath(String backendBasePath) throws ODataConversionException {
        if (backendBasePath == null) {
            // Try to set up the Backend-Host + BasePath based on the given Metadata URL
            try {
                String backend = getBasePath(apiSpecificationFile);
                Server server = new Server();
                LOG.info("Set backend server: {} based on given Metadata URL", backend);
                server.setUrl(backend);
                openAPI.addServersItem(server);
            } catch (MalformedURLException e) {
                throw new ODataConversionException("Unknown backend host. You either have to provide the metadata using an HTTP-Endpoint "
                    + "or configure a backendBasePath", e);
            }
        } else {
            LOG.info("Using backend base path: {}", backendBasePath);
            try {
                URL url = new URL(backendBasePath); // Parse it to make sure it is valid
                if (url.getPath() != null && !url.getPath().isEmpty() && !backendBasePath.endsWith("/")) { // See issue #178
                    backendBasePath += "/";
                }
                Server server = new Server();
                server.setUrl(backendBasePath);
                openAPI.addServersItem(server);
            } catch (MalformedURLException e) {
                throw new ODataConversionException("The configured backendBasePath: '" + backendBasePath + "' is invalid.", e);
            }
        }
    }

    public byte[] getApiSpecificationContent() {
        FilterProvider filter = new SimpleFilterProvider().setDefaultFilter(
            // "types" is the OpenAPI 3.1 representation of "type", which newer swagger-models add to every schema
            SimpleBeanPropertyFilter.serializeAllExcept("exampleSetFlag", "types"));
        // Use builder to avoid deprecated mutating configure() method (removed in Jackson 2.19+)
        ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .serializationInclusion(Include.NON_NULL)
            .filterProvider(filter)
            .build();
        mapper.addMixIn(Object.class, OpenAPIMixIn.class);
        try {
            return mapper.writeValueAsBytes(openAPI);
        } catch (JsonProcessingException e) {
            LOG.error("Error creating OpenAPI specification based on OData specification", e);
            return new byte[0];
        }
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    private String getBasePath(String pathToMetaData) throws MalformedURLException {
        // Only if the MetaData-Description is given from an HTTP-Endpoint we can use it
        new URL(pathToMetaData); // Try to parse it, only to see if it's a valid URL
        pathToMetaData = pathToMetaData.substring(0, pathToMetaData.lastIndexOf("/"));
        return pathToMetaData;
    }

    protected ApiResponse createResponse(String description, Schema<?> schema) {
        ApiResponse response = new ApiResponse();
        response.setDescription(description);
        Content content = new Content();
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);
        content.addMediaType("application/json", mediaType);
        response.setContent(content);
        return response;
    }

    public String getDescription() {
        return openAPI.getInfo().getDescription();
    }

    protected Schema<?> getSimpleSchema(String type) {
        switch (type) {
            case "Guid":
                return new UUIDSchema();
            case "Int16":
            case "Int32":
            case "Int64":
            case "Decimal":
                return new IntegerSchema();
            case "Double":
                return new NumberSchema();
            case "String":
            case "Single":
            case "Time":
            case "DateTimeOffset":
            case "GeographyPoint":
            case "Duration":
                return new StringSchema();
            case "DateTime":
                return new DateTimeSchema();
            case "Binary":
                return new BinarySchema();
            case "Boolean":
                return new BooleanSchema();
            default:
                return null;
        }
    }
}
