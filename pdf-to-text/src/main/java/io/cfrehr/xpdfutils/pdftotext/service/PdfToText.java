package io.cfrehr.xpdfutils.pdftotext.service;

import io.cfrehr.xpdfutils.common.*;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextResponse;
import lombok.val;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//@Builder
public class PdfToText extends XpdfProcess<PdfToTextRequest, PdfToTextResponse> {
    private final String exeCommand;
    //todo: are threads managed correctly with process here?
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PdfToText() throws XpdfException, URISyntaxException {
        //todo: add binaries for all OS/bit combos
        //todo: add unit tests to ensure all os/bit value combos have resource
        //todo: cant reference resources from jar.. need plan to package binaries properly
        //todo: can command args be wrapped in strings when running on linux/mac?
        val os = XpdfOperatingSystem.get();
        val exeDirectory = getClass().getClassLoader().getResource("xpdf/%s/%s".formatted(os.getOperatingSystem(), os.getBit()));
        if (exeDirectory != null)
//            exeCommand = Paths.get(Paths.get(exeDirectory.toURI()).toFile().getAbsolutePath(), "pdftotext").toFile().getAbsolutePath();
//            exeCommand = "\"" + (new File(exeDirectory.getPath())).getAbsolutePath() + "\"";
            exeCommand = "\"" + Paths.get(exeDirectory.toURI()).toFile().getAbsolutePath() + "/pdftotext.exe\"";
        else
            throw new XpdfException("Unable to locate pdftotext executable");
    }

    @Override
    public PdfToTextResponse process(PdfToTextRequest request) throws XpdfException {
        try {
            // process:
            // https://stackoverflow.com/a/30926869
            // https://www.baeldung.com/run-shell-command-in-java
            val processArgs = new ArrayList<String>();
            processArgs.add(exeCommand);
            processArgs.addAll(getArguments(request));

            val builder = new ProcessBuilder(processArgs.toArray(new String[0]));
            val process = builder.start();
            //todo: wtf is stream gobbler lol? ...i think its a dummy for something i need to create
//            StreamGobbler streamGobbler =
//                    new StreamGobbler(process.getOutputStream(), System.out::println);
//            Future<?> future = executorService.submit(streamGobbler);
            process.waitFor();

            if (process.exitValue() == 0) {
                return PdfToTextResponse.builder()
                        .pdfText(getPdfText(request))
                        .build();
            } else {
                //todo: log exitCode/standardOutput/errorOutput?
                //todo: use instead IOUtils.toString(inputStream, StandardCharsets.UTF_8)
                val standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
                System.out.println("standardOutput: " + standardOutput);

                val errorOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
                System.out.println("errorOutput: " + errorOutput);

                switch (process.exitValue()) {

                    case 1 -> throw new XpdfException("Error opening the PDF file");
                    case 2 -> throw new XpdfException("Error opening the output file");
                    case 3 -> throw new XpdfException("Error related to PDF permissions");
                    case 99 -> throw new XpdfException("Other Xpdf error");
                    default -> throw new XpdfException("Unknown Xpdf error");
                }
            }

//            switch (process.exitValue()) {
//                case 0 -> {
//                    return PdfToTextResponse.builder()
//                            .pdfText(getPdfText(request))
//                            .build();
//                }
//                //todo: clean up and verify error messages
//                case 1 -> throw new XpdfException("Error opening the PDF file");
//                case 2 -> throw new XpdfException("Error opening the output file");
//                case 3 -> throw new XpdfException("Error related to PDF permissions");
//                case 99 -> throw new XpdfException("Other Xpdf error");
//                default -> throw new XpdfException("Unknown Xpdf error");
//            }
        } catch (XpdfException e) {
            throw e;
        } catch (Exception e) {
            throw new XpdfException(e);
        }
    }

    @Override
    protected void validate(PdfToTextRequest request) throws XpdfException {
        val pageStart = request.getOptions().getPageStart();
        val pageEnd = request.getOptions().getPageEnd();
        if ((pageStart != null && pageStart < 0)
                || (pageEnd != null && pageEnd < 0))
            throw new XpdfException("Page option cannot be less than zero");
        if (pageStart != null && pageEnd != null && pageStart > pageEnd)
            throw new XpdfException("Start page must come before end page");

    }

    @Override
    protected List<String> getArguments(PdfToTextRequest request) {
        val args = new ArrayList<String>();

        val pageStart = request.getOptions().getPageStart();
        val pageEnd = request.getOptions().getPageEnd();
        if (pageStart != null)
            args.add("-f %s".formatted(pageStart));
        if (pageEnd != null)
            args.add("-l %s".formatted(pageEnd));

        val format = request.getOptions().getFormat();
        if (format != null) {
            switch (format) {
                case RAW -> args.add("-raw");
                case SIMPLE -> args.add("-simple");
                case TABLE -> args.add("-table");
                case LAYOUT -> args.add("-layout");
                case LINE_PRINTER -> args.add("-lineprinter");
            }
        }

        val encoding = request.getOptions().getEncoding();
        if (encoding != null) {
            switch (encoding) {
                case ASCII_7 -> args.add("-enc ASCII7");
                case LATIN_1 -> args.add("-enc Latin1");
                case SYMBOL -> args.add("-enc Symbol");
                case UCS_2 -> args.add("-enc UCS-2");
                case UTF_8 -> args.add("-enc UTF-8");
                case ZAPF_DINGBATS -> args.add("-enc ZapfDingbats");
            }
        }

        val endOfLine = request.getOptions().getEndOfLine();
        if (endOfLine != null) {
            switch (endOfLine) {
                case DOS -> args.add("-eol dos");
                case MAC -> args.add("-eol mac");
                case UNIX -> args.add("-eol unix");
            }
        }

        val pageBreakIncluded = request.getOptions().isPageBreakIncluded();
        if (!pageBreakIncluded)
            args.add("-nopgbrk");

        val ownerPassword = request.getOptions().getOwnerPassword();
        val userPassword = request.getOptions().getUserPassword();
        if (ownerPassword != null)
            args.add("-opw \"%s\"".formatted(ownerPassword));
        if (userPassword != null)
            args.add("-upw \"%s\"".formatted(userPassword));

        args.add("\"%s\"".formatted(request.getFilePathPdf()));
        args.add("\"%s\"".formatted(request.getFilePathTxt()));

        return args;
    }

    protected String getPdfText(PdfToTextRequest request) throws IOException {
        return String.join("\n", Files.readAllLines(Paths.get(request.getFilePathTxt())));
    }
}
