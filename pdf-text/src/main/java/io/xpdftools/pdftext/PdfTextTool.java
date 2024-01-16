package io.xpdftools.pdftext;

import io.xpdftools.common.XpdfTool;
import io.xpdftools.common.exception.*;
import io.xpdftools.common.util.ReadInputStreamTask;
import io.xpdftools.common.util.XpdfUtils;
import lombok.Builder;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.xpdftools.common.util.XpdfUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
//todo: everywhere "native <em>pdftotext</em> library" used, just replace with "native library"
//      except maybe in this class javadoc, and public process method
//      just want to have consistent naming convention everywhere we talk about "the library"
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
     * The {@code Path} to the native <em>pdftotext</em> library that should be invoked.
     * By default, the library included with this project will be invoked.
     */
    protected final Path nativeLibraryPath;

    /**
     * The default directory that output text {@code Files} will be written to if not specified in {@code PdfTextRequest}.
     * By default, this value will be configured to {@link XpdfUtils#getPdfTextOutPath XpdfUtils.getPdfTextOutPath()}.
     */
    protected final File defaultOutputDirectory;

    //todo: test what happens in xpdf-apis if you dont configure this property
    //      i want to see what happens with reference data types...
    //      does it accept default value from XpdfUtils, xpdf-apis provide default value of 0, or is it just null?
    /**
     * The maximum amount of time in milliseconds allotted to <em>pdftotext</em> process before timing out.
     * By default, this value will be configured to {@link XpdfUtils#getPdfTextTimeoutMilliseconds()}  XpdfUtils.getPdfTextTimeoutMilliseconds()}.
     */
    protected final Long timeoutMilliseconds;

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
    //todo: clean up exception handling in all constructors
    //todo: javadoc
    public static class PdfTextToolBuilder {

        public PdfTextTool build() {
            val nativeLibraryPathBuilder = configureNativeLibraryPath();
            val defaultOutputDirectoryBuilder = configureDefaultOutputDirectory();
            val timeoutMillisecondsBuilder = configureTimeoutMilliseconds();

            return new PdfTextTool(nativeLibraryPathBuilder, defaultOutputDirectoryBuilder, timeoutMillisecondsBuilder);
        }

        //todo: is javadoc needed for lombok stuff? can lombok stuff even be included by javadoc plugin??
        /**
         * Configures path to native <em>pdftotext</em> library.
         *
         * @since 4.4.0
         */
        protected Path configureNativeLibraryPath() {
            if (nativeLibraryPath == null) {
                // copy library from project resources to OS-accessible directory on local system
                val pdfTextLocalPath = getPdfTextLocalPath();
                if (!pdfTextLocalPath.toFile().exists()) {
                    val binResourceStream = XpdfUtils.class.getClassLoader().getResourceAsStream(getPdfTextResourceName());
                    if (binResourceStream == null) {
                        throw new XpdfRuntimeException("Unable to locate native library in project resources");
                    }
                    try {
                        FileUtils.copyInputStreamToFile(binResourceStream, pdfTextLocalPath.toFile());
                    } catch (IOException e) {
                        throw new XpdfRuntimeException("Unable to copy native library from resources to local system");
                    }
                }
                return pdfTextLocalPath;
            } else {
                if (!nativeLibraryPath.toFile().exists()) {
                    throw new XpdfRuntimeException("The configured native library does not exist at the path specified");
                }
                return nativeLibraryPath;
            }
        }

        /**
         * Configures default output directory.
         *
         * @since 4.4.0
         */
        protected File configureDefaultOutputDirectory() {
            if (defaultOutputDirectory == null) {
                return getPdfTextOutPath().toFile();
            } else {
                defaultOutputDirectory.mkdir();
                return defaultOutputDirectory;
            }
        }

        /**
         * Configures process timeout.
         *
         * @since 4.4.0
         */
        protected long configureTimeoutMilliseconds() {
            if (timeoutMilliseconds == null) {
                return XpdfUtils.getPdfTextTimeoutMilliseconds();
            } else {
                return timeoutMilliseconds;
            }
        }
    }

    //todo: maybe you should just drop the interface and simplify this
    //todo: add @NotNull to all methods parameters where should not be null?
    /**
     * Gets text from a PDF file.
     *
     * <p> This method invokes the native <em>pdftotext</em> command with a given set of arguments.
     * Once processing is complete, it returns the text {@code File} that the text was extracted into.
     *
     * @param request the {@code PdfTextRequest}
     * @return the {@code PdfTextResponse} containing {@code File} with PDF text
     * @throws XpdfValidationException if {@code PdfTextRequest} is invalid
     * @throws XpdfNativeExecutionException if native <em>pdftotext</em> process returns non-zero exit code
     * @throws XpdfNativeTimeoutException if native <em>pdftotext</em> process duration exceeds timeout length
     * @throws XpdfProcessingException if any other exception occurs during processing
     * @since 4.4.0
     */
    @Override
    public PdfTextResponse process(PdfTextRequest request) throws XpdfException {
        val executorService = Executors.newSingleThreadExecutor();
        Process process = null;

        try {
            // validate request
            validate(request);

            // configure output text file
            val textFile = initializeTextFile(request);

            // get commands
            val commandParts = getCommandParts(request, textFile);

            // process commands
            val processBuilder = new ProcessBuilder(commandParts);
            process = processBuilder.start();

            //todo: should we provide any logging for this?
            val standardOutput = executorService.submit(new ReadInputStreamTask(process.getInputStream()));
            val errorOutput = executorService.submit(new ReadInputStreamTask(process.getErrorStream()));

            // wait for process finish
            //todo: look at other implementations of process to see if your pattern looks good.
            //      for example, FileSystemUtils.performCommand() has very similar structure, so thats nice!
            //      but look at some other implementations anyways to get an understanding of how things are done
            if (process.waitFor(timeoutMilliseconds, TimeUnit.MILLISECONDS)) {
                // handle process finished
                if (process.exitValue() == 0) {
                    return PdfTextResponse.builder()
                            .textFile(textFile)
                            .standardOutput(standardOutput.get())
                            .build();
                } else {
                    final String message;
                    switch (process.exitValue()) {
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
                    throw new XpdfNativeExecutionException(standardOutput.get(), errorOutput.get(), message);
                }
            } else {
                // handle process timeout
                throw new XpdfNativeTimeoutException("Timeout reached before process could finish");
            }
        } catch (XpdfException | XpdfRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new XpdfProcessingException(e);
        } finally {
            //todo: does order matter here?
            //todo: also, should really try to capture these in unit tests
            executorService.shutdown();
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * Validates the {@code PdfTextRequest}.
     *
     * @param request the {@code PdfTextRequest}
     * @throws XpdfValidationException if {@code PdfTextRequest} is invalid
     * @since 4.4.0
     */
    protected void validate(PdfTextRequest request) throws XpdfValidationException {
        if (request == null) {
            throw new XpdfValidationException("PdfTextRequest cannot be null");
        }

        // check pdf file exists
        if (!request.getPdfFile().exists()) {
            throw new XpdfValidationException("PdfFile does not exist");
        }

        // check valid text file path
        if (request.getTextFile() != null) {
            try {
                request.getTextFile().getCanonicalPath();
            } catch (IOException e) {
                throw new XpdfValidationException("Invalid path given for TextFile");
            }
        }

        // verify options
        if (request.getOptions() != null) {
            val pageStart = request.getOptions().getPageStart();
            val pageEnd = request.getOptions().getPageEnd();
            if (pageStart != null && pageStart < 0) {
                throw new XpdfValidationException("PageStart cannot be less than zero");
            }
            if (pageEnd != null && pageEnd < 0) {
                throw new XpdfValidationException("PageEnd cannot be less than zero");
            }
            if (pageStart != null && pageEnd != null && pageStart > pageEnd) {
                throw new XpdfValidationException("PageStart must come before PageEnd");
            }
        }

        //todo: what other validation would be helpful?

        //todo: how can you be sure user is not injecting malicious arguments into file path, or other string fields on request?
        // need to verify if possible to do this, and prevent..
    }

    /**
     * Gets the output text {@code File} to write to and creates parent directory.
     *
     * @param request the {@code PdfTextRequest}
     * @return the output text {@code File}
     * @throws IOException if the canonical path of default output directory is invalid
     * @since 4.4.0
     */
    protected File initializeTextFile(PdfTextRequest request) throws IOException {
        final File textFile;
        if (request.getTextFile() != null) {
            // use text file provided in request
            textFile = request.getTextFile();
        } else {
            // create text file from default output directory
            textFile = Paths.get(defaultOutputDirectory.getCanonicalPath(), String.format("%s.txt", UUID.randomUUID())).toFile();
        }

        // create output directory if not exists
        textFile.getParentFile().mkdir();

        return textFile;
    }

    /**
     * Gets the complete list of command parts to be executed in {@code Process}.
     *
     * @param request the {@code PdfTextRequest}
     * @param textFile the output text {@code File}
     * @return the command parts as {@code List<String>}
     * @throws IOException if the canonical path of local <em>pdftotext</em> library is invalid
     * @since 4.4.0
     */
    protected List<String> getCommandParts(PdfTextRequest request, File textFile) throws IOException {
        val commandParts = new ArrayList<String>();

        commandParts.add(nativeLibraryPath.toFile().getCanonicalPath());
        commandParts.addAll(getCommandOptions(request.getOptions()));
        commandParts.add(request.getPdfFile().getCanonicalPath());
        commandParts.add(textFile.getCanonicalPath());

        return commandParts;
    }

    /**
     * Gets the options to be invoked alongside the <em>pdftotext</em> command.
     *
     * @param options the command options as {@code PdfTextOptions}
     * @return the command options as {@code List<String>}
     * @since 4.4.0
     */
    protected List<String> getCommandOptions(PdfTextOptions options) {
        if (options == null) {
            return emptyList();
        }

        val args = new ArrayList<String>();

        //todo: add by default? or add when in debug? or add as option? or make configurable?
//        args.add("-verbose");

        val pageStart = options.getPageStart();
        if (pageStart != null) {
            args.addAll(asList("-f", pageStart.toString()));
        }

        val pageEnd = options.getPageEnd();
        if (pageEnd != null) {
            args.addAll(asList("-l", pageEnd.toString()));
        }

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
                    args.addAll(asList("-enc", "ASCII7"));
                    break;
                case LATIN_1:
                    args.addAll(asList("-enc", "Latin1"));
                    break;
                case SYMBOL:
                    args.addAll(asList("-enc", "Symbol"));
                    break;
                case UCS_2:
                    args.addAll(asList("-enc", "UCS-2"));
                    break;
                case UTF_8:
                    args.addAll(asList("-enc", "UTF-8"));
                    break;
                case ZAPF_DINGBATS:
                    args.addAll(asList("-enc", "ZapfDingbats"));
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("Encoding case %s is missing from command options switch statement", encoding.name()));
            }
        }

        val endOfLine = options.getEndOfLine();
        if (endOfLine != null) {
            switch (endOfLine) {
                case DOS:
                    args.addAll(asList("-eol", "dos"));
                    break;
                case MAC:
                    args.addAll(asList("-eol", "mac"));
                    break;
                case UNIX:
                    args.addAll(asList("-eol", "unix"));
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("EndOfLine case %s is missing from command options switch statement", endOfLine.name()));
            }
        }

        val pageBreakIncluded = options.isPageBreakIncluded();
        if (!pageBreakIncluded) {
            args.add("-nopgbrk");
        }

        val ownerPassword = options.getOwnerPassword();
        if (ownerPassword != null) {
            args.addAll(asList("-opw", String.format("\"%s\"", ownerPassword)));
        }

        val userPassword = options.getUserPassword();
        if (userPassword != null) {
            args.addAll(asList("-upw", String.format("\"%s\"", userPassword)));
        }

        return args;
    }

}
