/*
 * PdfImages API - An API for accessing a native pdfimages library (https://xpdf.io)
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
package io.xpdf.api.pdfimages;

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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.xpdf.api.pdfimages.util.PdfImagesUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * A wrapper of the <em>pdfimages</em> command line tool.
 *
 * <br><br> Targets the <em>pdfimages</em> executable native to your OS and JVM architecture.
 * The {@link #process} method executes a shell command to invoke the executable, which extracts and saves images from a PDF file.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfImagesTool.builder()
 *      .executableFile(new File("~/libs/pdfimages"))
 *      .timeoutSeconds(60)
 *      .build();
 * </pre></blockquote>
 *
 * @implNote See <em>pdfimages</em> documentation for more detailed information about this function.
 * You can find source documentation alongside the executable file in the package resources.
 * @since 1.2.0
 */
@Builder
@Getter
@ToString
@Slf4j
public class PdfImagesTool implements XpdfTool<PdfImagesRequest, PdfImagesResponse> {

    /**
     * Executable file that should be invoked.
     *
     * @implNote If unassigned, this will default to the executable included with this project.
     * @since 1.2.0
     */
    private final File executableFile;

    /**
     * Maximum amount of time in seconds allotted to a process before timing out.
     *
     * @implNote If unassigned, this will default to 30 seconds.
     * @since 1.2.0
     */
    private final Integer timeoutSeconds;

    public static class PdfImagesToolBuilder {

        public PdfImagesTool build() {
            val executableFileBuilder = configureExecutableFile();
            val timeoutSecondsBuilder = configureTimeoutSeconds();

            return new PdfImagesTool(executableFileBuilder, timeoutSecondsBuilder);
        }

        protected File configureExecutableFile() {
            if (executableFile == null) {
                // copy executable from project resources to OS-accessible directory on local system
                val executablePath = getPdfImagesExecutablePath();
                if (!executablePath.toFile().exists()) {
                    val executableResourceStream = getClass().getClassLoader().getResourceAsStream(getPdfImagesExecutableResourceName());
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
                return getPdfImagesTimeoutSeconds();
            } else {
                return timeoutSeconds;
            }
        }
    }

    /**
     * Extracts and saves images from a PDF file.
     *
     * <br><br> Invokes the <em>pdfimages</em> executable against a PDF file.
     * This executable extracts and saves images from a PDF file.
     *
     * <br><br> This method utilizes Java's {@link ProcessBuilder} API to execute a shell command that launches the executable.
     * Be aware that this method may become blocking for very large PDF files.
     *
     * @param request {@link PdfImagesRequest}
     * @return {@link PdfImagesResponse} with list of image files extracted from PDF
     * @throws XpdfValidationException if request is invalid
     * @throws XpdfExecutionException if process returns non-zero exit code
     * @throws XpdfTimeoutException if process duration exceeds timeout length
     * @throws XpdfProcessingException if any other exception occurs during processing
     * @implNote This method executes a shell command and may become blocking.
     * @since 1.2.0
     */
    @Override
    public PdfImagesResponse process(PdfImagesRequest request) throws XpdfException {
        log.debug("Process starting");
        Process process = null;

        try {
            // validate request
            log.debug("Validating request");
            validate(request);

            // configure output image file path prefix
            log.debug("Configuring output image file path prefix");
            val imageFilePathPrefix = initializeImageFilePathPrefix(request);

            // get commands
            log.debug("Building command");
            val commandParts = getCommandParts(request, imageFilePathPrefix);

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
                    return PdfImagesResponse.builder()
                            .imageFiles(getImageFilesMatchingPathPrefix(imageFilePathPrefix))
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
     * Validates a {@link PdfImagesRequest}.
     *
     * @param request {@link PdfImagesRequest}
     * @throws XpdfValidationException if request is invalid
     */
    protected void validate(PdfImagesRequest request) throws XpdfValidationException {
        if (request == null) {
            throw new XpdfValidationException("PdfImagesRequest cannot be null");
        }

        // check pdf file exists
        if (!request.getPdfFile().exists()) {
            throw new XpdfValidationException("PdfFile does not exist");
        }

        // check valid image file path prefix
        if (request.getImageFilePathPrefix() != null) {
            try {
                request.getImageFilePathPrefix().toFile().getCanonicalPath();
            } catch (IOException e) {
                throw new XpdfValidationException("Invalid path given for ImageFilePathPrefix");
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
     * Gets the path prefix that the executable should write image files to.
     *
     * @param request {@link PdfImagesRequest}
     * @return image file path prefix
     */
    protected Path initializeImageFilePathPrefix(PdfImagesRequest request) {
        final Path imageFilePathPrefix;
        if (request.getImageFilePathPrefix() != null) {
            // use image file path prefix provided in request
            imageFilePathPrefix = request.getImageFilePathPrefix();
        } else {
            // create image file path prefix
            imageFilePathPrefix = getPdfImagesTempOutputPath().resolve(UUID.randomUUID().toString());
            // auto-delete image directory
            imageFilePathPrefix.getParent().toFile().deleteOnExit();
        }

        // create directories on path if not existing
        imageFilePathPrefix.getParent().toFile().mkdirs();

        return imageFilePathPrefix;
    }

    /**
     * Gets the complete list of command parts for a {@code Process}.
     *
     * @param request {@link PdfImagesRequest}
     * @param imageFilePathPrefix {@link Path}
     * @return command parts as {@code List<String>}
     * @throws IOException if canonical path of {@link #executableFile} or imageFilePathPrefix is invalid
     */
    protected List<String> getCommandParts(PdfImagesRequest request, Path imageFilePathPrefix) throws IOException {
        val commandParts = new ArrayList<String>();

        commandParts.add(executableFile.getCanonicalPath());
        commandParts.addAll(getCommandOptions(request.getOptions()));
        commandParts.add(request.getPdfFile().getCanonicalPath());
        commandParts.add(imageFilePathPrefix.toFile().getCanonicalPath());

        return commandParts;
    }

    /**
     * Gets the command options to invoke the executable with.
     *
     * @param options {@link PdfImagesOptions}
     * @return command options as {@code List<String>}
     */
    protected List<String> getCommandOptions(PdfImagesOptions options) {
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

        val fileFormat = options.getFileFormat();
        if (fileFormat != null) {
            switch (fileFormat) {
                case JPEG:
                    args.add("-j");
                    break;
                case RAW:
                    args.add("-raw");
                    break;
                default:
                    throw new XpdfRuntimeException(String.format("File format case %s is missing from command options switch statement", fileFormat.name()));
            }
        }

        val metadataIncluded = options.getMetadataIncluded();
        if (Boolean.TRUE.equals(metadataIncluded)) {
            args.add("-list");
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

    /**
     * Gets the image files matching the path prefix.
     *
     * @param imageFilePathPrefix {@link Path}
     * @return image files as {@code List<File>}
     */
    protected List<File> getImageFilesMatchingPathPrefix(Path imageFilePathPrefix) {
        val imageDirectory = imageFilePathPrefix.getParent().toFile();
        val imageFileNamePrefix = imageFilePathPrefix.getFileName().toString();
        return asList(requireNonNull(
                imageDirectory.listFiles((directory, name) -> name.matches(imageFileNamePrefix + "-[0-9]+\\..+"))
        ));
    }

}
