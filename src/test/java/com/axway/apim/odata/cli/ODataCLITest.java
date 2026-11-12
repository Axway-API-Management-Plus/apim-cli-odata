package com.axway.apim.odata.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ODataCLITest {

    private static final String BACKEND = "https://myhost.customer.com:8767/api/v1/myAPI";

    private String resource(String name) throws Exception {
        return Paths.get(getClass().getResource("/odata/" + name).toURI()).toString();
    }

    @Test
    public void convertODataV2ToFile() throws Exception {
        Path out = Files.createTempDirectory("odata-cli").resolve("sub/openapi.json");
        int exit = ODataCLI.run(new String[]{"convert", "-m", resource("ODataV2NorthWindMetadata.xml"), "-b", BACKEND, "-o", out.toString()});
        Assert.assertEquals(exit, 0);
        JsonNode openAPI = new ObjectMapper().readTree(out.toFile());
        Assert.assertEquals(openAPI.get("openapi").asText().substring(0, 1), "3");
        Assert.assertEquals(openAPI.get("servers").get(0).get("url").asText(), BACKEND + "/");
        Assert.assertNotNull(openAPI.get("paths").get("/Products*"));
    }

    @Test
    public void convertODataV4ToStdout() throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        int exit;
        try {
            exit = ODataCLI.run(new String[]{"convert", "--metadata", resource("ODataV4TrippinServiceMetadata.xml"), "--backendBasePath", BACKEND});
        } finally {
            System.setOut(original);
        }
        Assert.assertEquals(exit, 0);
        JsonNode openAPI = new ObjectMapper().readTree(captured.toByteArray());
        Assert.assertEquals(openAPI.get("info").get("title").asText(), "Trippin OData Service");
    }

    @Test
    public void odataV3IsRejected() throws Exception {
        Assert.assertEquals(ODataCLI.run(new String[]{"convert", "-m", resource("ODataV3ODataDemoMetadata.xml")}), 2);
    }

    @Test
    public void missingFileFails() {
        Assert.assertEquals(ODataCLI.run(new String[]{"convert", "-m", "does-not-exist.xml"}), 2);
    }

    @Test
    public void missingMetadataOptionFails() {
        Assert.assertEquals(ODataCLI.run(new String[]{"convert"}), 1);
    }

    @Test
    public void unknownCommandFails() {
        Assert.assertEquals(ODataCLI.run(new String[]{"import"}), 1);
    }

    @Test
    public void helpSucceeds() {
        Assert.assertEquals(ODataCLI.run(new String[]{"--help"}), 0);
        Assert.assertEquals(ODataCLI.run(new String[]{"convert", "-h"}), 0);
    }
}
