/*
 * PdfText API - An API for accessing a native pdftotext library (https://xpdf.io)
 * Copyright © 2024 xpdf.io (info@xpdf.io)
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
package io.xpdf.api.pdftext.options;

/**
 * Defines the line-ending characters that should be applied to text extracted from a PDF file.
 *
 * @since 1.0.0
 */
public enum PdfTextEndOfLine {

    /**
     * "{@code \r\n}" - line-ending characters on Windows operating systems.
     *
     * @since 1.0.0
     */
    DOS,

    /**
     * "{@code \r}" - line-ending characters on Mac operating systems.
     *
     * @since 1.0.0
     */
    MAC,

    /**
     * "{@code \n}" - line-ending characters on Unix operating systems.
     *
     * @since 1.0.0
     */
    UNIX,

}
