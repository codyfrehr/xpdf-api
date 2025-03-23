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

import io.xpdf.api.common.XpdfRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.File;
import java.nio.file.Path;

/**
 * Represents a command to invoke the <em>pdfimages</em> executable with.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfImagesRequest.builder()
 *      .pdfFile(new File("~/docs/some.pdf"))
 *      .imageFilePathPrefix(Paths.get("~/docs/some-path-prefix"))
 *      .options(PdfImagesOptions.builder().fileFormat(PdfImagesFileFormat.JPEG).build())
 *      .build();
 * </pre></blockquote>
 *
 * @implNote A {@code NullPointerException} will be thrown if the required arguments are not provided to the builder.
 * @since 1.2.0
 */
@Builder
@Getter
@ToString
public class PdfImagesRequest extends XpdfRequest {

    /**
     * Input PDF file.
     *
     * @implNote Required.
     * @since 1.2.0
     */
    @NonNull
    private final File pdfFile;

    /**
     * Output image file path prefix.
     *
     * @implNote If unassigned, a temporary directory will get created for image files, and then automatically deleted at JVM termination.
     * @since 1.2.0
     */
    private final Path imageFilePathPrefix;

    /**
     * Command options to customize execution.
     *
     * @since 1.2.0
     */
    private final PdfImagesOptions options;

}
