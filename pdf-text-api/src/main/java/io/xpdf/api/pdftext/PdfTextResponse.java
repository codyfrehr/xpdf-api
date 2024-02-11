/*
 * PdfText API - An API for accessing a native pdftotext library.
 * Copyright © 2024 xpdf.io (cfrehr@gmail.com)
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
package io.xpdf.api.pdftext;

import io.xpdf.api.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.File;

/**
 * Represents the result of invoking a native <em>pdftotext</em> library.
 *
 * @since 1.0.0
 */
@Builder
@Getter
@ToString
public class PdfTextResponse extends XpdfResponse {

    /**
     * Text file containing text extracted from input PDF file.
     *
     * @implNote If this is a temporary file, it will be automatically deleted on JVM termination.
     * @since 1.0.0
     */
    private final File textFile;

    /**
     * Standard output streamed from native process.
     *
     * @since 1.0.0
     */
    private final String standardOutput;

}
