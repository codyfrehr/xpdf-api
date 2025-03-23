/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
 * Copyright © 2025 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.xpdf.api.pdfinfo;

import io.xpdf.api.common.XpdfTool;
import io.xpdf.api.common.exception.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.xpdf.api.pdfinfo.util.PdfInfoUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * A wrapper of the <em>pdfinfo</em> command line tool.
 *
 * <br><br> Targets the <em>pdfinfo</em> executable native to your OS and JVM architecture.
 * The {@link #process} method executes a shell command to invoke the executable, which extracts information about a PDF file.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfInfoTool.builder()
 *      .executableFile(new File("~/libs/pdfinfo"))
 *      .timeoutSeconds(5)
 *      .build();
 * </pre></blockquote>
 *
 * @implNote See <em>pdfinfo</em> documentation for more detailed information about this function.
 * You can find source documentation alongside the executable file in the package resources.
 * @since 1.1.0
 */
@Builder
@Getter
@ToString
@Slf4j
public class PdfInfoTool implements XpdfTool<PdfInfoRequest, PdfInfoResponse> {

    /**
     * Executable file that should be invoked.
     *
     * @implNote If unassigned, this will default to the executable included with this project.
     * @since 1.1.0
     */
    private final File executableFile;

    /**
     * Maximum amount of time in seconds allotted to a process before timing out.
     *
     * @implNote If unassigned, this will default to 5 seconds.
     * @since 1.1.0
     */
    private final Integer timeoutSeconds;

    public static class PdfInfoToolBuilder {

        public PdfInfoTool build() {
            val executableFileBuilder = configureExecutableFile();
            val timeoutSecondsBuilder = configureTimeoutSeconds();

            return new PdfInfoTool(executableFileBuilder, timeoutSecondsBuilder);
        }

        protected File configureExecutableFile() {
            if (executableFile == null) {
                // copy executable from project resources to OS-accessible directory on local system
                val executablePath = getPdfInfoExecutablePath();
                if (!executablePath.toFile().exists()) {
                    val executableResourceStream = getClass().getClassLoader().getResourceAsStream(getPdfInfoExecutableResourceName());
                    if (executableResourceStream == null) {
                        throw new XpdfRuntimeException("Unable to locate executable in project resources");
                    }
                    try {
                        FileUtils.copyInputStreamToFile(executableResourceStream, executablePath.toFile());
                    } catch (IOException e) {
                        throw new XpdfRuntimeException("Unable to copy executable from resources to local system");
                    }
                }
                if (!executablePath.toFile().setExecutable(true)) {
                    throw new XpdfRuntimeException("Unable to set execute permissions on executable");
                }
                return executablePath.toFile();
            } else {
                if (!executableFile.exists()) {
                    throw new XpdfRuntimeException("The configured executable does not exist");
                }
                if (!executableFile.setExecutable(true)) {
                    throw new XpdfRuntimeException("Unable to set execute permissions on executable");
                }
                return executableFile;
            }
        }

        protected int configureTimeoutSeconds() {
            if (timeoutSeconds == null) {
                return getPdfInfoTimeoutSeconds();
            } else {
                return timeoutSeconds;
            }
        }
    }

    /**
     * Extracts information about a PDF file.
     *
     * <br><br> Invokes the <em>pdfinfo</em> executable against a PDF file.
     * This executable extracts the contents of the "Info" dictionary from a PDF file.
     *
     * <br><br> This method utilizes Java's {@link ProcessBuilder} API to execute a shell command that launches the executable.
     * Be aware that this method may become blocking for very large PDF files.
     *
     * @param request {@link PdfInfoRequest}
     * @return {@link PdfInfoResponse} with standard output containing info extracted from PDF
     * @throws XpdfValidationException if request is invalid
     * @throws XpdfExecutionException if process returns non-zero exit code
     * @throws XpdfTimeoutException if process duration exceeds timeout length
     * @throws XpdfProcessingException if any other exception occurs during processing
     * @implNote This method executes a shell command and may become blocking.
     * @since 1.1.0
     */
    @Override
    public PdfInfoResponse process(PdfInfoRequest request) throws XpdfException {
        log.debug("Process starting");
        Process process = null;

        try {
            // validate request
            log.debug("Validating request");
            validate(request);

            // get commands
            log.debug("Building command");
            val commandParts = getCommandParts(request);

            // process commands
            log.debug("Invoking executable; command: {}", commandParts.toString());
            val processBuilder = new ProcessBuilder(commandParts);
            process = processBuilder.start();

            // wait for process finish
            if (process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                val standardOutput = StringUtils.trimToNull(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
                val errorOutput = StringUtils.trimToNull(IOUtils.toString(process.getErrorStream(), Charset.defaultCharset()));
                log.debug("Invocation completed; exit code: {}, standard output: {}", process.exitValue(), standardOutput);

                // handle process finished
                if (process.exitValue() == 0) {
                    log.debug("Invocation succeeded");
                    return PdfInfoResponse.builder()
                            .standardOutput(standardOutput)
                            .build();
                } else {
                    log.debug("Invocation failed; error output: {}", errorOutput);
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
                    throw new XpdfExecutionException(message, standardOutput, errorOutput);
                }
            } else {
                // handle process timeout
                log.debug("Invocation timed out");
                throw new XpdfTimeoutException("Timeout reached before process could finish");
            }
        } catch (XpdfException | XpdfRuntimeException e) {
            log.debug("Process failed; exception message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.debug("Process failed; exception message: {}", e.getMessage());
            throw new XpdfProcessingException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
            log.debug("Process finished");
        }
    }

    /**
     * Validates a {@link PdfInfoRequest}.
     *
     * @param request {@link PdfInfoRequest}
     * @throws XpdfValidationException if request is invalid
     */
    protected void validate(PdfInfoRequest request) throws XpdfValidationException {
        if (request == null) {
            throw new XpdfValidationException("PdfInfoRequest cannot be null");
        }

        // check pdf file exists
        if (!request.getPdfFile().exists()) {
            throw new XpdfValidationException("PdfFile does not exist");
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
     * Gets the complete list of command parts for a {@code Process}.
     *
     * @param request {@link PdfInfoRequest}
     * @return command parts as {@code List<String>}
     * @throws IOException if canonical path of {@link #executableFile} is invalid
     */
    protected List<String> getCommandParts(PdfInfoRequest request) throws IOException {
        val commandParts = new ArrayList<String>();

        commandParts.add(executableFile.getCanonicalPath());
        commandParts.addAll(getCommandOptions(request.getOptions()));
        commandParts.add(request.getPdfFile().getCanonicalPath());

        return commandParts;
    }

    /**
     * Gets the command options to invoke the executable with.
     *
     * @param options {@link PdfInfoOptions}
     * @return command options as {@code List<String>}
     */
    protected List<String> getCommandOptions(PdfInfoOptions options) {
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

        val boundingBoxesIncluded = options.getBoundingBoxesIncluded();
        if (Boolean.TRUE.equals(boundingBoxesIncluded)) {
            args.add("-box");
        }

        val metadataIncluded = options.getMetadataIncluded();
        if (Boolean.TRUE.equals(metadataIncluded)) {
            args.add("-meta");
        }

        val datesUndecoded = options.getDatesUndecoded();
        if (Boolean.TRUE.equals(datesUndecoded)) {
            args.add("-rawdates");
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
