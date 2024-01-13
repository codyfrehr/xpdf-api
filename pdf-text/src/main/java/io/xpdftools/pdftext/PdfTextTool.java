package io.xpdftools.pdftext;

import io.xpdftools.common.XpdfCommandType;
import io.xpdftools.common.XpdfTool;
import io.xpdftools.common.exception.*;
import io.xpdftools.common.util.ReadInputStreamTask;
import io.xpdftools.common.util.XpdfUtils;
import lombok.Builder;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
/**
 * A wrapper of the Xpdf command line tool <em>pdftotext</em>.
 *
 * <p> {@code PdfTextTool} automatically configures itself to target the <em>pdftotext</em> executable native to your OS and JVM architecture.
 * The {@link #process process} method invokes the command to extract text from your PDF file.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
public class PdfTextTool implements XpdfTool<PdfTextRequest, PdfTextResponse>  {

    /**
     * The command type.
     */
    private static final XpdfCommandType XPDF_COMMAND_TYPE = XpdfCommandType.PDF_TEXT;

    /**
     * The directory to which output text {@code Files} will be written if {@code textFile} is not provided in {@code PdfTextRequest}.
     */
    private final File defaultOutputDirectory;

    //todo: are threads managed correctly with process here?
    // should new singleton be declared, or retrieved
    // should you properly shutdown when done?
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    //todo: should throw better exception type for constructor, not runtime exception?
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
    //todo: add unit tests to ensure all os/bit value combos have resource
    //todo: add proper javadoc (fix description, add params, etc)
    //todo: clean up exception handling in all constructors
    //todo: javadoc
    public static class PdfTextToolBuilder {

        public PdfTextTool build() {
            val defaultOutputDirectoryBuilder = configureDefaultOutputDirectory();

            return new PdfTextTool(defaultOutputDirectoryBuilder);
        }

        protected File configureDefaultOutputDirectory() {
            if (defaultOutputDirectory == null) {
                return XpdfUtils.getPdfTextOutPath().toFile();
            } else {
                try {
                    if (!defaultOutputDirectory.isDirectory()) {
                        throw new XpdfRuntimeException("The default output directory must be a directory");
                    }
                } catch (Exception e) {
                    throw new XpdfRuntimeException("Unable to create temporary directory for output text files", e);
                }
                return defaultOutputDirectory;
            }
        }
    }

    //todo: maybe you should just drop the interface and simplify this
    //todo: add @NotNull to public method parameters
    //todo: break out logic into smaller methods
    /**
     * Gets text from a PDF file.
     *
     * <p> This method executes the <em>pdftotext</em> command with a given set of arguments.
     * Once processing is complete, it returns the text {@code File} that the text was extracted into.
     *
     * @param request the command arguments
     * @return the PDF text {@code File}
     * @throws XpdfValidationException if {@code PdfTextRequest} is invalid
     * @throws XpdfExecutionException if <em>pdftotext</em> command returns non-zero exit code
     * @throws XpdfProcessingException if any other exception occurs during processing
     * @since 4.4.0
     */
    @Override
    public PdfTextResponse process(PdfTextRequest request) throws XpdfException {
        val executorService = Executors.newSingleThreadExecutor();

        try {
            // validate request
            validate(request);

            // configure output text file
            final File textFile;
            if (request.getTextFile() != null) {
                textFile = request.getTextFile();
            } else {
                textFile = Paths.get(defaultOutputDirectory.getCanonicalPath(), String.format("%s.txt", UUID.randomUUID())).toFile();
            }

            // create output directory if not exists
            textFile.getParentFile().mkdir();

            //todo: should this really be checked everytime..?
            //      maybe it should just be checked/created once at startup
            // create bin resource on an OS-accessible directory
            if (!XpdfUtils.getPdfTextLocalPath().toFile().exists()) {
                val binResourceStream = XpdfUtils.class.getClassLoader().getResourceAsStream(XpdfUtils.getPdfTextResourceName());
                if (binResourceStream == null) {
                    throw new XpdfRuntimeException("Unable to locate Xpdf binaries");
                }
                FileUtils.copyInputStreamToFile(binResourceStream, XpdfUtils.getPdfTextLocalPath().toFile());
            }

            // get commands
            val commandParts = new ArrayList<String>();
            commandParts.add(XpdfUtils.getPdfTextLocalPath().toFile().getCanonicalPath());
            commandParts.addAll(getCommandOptions(request.getOptions()));
            commandParts.add(request.getPdfFile().getCanonicalPath());
            commandParts.add(textFile.getCanonicalPath());

            // process commands
            val processBuilder = new ProcessBuilder(commandParts.toArray(new String[0]));
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
                        .textFile(textFile)
                        .standardOutput(standardOutput)
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

                throw new XpdfExecutionException(standardOutput, errorOutput, message);
            }
        } catch (XpdfException | XpdfRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new XpdfProcessingException(e);
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Gets the options which can be invoked alongside the command.
     *
     * @param request the command arguments
     * @throws XpdfValidationException if {@code PdfTextRequest} is invalid
     * @since 4.4.0
     */
    protected void validate(PdfTextRequest request) throws XpdfValidationException {
        // verify files
        if (!request.getPdfFile().exists())
            throw new XpdfValidationException("PdfFile does not exist");

        if (request.getTextFile() != null) {
            try {
                request.getTextFile().getCanonicalPath();
            } catch (IOException e) {
                throw new XpdfValidationException("TxtFile does not have a valid path");
            }
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

        //todo: how can you be sure user is not injecting malicious arguments into file path, or other string fields on request?
        // need to verify if possible to do this, and prevent..
    }

    /**
     * Gets the options to be invoked alongside the command.
     *
     * @param options the command options as {@code PdfTextOptions}
     * @return the command options as {@code List<String>}
     * @since 4.4.0
     */
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

}
