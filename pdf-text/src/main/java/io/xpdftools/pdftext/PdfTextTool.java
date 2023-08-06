package io.xpdftools.pdftext;

import io.xpdftools.common.*;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
/**
 * A wrapper of the Xpdf command line tool <em>pdftotext</em>.
 *
 * <p> {@code PdfTextTool} automatically configures itself to target the <em>pdftotext</em> library native to your OS and JVM architecture.
 * The {@link #process process} method invokes the native library to extract text from your PDF file.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
public class PdfTextTool implements XpdfTool<PdfTextRequest, PdfTextResponse> {

    /**
     * The complete command path to be invoked.
     */
    private final String binCommand;

    //todo: are threads managed correctly with process here?
    // should new singleton be declared, or retrieved
    // should you properly shutdown when done?
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Creates an instance of {@code PdfTextTool} and configures itself to target the <em>pdftotext</em> library native to your OS and JVM architecture.
     *
     * @since 4.4.0
     */
    public PdfTextTool() {
        //todo: add unit tests to ensure all os/bit value combos have resource
        val targetSystem = XpdfUtils.getTargetSystem();

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
        val binName = targetSystem.contains("windows") ? "pdftotext.exe" : "pdftotext";
        val binResourceStream = getClass().getClassLoader().getResourceAsStream(String.format("xpdf/%s/%s", targetSystem, binName));
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
    }

    //todo: is "process" really the most friendly name for this?
    // maybe you should just drop the interface and simplify this
    //todo: add @NotNull to public method parameters
    //todo: is PdfTextRequest the best
    /**
     * Gets text from a PDF file.
     *
     * <p> This method invokes the <em>pdftotext</em> command with a given set of arguments.
     * Once processing is complete, it reads and returns the text from the generated text file.
     *
     * @param request the command arguments
     * @return the PDF text
     * @throws XpdfValidationException if {@code PdfTextRequest} is invalid
     * @throws XpdfProcessingException if <em>pdftotext</em> command returns non-zero exit code
     * @since 4.4.0
     */
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
                    case 1:
                        message = "Error opening the PDF file";
                        break;
                    case 2:
                        message = "Error opening the output file";
                        break;
                    case 3:
                        message = "Error related to PDF permissions";
                        break;
                    case 99:
                        message = "Other Xpdf error";
                        break;
                    default:
                        message = "Unknown Xpdf error";
                        break;
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

    //todo: add some kind of javadoc
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
        if (request.getOptions() != null) {
            val pageStart = request.getOptions().getPageStart();
            val pageEnd = request.getOptions().getPageEnd();
            if (pageStart != null && pageStart < 0)
                throw new XpdfValidationException("PageStart cannot be less than zero");
            if (pageEnd != null && pageEnd < 0)
                throw new XpdfValidationException("PageEnd cannot be less than zero");
            if (pageStart != null && pageEnd != null && pageStart > pageEnd)
                throw new XpdfValidationException("PageStart must come before PageEnd");
        }

        //todo: what other validation would be helpful?
    }

    //todo: add some kind of javadoc
    protected List<String> getCommandOptions(PdfTextOptions options) {
        if (options == null)
            return Collections.emptyList();

        val args = new ArrayList<String>();

        //todo: add by default? or add when in debug? or add as option? or make configurable?
//        args.add("-verbose");

        val pageStart = options.getPageStart();
        if (pageStart != null)
            args.addAll(Arrays.asList("-f", pageStart.toString()));

        val pageEnd = options.getPageEnd();
        if (pageEnd != null)
            args.addAll(Arrays.asList("-l", pageEnd.toString()));

        val format = options.getFormat();
        if (format != null) {
            switch (format) {
                case RAW:
                    args.add("-raw");
                    break;
                case SIMPLE:
                    args.add("-simple");
                    break;
                case TABLE:
                    args.add("-table");
                    break;
                case LAYOUT:
                    args.add("-layout");
                    break;
                case LINE_PRINTER:
                    args.add("-lineprinter");
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("Format case %s is missing from command options switch statement", format.name()));
            }
        }

        val encoding = options.getEncoding();
        if (encoding != null) {
            switch (encoding) {
                case ASCII_7:
                    args.addAll(Arrays.asList("-enc", "ASCII7"));
                    break;
                case LATIN_1:
                    args.addAll(Arrays.asList("-enc", "Latin1"));
                    break;
                case SYMBOL:
                    args.addAll(Arrays.asList("-enc", "Symbol"));
                    break;
                case UCS_2:
                    args.addAll(Arrays.asList("-enc", "UCS-2"));
                    break;
                case UTF_8:
                    args.addAll(Arrays.asList("-enc", "UTF-8"));
                    break;
                case ZAPF_DINGBATS:
                    args.addAll(Arrays.asList("-enc", "ZapfDingbats"));
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("Encoding case %s is missing from command options switch statement", encoding.name()));
            }
        }

        val endOfLine = options.getEndOfLine();
        if (endOfLine != null) {
            switch (endOfLine) {
                case DOS:
                    args.addAll(Arrays.asList("-eol", "dos"));
                    break;
                case MAC:
                    args.addAll(Arrays.asList("-eol", "mac"));
                    break;
                case UNIX:
                    args.addAll(Arrays.asList("-eol", "unix"));
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("EndOfLine case %s is missing from command options switch statement", endOfLine.name()));
            }
        }

        val pageBreakIncluded = options.isPageBreakIncluded();
        if (!pageBreakIncluded)
            args.add("-nopgbrk");

        val ownerPassword = options.getOwnerPassword();
        if (ownerPassword != null)
            args.addAll(Arrays.asList("-opw", String.format("\"%s\"", ownerPassword)));

        val userPassword = options.getUserPassword();
        if (userPassword != null)
            args.addAll(Arrays.asList("-upw", String.format("\"%s\"", userPassword)));

        return args;
    }

    //todo: add some kind of javadoc
    protected String getPdfText(PdfTextRequest request) throws IOException {
        //todo: need to figure out encoding stuff...
        // when you specify encoding to xpdf, that helps it understand how to read the document.
        // but how does it decide to encode the data into the text file? does it retain same specified encoding?
        // run some tests...
        //todo: you should not assume lines joined only by \n, especially since "endOfLine" is an options specified by user.
        //  you need to find another way to read file completely, and not assume line endings
        return String.join("\n", Files.readAllLines(request.getTxtFile().toPath(), StandardCharsets.UTF_8));
    }
}
