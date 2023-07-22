package io.cfrehr.xpdfutils.pdftotext.service;

import io.cfrehr.xpdfutils.common.*;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextResponse;
import lombok.val;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
public class PdfToText implements XpdfUtility<PdfToTextRequest, PdfToTextResponse> {
    private final String exeCommand;
    //todo: are threads managed correctly with process here?
    // should new singleton be declared, or retrieved
    // should you properly shutdown when done?
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PdfToText() {
        //todo: add unit tests to ensure all os/bit value combos have resource
        //todo: cant reference resources from jar.. need plan to package binaries properly
        //todo: can command args be wrapped in strings when running on linux/mac?
        val os = XpdfOperatingSystem.get();
        val exeDirectory = getClass().getClassLoader().getResource("xpdf/%s/%s".formatted(os.getOperatingSystem(), os.getBit()));
        if (exeDirectory == null)
            throw new XpdfRuntimeException("Unable to locate pdftotext binaries");
        try {
            exeCommand = Paths.get(Paths.get(exeDirectory.toURI()).toFile().getCanonicalPath(),"pdftotext").toFile().getCanonicalPath();
        } catch (Exception e) {
            throw new XpdfRuntimeException("Unable to get path of pdftotext binaries");
        }
    }

    //todo: is "process" really the most friendly name for this?
    // maybe you should just drop the interface and simplify this
    //todo: add @NotNull to public method parameters
    @Override
    public PdfToTextResponse process(PdfToTextRequest request) throws XpdfProcessingException, XpdfValidationException {
        val executorService = Executors.newSingleThreadExecutor();

        try {
            // validate request
            validate(request);

            // get commands
            //todo: how can you be sure user is not injecting malicious arguments into file path..?
            // need to verify args better..
            val commands = new ArrayList<String>();
            commands.add(exeCommand);
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

            val exitCode = process.waitFor();

            // handle process result
            if (exitCode == 0) {
                return PdfToTextResponse.builder()
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

    protected void validate(PdfToTextRequest request) throws XpdfValidationException {
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

    protected List<String> getCommandOptions(PdfToTextRequest.Options options) {
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

    protected String getPdfText(PdfToTextRequest request) throws IOException {
        //todo: need to figure out encoding stuff...
        // when you specify encoding to xpdf, that helps it understand how to read the document.
        // but how does it decide to encode the data into the text file? does it retain same specified encoding?
        // run some tests...
        return String.join("\n", Files.readAllLines(request.getTxtFile().toPath(), StandardCharsets.UTF_8));
    }
}
