package com.axway.apim.odata.cli;

import com.axway.apim.odata.ODataConversionException;
import com.axway.apim.odata.ODataConverter;
import com.axway.apim.odata.spec.ODataSpecification;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Command line entry point:
 * <pre>apim-odata convert -m &lt;file|url&gt; [-o openapi.json] [-b backend]</pre>
 */
public class ODataCLI {

    public static final String CONVERT = "convert";
    private static final String BINARY = "apim-odata";

    private ODataCLI() {
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }

    /**
     * Runs the CLI.
     *
     * @return the exit code. 0 for success.
     */
    public static int run(String[] args) {
        if (args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0]) || "help".equals(args[0])) {
            printUsage();
            return args.length == 0 ? 1 : 0;
        }
        if (!CONVERT.equals(args[0])) {
            System.err.println("Unknown command: " + args[0]);
            printUsage();
            return 1;
        }
        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);
        Options options = convertOptions();
        for (String arg : commandArgs) {
            if ("-h".equals(arg) || "--help".equals(arg)) { // checked first, as -m is required
                printConvertHelp(options);
                return 0;
            }
        }
        try {
            CommandLine cmd = new DefaultParser().parse(options, commandArgs);
            convert(cmd);
            return 0;
        } catch (ParseException e) {
            System.err.println("Error: " + e.getMessage());
            printConvertHelp(options);
            return 1;
        } catch (ODataConversionException | IOException e) {
            System.err.println("Error converting OData metadata: " + e.getMessage());
            return 2;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while reading the OData metadata.");
            return 2;
        }
    }

    private static void convert(CommandLine cmd) throws IOException, InterruptedException, ODataConversionException {
        String metadataLocation = cmd.getOptionValue("m");
        byte[] metadata = readMetadata(metadataLocation, cmd.getOptionValue("u"), cmd.getOptionValue("p"));
        ODataSpecification specification = ODataConverter.convert(metadata, metadataLocation, cmd.getOptionValue("b"));
        byte[] openAPI = specification.getApiSpecificationContent();
        String output = cmd.getOptionValue("o");
        if (output == null) {
            System.out.write(openAPI);
            System.out.println();
            System.out.flush();
        } else {
            Path outputFile = Paths.get(output).toAbsolutePath();
            if (outputFile.getParent() != null) {
                Files.createDirectories(outputFile.getParent());
            }
            Files.write(outputFile, openAPI);
            System.err.println("OpenAPI specification written to: " + outputFile);
        }
    }

    static byte[] readMetadata(String location, String username, String password) throws IOException, InterruptedException {
        String lower = location.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(location)).GET();
            if (username != null) {
                String credentials = username + ":" + (password == null ? "" : password);
                request.header("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
            }
            HttpResponse<byte[]> response = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
                .send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() > 299) {
                throw new IOException("Cannot read OData metadata from " + location + ". Response-Code: " + response.statusCode());
            }
            return response.body();
        }
        Path file = Paths.get(location);
        if (!Files.isRegularFile(file)) {
            throw new IOException("OData metadata file not found: " + file.toAbsolutePath());
        }
        return Files.readAllBytes(file);
    }

    static Options convertOptions() {
        Options options = new Options();
        options.addOption(Option.builder("m").longOpt("metadata").hasArg().argName("file|url").required()
            .desc("OData metadata ($metadata / EDMX): a local file or an HTTP(S) URL").build());
        options.addOption(Option.builder("o").longOpt("output").hasArg().argName("file")
            .desc("File to write the OpenAPI 3 specification (JSON) to. Default: standard output").build());
        options.addOption(Option.builder("b").longOpt("backendBasePath").hasArg().argName("url")
            .desc("Backend of the OData service, used as server of the OpenAPI specification. "
                + "Default: the metadata URL without /$metadata, if the metadata is read from an HTTP(S) URL").build());
        options.addOption(Option.builder("u").longOpt("username").hasArg().argName("user")
            .desc("Username (HTTP Basic) to read the metadata from an HTTP(S) URL").build());
        options.addOption(Option.builder("p").longOpt("password").hasArg().argName("password")
            .desc("Password (HTTP Basic) to read the metadata from an HTTP(S) URL").build());
        options.addOption(Option.builder("h").longOpt("help").desc("Print this help").build());
        return options;
    }

    private static void printConvertHelp(Options options) {
        new HelpFormatter().printHelp(BINARY + " " + CONVERT, "Converts OData V2/V4 metadata into an OpenAPI 3 specification.\n\n",
            options, "\nExample: " + BINARY + " " + CONVERT + " -m northwind-metadata.xml -b https://services.odata.org/V2/Northwind/Northwind.svc -o northwind-openapi.json", true);
    }

    private static void printUsage() {
        System.out.println("Usage: " + BINARY + " <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  convert    Convert OData V2/V4 metadata into an OpenAPI 3 specification");
        System.out.println();
        System.out.println("Run '" + BINARY + " convert -h' for the options of a command.");
    }
}
