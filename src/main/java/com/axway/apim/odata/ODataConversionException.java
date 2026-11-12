package com.axway.apim.odata;

/**
 * Thrown if OData metadata cannot be converted into an OpenAPI specification.
 */
public class ODataConversionException extends Exception {

    public ODataConversionException(String message) {
        super(message);
    }

    public ODataConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
