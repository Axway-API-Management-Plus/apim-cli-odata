package com.axway.apim.odata;

import com.axway.apim.odata.spec.ODataSpecification;
import com.axway.apim.odata.spec.ODataV2Specification;
import com.axway.apim.odata.spec.ODataV4Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Converts OData metadata ($metadata / EDMX) into an OpenAPI 3 specification.
 * <p>
 * Supported: OData V2 and OData V4. OData V3 is detected, but not supported.
 */
public class ODataConverter {
    private static final Logger LOG = LoggerFactory.getLogger(ODataConverter.class);

    private ODataConverter() {
    }

    /**
     * Converts the given OData metadata.
     *
     * @param metadata        the content of the OData $metadata document
     * @param source          where the metadata is from (file or URL). Used for the description of the API and to derive the backend
     * @param backendBasePath the backend of the OData service. If null and the source is an HTTP(S) URL, the backend
     *                        is derived from it. Otherwise, the specification has no server.
     * @return the parsed specification. Use {@link ODataSpecification#getApiSpecificationContent()} to get the OpenAPI JSON.
     * @throws ODataConversionException if the metadata is not supported or invalid
     */
    public static ODataSpecification convert(byte[] metadata, String source, String backendBasePath) throws ODataConversionException {
        if (metadata == null || metadata.length == 0) {
            throw new ODataConversionException("The OData metadata is empty.");
        }
        ODataSpecification specification = parse(metadata, source);
        if (backendBasePath != null || isHttpUrl(source)) {
            specification.configureBasePath(backendBasePath);
        } else {
            LOG.warn("No backend configured and the metadata was not read from an HTTP endpoint. The OpenAPI specification has no server.");
        }
        return specification;
    }

    private static ODataSpecification parse(byte[] metadata, String source) throws ODataConversionException {
        ODataSpecification v2 = new ODataV2Specification();
        v2.setApiSpecificationFile(source);
        if (v2.parse(metadata)) {
            LOG.info("Detected OData V2 metadata");
            return v2;
        }
        assertNotODataV3(metadata);
        ODataSpecification v4 = new ODataV4Specification();
        v4.setApiSpecificationFile(source);
        if (v4.parse(metadata)) {
            LOG.info("Detected OData V4 metadata");
            return v4;
        }
        throw new ODataConversionException("The given document is not valid OData V2 or OData V4 metadata ($metadata).");
    }

    private static void assertNotODataV3(byte[] metadata) throws ODataConversionException {
        if (metadata.length < 500) return;
        String specStart = new String(metadata, 0, 500, StandardCharsets.UTF_8).toLowerCase();
        if (specStart.contains("edmx") && specStart.contains("3.0")) {
            throw new ODataConversionException("Detected OData V3 metadata, which is not supported. Only OData V2 and V4 are supported.");
        }
    }

    private static boolean isHttpUrl(String source) {
        if (source == null) return false;
        String lower = source.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
