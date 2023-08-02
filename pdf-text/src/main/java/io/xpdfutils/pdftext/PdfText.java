package io.xpdfutils.pdftext;

import io.xpdfutils.common.*;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
public class PdfText implements XpdfUtility<PdfTextRequest, PdfTextResponse> {
    private final String binCommand;
    //todo: are threads managed correctly with process here?
    // should new singleton be declared, or retrieved
    // should you properly shutdown when done?
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PdfText() {
        //todo: add unit tests to ensure all os/bit value combos have resource
        val os = XpdfOperatingSystem.get();

        //todo: whats the best way to distribute resources?
        //      this solution copies binaries resource from inside jar to a directory outside of jar which accessible to client OS...
        //      but is there a better way? this solution feels dirty
        //      should we request client download the binaries themself, and configure path?
        //todo: also, there is no way for the client to verify that we are including the authentic xpdf binaries in this solution...
        //      how can you package the binaries with this solution in a credible way?
        //      maybe some way to incorporate the pgp key provided on xpdf website into build/distribution process? https://www.xpdfreader.com/download.html
        //todo: instead of copying resource every time an instance is created, a check should first be performed to make sure it doesnt already exist
        //      this same check should also be done in the process() method
        //      maybe move some of this code into common..
        val binName = "windows".equals(os.getOperatingSystem()) ? "pdftotext.exe" : "pdftotext";
        val binResourceStream = getClass().getClassLoader().getResourceAsStream("xpdf/%s/%s/%s".formatted(os.getOperatingSystem(), os.getBit(), binName));
        if (binResourceStream == null)
            throw new XpdfRuntimeException("Unable to locate pdftotext binaries");

        try {
            // copy bin resource to directory accessible to client OS
            val binAccessiblePath = Paths.get(System.getProperty("java.io.tmpdir"), "xpdf", binName);
            FileUtils.copyInputStreamToFile(binResourceStream, binAccessiblePath.toFile());
            binCommand = binAccessiblePath.toFile().getCanonicalPath();
        } catch (Exception e) {
            throw new XpdfRuntimeException("Unable to copy pdftotext binaries to directory accessible by OS");
        }

        // old approach
//        val exeDirectory = getClass().getClassLoader().getResource("xpdf/%s/%s".formatted(os.getOperatingSystem(), os.getBit()));
        //        if (exeDirectory == null)
//            throw new XpdfRuntimeException("Unable to locate pdftotext binaries");
//        try {
//            exeCommand = Paths.get(Paths.get(exeDirectory.toURI()).toFile().getCanonicalPath(),"pdftotext").toFile().getCanonicalPath();
//        } catch (Exception e) {
//            throw new XpdfRuntimeException("Unable to get path of pdftotext binaries");
//        }
    }

    //todo: is "process" really the most friendly name for this?
    // maybe you should just drop the interface and simplify this
    //todo: add @NotNull to public method parameters
    @Override
    public PdfTextResponse process(PdfTextRequest request) throws XpdfProcessingException, XpdfValidationException {
        val executorService = Executors.newSingleThreadExecutor();

        try {
            // validate request
            validate(request);

            // get commands
            //todo: how can you be sure user is not injecting malicious arguments into file path..?
            // need to verify args better..
            val commands = new ArrayList<String>();
            commands.add(binCommand);
            commands.addAll(getCommandOptions(request.getOptions()));
            commands.add(request.getPdfFile().getCanonicalPath());
            commands.add(request.getTxtFile().getCanonicalPath());

            // process commands
            val processBuilder = new ProcessBuilder(commands.toArray(new String[0]));
            val process = processBuilder.start();

            //todo: log output
            //todo: should you do this after waiting for process to finish, so that processing of pdf does not hang on waiting for std out?
            // or makes sense as is?
            val standardOutput = executorService.submit(new ReadInputStreamTask(process.getInputStream())).get();
            val errorOutput = executorService.submit(new ReadInputStreamTask(process.getErrorStream())).get();

            //todo: configurable timeout?
            val exitCode = process.waitFor();

            // handle process result
            if (exitCode == 0) {
                return PdfTextResponse.builder()
                        .pdfText(getPdfText(request))
                        .stdOut(standardOutput)
                        .build();
            } else {
                final String message;
                switch (exitCode) {
                    case 1 -> message = "Error opening the PDF file";
                    case 2 -> message = "Error opening the output file";
                    case 3 -> message = "Error related to PDF permissions";
                    case 99 -> message = "Other Xpdf error";
                    default -> message = "Unknown Xpdf error";
                }

                throw new XpdfProcessingException(standardOutput, errorOutput, message);
            }
        } catch (XpdfProcessingException | XpdfValidationException | XpdfRuntimeException e) {
            throw e;
        } catch (Exception e) {
            //todo: should we be throwing checked or unchecked exceptions?
            throw new XpdfRuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    protected void validate(PdfTextRequest request) throws XpdfValidationException {
        // verify files
        if (!request.getPdfFile().exists())
            throw new XpdfValidationException("PdfFile does not exist");

        try {
            request.getTxtFile().getCanonicalPath();
        } catch (IOException e) {
            throw new XpdfValidationException("TxtFile does not have a valid path");
        }

        // verify options
        val pageStart = request.getOptions().getPageStart();
        val pageEnd = request.getOptions().getPageEnd();
        if (pageStart != null && pageStart < 0)
            throw new XpdfValidationException("PageStart cannot be less than zero");
        if (pageEnd != null && pageEnd < 0)
            throw new XpdfValidationException("PageEnd cannot be less than zero");
        if (pageStart != null && pageEnd != null && pageStart > pageEnd)
            throw new XpdfValidationException("PageStart must come before PageEnd");

        //todo: what other validation would be helpful?
    }

    protected List<String> getCommandOptions(PdfTextOptions options) {
        if (options == null)
            return List.of();

        val args = new ArrayList<String>();

        //todo: add by default? or add when in debug? or add as option? or make configurable?
//        args.add("-verbose");

        val pageStart = options.getPageStart();
        if (pageStart != null)
            args.addAll(List.of("-f", pageStart.toString()));

        val pageEnd = options.getPageEnd();
        if (pageEnd != null)
            args.addAll(List.of("-l", pageEnd.toString()));

        val format = options.getFormat();
        if (format != null) {
            switch (format) {
                case RAW -> args.add("-raw");
                case SIMPLE -> args.add("-simple");
                case TABLE -> args.add("-table");
                case LAYOUT -> args.add("-layout");
                case LINE_PRINTER -> args.add("-lineprinter");
                default -> throw new XpdfRuntimeException("Format case %s is missing from command options switch statement".formatted(format.name()));
            }
        }

        val encoding = options.getEncoding();
        if (encoding != null) {
            switch (encoding) {
                case ASCII_7 -> args.addAll(List.of("-enc", "ASCII7"));
                case LATIN_1 ->args.addAll(List.of("-enc", "Latin1"));
                case SYMBOL -> args.addAll(List.of("-enc", "Symbol"));
                case UCS_2 -> args.addAll(List.of("-enc", "UCS-2"));
                case UTF_8 -> args.addAll(List.of("-enc", "UTF-8"));
                case ZAPF_DINGBATS -> args.addAll(List.of("-enc", "ZapfDingbats"));
                default -> throw new XpdfRuntimeException("Encoding case %s is missing from command options switch statement".formatted(encoding.name()));
            }
        }

        val endOfLine = options.getEndOfLine();
        if (endOfLine != null) {
            switch (endOfLine) {
                case DOS -> args.addAll(List.of("-eol", "dos"));
                case MAC -> args.addAll(List.of("-eol", "mac"));
                case UNIX -> args.addAll(List.of("-eol", "unix"));
                default -> throw new XpdfRuntimeException("EndOfLine case %s is missing from command options switch statement".formatted(endOfLine.name()));
            }
        }

        val pageBreakIncluded = options.isPageBreakIncluded();
        if (!pageBreakIncluded)
            args.add("-nopgbrk");

        val ownerPassword = options.getOwnerPassword();
        if (ownerPassword != null)
            args.addAll(List.of("-opw", "\"%s\"".formatted(ownerPassword)));

        val userPassword = options.getUserPassword();
        if (userPassword != null)
            args.addAll(List.of("-upw", "\"%s\"".formatted(userPassword)));

        return args;
    }

    protected String getPdfText(PdfTextRequest request) throws IOException {
        //todo: need to figure out encoding stuff...
        // when you specify encoding to xpdf, that helps it understand how to read the document.
        // but how does it decide to encode the data into the text file? does it retain same specified encoding?
        // run some tests...
        return String.join("\n", Files.readAllLines(request.getTxtFile().toPath(), StandardCharsets.UTF_8));
    }
}
