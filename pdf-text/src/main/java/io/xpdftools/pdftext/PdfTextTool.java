package io.xpdftools.pdftext;

import io.xpdftools.common.XpdfTool;
import io.xpdftools.common.exception.*;
import io.xpdftools.common.util.ReadInputStreamTask;
import io.xpdftools.common.util.XpdfUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.xpdftools.common.util.XpdfUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * A wrapper of the <em>Xpdf</em> command line tool <em>pdftotext</em>.
 *
 * <br><br> Automatically configures itself to target the <em>pdftotext</em> library native to your OS and JVM architecture.
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
@Slf4j
public class PdfTextTool implements XpdfTool<PdfTextRequest, PdfTextResponse> {

    /**
     * Path to the native library that should be invoked.
     *
     * @implNote If unassigned, this value is configured to {@link XpdfUtils#getPdfTextNativeLibraryPath()},
     * which points to the native library included with this project.
     * @since 4.4.0
     */
    private final Path nativeLibraryPath;

    /**
     * Maximum amount of time in seconds allotted to the native process before timing out.
     *
     * @implNote If unassigned, this value is configured to {@link XpdfUtils#getPdfTextTimeoutSeconds()}.
     * @since 4.4.0
     */
    private final Integer timeoutSeconds;

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

    /**
     * Gets text from a PDF file.
     *
     * <br><br> This method invokes the native <em>pdftotext</em> library against a PDF file with a set of options.
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
        log.debug("Process starting");
        Process process = null;
        ExecutorService executorService = null;

        try {
            // validate request
            log.debug("Validating request");
            validate(request);

            // configure output text file
            log.debug("Configuring output text file");
            val textFile = initializeTextFile(request);

            // get commands
            log.debug("Building command");
            val commandParts = getCommandParts(request, textFile);

            // process commands
            log.debug("Invoking native library; command: {}", commandParts.toString());
            val processBuilder = new ProcessBuilder(commandParts);
            process = processBuilder.start();

            executorService = Executors.newSingleThreadExecutor();
            val standardOutput = executorService.submit(new ReadInputStreamTask(process.getInputStream()));
            val errorOutput = executorService.submit(new ReadInputStreamTask(process.getErrorStream()));

            // wait for process finish
            if (process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                log.debug("Invocation completed; exit code: {}, standard output: {}", process.exitValue(), standardOutput.get());

                // handle process finished
                if (process.exitValue() == 0) {
                    log.debug("Invocation succeeded");
                    return PdfTextResponse.builder()
                            .textFile(textFile)
                            .standardOutput(standardOutput.get())
                            .build();
                } else {
                    log.debug("Invocation failed; error output: {}", errorOutput.get());
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
                log.debug("Invocation timed out");
                throw new XpdfNativeTimeoutException("Timeout reached before process could finish");
            }
        } catch (XpdfException | XpdfRuntimeException e) {
            log.debug("Process failed; exception message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.debug("Process failed; exception message: {}", e.getMessage());
            throw new XpdfProcessingException(e);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
            if (process != null) {
                process.destroy();
            }
            log.debug("Process finished");
        }
    }

    /**
     * Validates a {@link PdfTextRequest}.
     *
     * @param request {@link PdfTextRequest}
     * @throws XpdfValidationException if request is invalid
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
     */
    protected List<String> getCommandOptions(PdfTextOptions options) {
        if (options == null) {
            return emptyList();
        }

        val args = new ArrayList<String>();

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
            args.addAll(asList("-opw", ownerPassword));
        }

        val userPassword = options.getUserPassword();
        if (userPassword != null) {
            args.addAll(asList("-upw", userPassword));
        }

        val nativeOptions = options.getNativeOptions();
        if (nativeOptions != null) {
            args.addAll(nativeOptions.entrySet().stream()
                    .map(it -> asList(it.getKey(), it.getValue()))
                    .flatMap(Collection::stream)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList())
            );
        }

        return args;
    }

}
