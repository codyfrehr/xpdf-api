/*
 * Xpdf API - A collection of APIs for interacting with native Xpdf libraries.
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
 * Defines how text extracted from a PDF file should be formatted, or structured.
 *
 * @since 1.0.0
 */
public enum PdfTextFormat {

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Maintain (as best as possible) the original physical layout of the text.
     *  The default is to 'undo' physical layout (columns, hyphenation, etc.) and
     *  output the text in reading order. If the -fixed option is given, character
     *  spacing within each line will be determined by the specified character pitch.
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    LAYOUT,

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Similar to -layout, but optimized for simple one-column pages. This mode will do
     *  a better job of maintaining horizontal spacing, but it will only work properly
     *  with a single column of text.
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    SIMPLE,

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Similar to -simple, but handles slightly rotated text (e.g., OCR output) better.
     *  Only works for pages with a single column of text.
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    SIMPLE_2,

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Table mode is similar to physical layout mode, but optimized for tabular data, with the
     *  goal of keeping rows and columns aligned (at the expense of inserting extra whitespace).
     *  If the -fixed option is given, character spacing within each line will be determined by
     *  the specified character pitch.
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    TABLE,

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Line printer mode uses a strict fixed-character-pitch and -height layout. That is,
     *  the page is broken into a grid, and characters are placed into that grid. If the
     *  grid spacing is too small for the actual characters, the result is extra white-space.
     *  If the grid spacing is too large, the result is missing whitespace. The grid spacing
     *  can be specified using the -fixed and -linespacing options. If one or both are not
     *  given on the command line, pdftotext will attempt to compute appropriate value(s).
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    LINE_PRINTER,

    /**
     * From <em>pdftotext</em> documentation:
     * <blockquote><pre>
     *  Keep the text in content stream order. Depending on how the PDF file was generated,
     *  this may or may not be useful.
     * </pre></blockquote>
     *
     * @since 1.0.0
     */
    RAW,

}
