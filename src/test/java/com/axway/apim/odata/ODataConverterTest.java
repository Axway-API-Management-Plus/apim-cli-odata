package com.axway.apim.odata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.axway.apim.odata.spec.ODataSpecification;
import com.axway.apim.odata.spec.ODataV2Specification;
import com.axway.apim.odata.spec.ODataV4Specification;
import io.swagger.v3.oas.models.OpenAPI;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ODataConverterTest {

    ObjectMapper mapper = new ObjectMapper();

    private static final String TEST_PACKAGE = "/odata";


    @Test
    public void testODataV2API() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/ODataV2NorthWindMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/ODataV2NorthWindOpenAPI3.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "northwind-odata-v2.xml$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        Assert.assertEquals(apiDefinition.getDescription(), "The OData Service from northwind-odata-v2.xml$metadata");
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testODataV2APIWithFunctions() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/ODataV2ODataDemoMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/ODataV2ODataDemoOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testODataV2APIBackendFromMetadata() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/ODataV2NorthWindMetadata.xml");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/Northwind/Northwind.svc/$metadata", null);

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        JsonNode openAPI = mapper.readTree(apiDefinition.getApiSpecificationContent());

        Assert.assertEquals(openAPI.get("servers").get(0).get("url").asText(), "https://services.odata.org/V2/Northwind/Northwind.svc"); // Has our configured base path
    }

    @Test
    public void testSAPODataV2AccountDuplicateCheck() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-AccountDuplicateCheckMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-AccountDuplicateCheckOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataV2360ReviewsManagement() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-PMGMMultiraterMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-PMGMMultiraterOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataAccountsAndTransactionsAPI() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-AccountsAndTransactionsMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-AccountsAndTransactionsOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataBusinessPartnerA2XAPI() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-BusinessPartnerA2XMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-BusinessPartnerA2XOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataBasicProductAvailabilityInfoAPI() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-BasicProductAvailabilityInfoMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-BasicProductAvailabilityInfoOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataMasterDataForBusinessPartnerAPI() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-MasterDataForBusinessPartnerMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-MasterDataForBusinessPartnerOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test
    public void testSAPODataCustomerMaterialA2XAPI() throws Exception {

        byte[] odataMetadata = getAPISpecificationContent(TEST_PACKAGE + "/SAP-CustomerMaterialA2XMetadata.xml");
        byte[] odataOpenAPI3 = getAPISpecificationContent(TEST_PACKAGE + "/SAP-CustomerMaterialA2XOpenAPI.json");
        ODataSpecification apiDefinition = ODataConverter.convert(odataMetadata, "https://services.odata.org/V2/(S(owef4vwcosio0xpu1glpf320))/OData/OData.svc/$metadata", "https://myhost.customer.com:8767/api/v1/myAPI");

        Assert.assertTrue(apiDefinition instanceof ODataV2Specification);
        if (apiDefinition.getApiSpecificationContent().length != odataOpenAPI3.length) {
            System.out.print(new String(apiDefinition.getApiSpecificationContent(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(apiDefinition.getApiSpecificationContent(), odataOpenAPI3);
    }

    @Test(expectedExceptions = ODataConversionException.class, expectedExceptionsMessageRegExp = "Detected OData V3 metadata.*")
    public void testODataV3API() throws Exception {

        byte[] content = getAPISpecificationContent(TEST_PACKAGE + "/ODataV3ODataDemoMetadata.xml");
        ODataConverter.convert(content, "https://any.odata.service/$metadata", null);
    }

    @Test
    public void testODataV4API() throws Exception {

        byte[] content = getAPISpecificationContent(TEST_PACKAGE + "/ODataV4TrippinServiceMetadata.xml");
        ODataSpecification apiDefinition = ODataConverter.convert(content, "https://any.odata.service/$metadata", null);
        Assert.assertTrue(apiDefinition instanceof ODataV4Specification);
        ODataV4Specification oDataV4Specification = (ODataV4Specification) apiDefinition;
        byte[] openAPI = oDataV4Specification.getApiSpecificationContent();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<OpenAPI> api = new TypeReference<OpenAPI>() {
        };
        OpenAPI generatedAPI = objectMapper.readValue(openAPI, api);
        Assert.assertEquals(generatedAPI.getInfo().getTitle(), "Trippin OData Service");
    }


    @Test
    public void testOData4SAPSalesPricingReadOdataV4() throws Exception {
        byte[] content = getAPISpecificationContent(TEST_PACKAGE + "/ODataV4_SAP_OP_SLSPRCGACCESSSEQUENCE_0001.edmx");
        ODataSpecification apiDefinition = ODataConverter.convert(content, "https://any.odata.service/$metadata", null);
        Assert.assertTrue(apiDefinition instanceof ODataV4Specification);
        ODataV4Specification oDataV4Specification = (ODataV4Specification) apiDefinition;
        byte[] openAPI = oDataV4Specification.getApiSpecificationContent();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<OpenAPI> api = new TypeReference<OpenAPI>() {
        };
        OpenAPI generatedAPI = objectMapper.readValue(openAPI, api);
        //Console.println(new String(openAPI));
        Assert.assertEquals(generatedAPI.getInfo().getTitle(), "com.sap.gateway.srvd_a2x.api_slsprcgaccesssequence.v0001 OData Service");
    }


    private byte[] getAPISpecificationContent(String file) throws IOException {
        try (InputStream in = this.getClass().getResourceAsStream(file)) {
            if (in == null) throw new IOException("Resource not found: " + file);
            return in.readAllBytes();
        }
    }
}
