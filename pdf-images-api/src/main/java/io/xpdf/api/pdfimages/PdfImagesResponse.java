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

import io.xpdf.api.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.List;

/**
 * Represents the result of invoking the <em>pdfimages</em> executable.
 *
 * @since 1.2.0
 */
@Builder
@Getter
@ToString
public class PdfImagesResponse extends XpdfResponse {

    /**
     * List of image files extracted from input PDF.
     *
     * @implNote If these files are in a temporary directory, then they will be automatically deleted on JVM termination.
     * @since 1.2.0
     */
    private final List<File> imageFiles;

    /**
     * Standard output from the shell process that invoked the executable.
     *
     * @since 1.2.0
     */
    private final String standardOutput;

}
