package io.xpdftools.pdftext;

import io.xpdftools.common.XpdfTool;
import io.xpdftools.common.exception.*;
import io.xpdftools.common.util.ReadInputStreamTask;
import io.xpdftools.common.util.XpdfUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.xpdftools.common.util.XpdfUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

//todo: in the future, extend Callable so that users of sdk can run asynchronously if they would prefer
/**
 * A wrapper of the <em>Xpdf</em> command line tool <em>pdftotext</em>.
 *
 * <p> Automatically configures itself to target the <em>pdftotext</em> library native to your OS and JVM architecture.
 * The {@link #process} method invokes the native library to extract text from a PDF file.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfTextTool.builder()
 *      .nativeLibraryPath(Paths.get("C:/libs/pdftotext.exe"))
 *      .timeoutSeconds(60)
 *      .build();
 * </pre></blockquote>
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
@ToString
public class PdfTextTool implements XpdfTool<PdfTextRequest, PdfTextResponse>  {

    //todo: add @implNote to all properties of all builder classes, if necessary.
    //      for example, the statement about default value for this property is probably better in an @implNote
    //todo: add @since to all properties of all builder classes
    //      (or if not necessary, remove everywhere https://www.baeldung.com/javadoc-version-since)
    //      @since definitely NOT needed on private/protected methods.
    //      but it is useful on private builder properties, such as this property below, since it gets applied to builder methods
    //todo: should you use "If unassigned" instead of "By default", to match terminology used in other builders with nullable properties?
    /**
     * Path to the native library that should be invoked.
     *
     * @implNote If unassigned, this value is configured to {@link XpdfUtils#getPdfTextNativeLibraryPath},
     * which points to the native library included with this project.
     */
    private final Path nativeLibraryPath;

    /**
     * Maximum amount of time in seconds allotted to the native process before timing out.
     * By default, this value will be configured to {@link XpdfUtils#getPdfTextTimeoutSeconds}.
     */
    private final Integer timeoutSeconds;

    //todo: are threads managed correctly with process here?
    // should new singleton be declared, or retrieved
    // should you properly shutdown when done?
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    //todo: also, there is no way for the client to verify that we are including the authentic xpdf binaries in this solution...
    //      how can you package the binaries with this solution in a credible way?
    //      maybe some way to incorporate the pgp key provided on xpdf website into build/distribution process? https://www.xpdfreader.com/download.html
    //todo: is javadoc needed on builder/lombok stuff?
    //      probably not, since builder methods inherit javadoc from private properties
    //      besides, can lombok stuff even be included by javadoc plugin??
    //todo: can the static class PdfTextToolBuilder be made unaccessible to end user?
    //      not sure this is possible.
    //      but you can manually copy lombok-generated code for this class into this actual class, and play with modifiers, to see if possible
    public static class PdfTextToolBuilder {

        public PdfTextTool build() {
            val nativeLibraryPathBuilder = configureNativeLibraryPath();
            val timeoutSecondsBuilder = configureTimeoutSeconds();

            return new PdfTextTool(nativeLibraryPathBuilder, timeoutSecondsBuilder);
        }

        protected Path configureNativeLibraryPath() {
            if (nativeLibraryPath == null) {
                // copy library from project resources to OS-accessible directory on local system
                val pdfTextNativeLibraryPath = getPdfTextNativeLibraryPath();
                if (!pdfTextNativeLibraryPath.toFile().exists()) {
                    val nativeLibraryResourceStream = XpdfUtils.class.getClassLoader().getResourceAsStream(getPdfTextNativeLibraryResourceName());
                    if (nativeLibraryResourceStream == null) {
                        throw new XpdfRuntimeException("Unable to locate native library in project resources");
                    }
                    try {
                        FileUtils.copyInputStreamToFile(nativeLibraryResourceStream, pdfTextNativeLibraryPath.toFile());
                    } catch (IOException e) {
                        throw new XpdfRuntimeException("Unable to copy native library from resources to local system");
                    }
                }
                return pdfTextNativeLibraryPath;
            } else {
                if (!nativeLibraryPath.toFile().exists()) {
                    throw new XpdfRuntimeException("The configured native library does not exist at the path specified");
                }
                return nativeLibraryPath;
            }
        }

        protected int configureTimeoutSeconds() {
            if (timeoutSeconds == null) {
                return XpdfUtils.getPdfTextTimeoutSeconds();
            } else {
                return timeoutSeconds;
            }
        }
    }

    //todo: maybe you should just drop the interface and simplify this
    //todo: add @NotNull to all methods parameters where should not be null?
    /**
     * Gets text from a PDF file.
     *
     * <p> This method invokes the native <em>pdftotext</em> library against a PDF file with a set of options.
     * The native process extracts text from a PDF file into a text file.
     *
     * @param request {@link PdfTextRequest}
     * @return {@link PdfTextResponse} with text file containing text extracted from PDF
     * @throws XpdfValidationException if request is invalid
     * @throws XpdfNativeExecutionException if native process returns non-zero exit code
     * @throws XpdfNativeTimeoutException if native process duration exceeds timeout length
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

            //todo: would be cool to provide a log that prints out the command exactly as it would be entered in the terminal
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
            if (process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
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
     * Validates a {@link PdfTextRequest}.
     *
     * @param request {@link PdfTextRequest}
     * @throws XpdfValidationException if request is invalid
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
            val pageStop = request.getOptions().getPageStop();
            if (pageStart != null && pageStart <= 0) {
                throw new XpdfValidationException("PageStart must be greater than zero");
            }
            if (pageStop != null && pageStop <= 0) {
                throw new XpdfValidationException("PageStop must be greater than zero");
            }
            if (pageStart != null && pageStop != null && pageStart > pageStop) {
                throw new XpdfValidationException("PageStop must be greater than or equal to PageStart");
            }
        }
    }

    /**
     * Gets the text file that the native process should write to.
     *
     * @param request {@link PdfTextRequest}
     * @return text file
     * @throws IOException if canonical path of {@link XpdfUtils#getPdfTextTempOutputPath()} is invalid
     * @since 4.4.0
     */
    protected File initializeTextFile(PdfTextRequest request) throws IOException {
        final File textFile;
        if (request.getTextFile() != null) {
            // use text file provided in request
            textFile = request.getTextFile();
        } else {
            // create temporary text file
            textFile = Paths.get(getPdfTextTempOutputPath().toFile().getCanonicalPath(), String.format("%s.txt", UUID.randomUUID())).toFile();
            textFile.deleteOnExit();
        }

        // create output directory if not exists
        textFile.getParentFile().mkdir();

        return textFile;
    }

    /**
     * Gets the complete list of command parts for {@code Process}.
     *
     * @param request {@link PdfTextRequest}
     * @param textFile text file
     * @return command parts as {@code List<String>}
     * @throws IOException if canonical path of {@link #nativeLibraryPath} is invalid
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
     * Gets the command options to invoke with the native library.
     *
     * @param options {@link PdfTextOptions}
     * @return command options as {@code List<String>}
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

        val pageStop = options.getPageStop();
        if (pageStop != null) {
            args.addAll(asList("-l", pageStop.toString()));
        }

        val format = options.getFormat();
        if (format != null) {
            switch (format) {
                case LAYOUT:
                    args.add("-layout");
                    break;
                case SIMPLE:
                    args.add("-simple");
                    break;
                case SIMPLE_2:
                    args.add("-simple2");
                    break;
                case TABLE:
                    args.add("-table");
                    break;
                case LINE_PRINTER:
                    args.add("-lineprinter");
                    break;
                case RAW:
                    args.add("-raw");
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("Format case %s is missing from command options switch statement", format.name()));
            }
        }

        val encoding = options.getEncoding();
        if (encoding != null) {
            switch (encoding) {
                case LATIN_1:
                    args.addAll(asList("-enc", "Latin1"));
                    break;
                case ASCII_7:
                    args.addAll(asList("-enc", "ASCII7"));
                    break;
                case UTF_8:
                    args.addAll(asList("-enc", "UTF-8"));
                    break;
                case UCS_2:
                    args.addAll(asList("-enc", "UCS-2"));
                    break;
                case SYMBOL:
                    args.addAll(asList("-enc", "Symbol"));
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

        val pageBreakExcluded = options.getPageBreakExcluded();
        if (Boolean.TRUE.equals(pageBreakExcluded)) {
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

        val nativeOptions = options.getNativeOptions();
        if (nativeOptions != null) {
            args.addAll(nativeOptions.entrySet().stream()
                    .map(it -> asList(it.getKey(), StringUtils.isBlank(it.getValue()) ? null : String.format("\"%s\"", it.getValue())))
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }

        return args;
    }

}
