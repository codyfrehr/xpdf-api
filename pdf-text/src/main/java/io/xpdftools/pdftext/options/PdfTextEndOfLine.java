package io.xpdftools.pdftext.options;

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
