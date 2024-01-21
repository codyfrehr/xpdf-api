package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;

/**
 * Contains the command options to include when invoking the native <em>pdftotext</em> library.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfTextOptions.builder()
 *      .format(PdfTextFormat.RAW)
 *      .encoding(PdfTextEncoding.UTF_8)
 *      .ownerPassword("Secret123")
 *      .build();
 * </pre></blockquote>
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
public class PdfTextOptions {
    //todo: should we implement sensible default values?
    //      probably better than leaving the decision to xpdf binaries, right..?
    //      also, will make it more clear to end user whats happening...
    //todo: understand if 0 based and include info in implNote
    //todo: default value? (+ implNote on default value)
    /**
     * First page to include in text output.
     *
     * @implNote Must be positive.
     * @since 4.4.0
     */
    private final Integer pageStart;

    /**
     * Last page to include in text output.
     *
     * @implNote Must be positive and greater than or equal to {@link #pageStart}.
     * @since 4.4.0
     */
    private final Integer pageEnd;

    /**
     * Format, or structure, to give text output.
     *
     * @since 4.4.0
     */
    private final PdfTextFormat format;

    /**
     * Encoding to use for text output.
     *
     * @since 4.4.0
     */
    private final PdfTextEncoding encoding;

    /**
     * End-of-line convention to use for text output.
     *
     * @since 4.4.0
     */
    private final PdfTextEndOfLine endOfLine;

    /**
     * Flag to include new page characters in output text.
     *
     * @implNote Default is {@code true}.
     * @since 4.4.0
     */
    @Builder.Default
    private final boolean pageBreakIncluded = true;

    /**
     * Owner password for encrypted PDF files.
     *
     * @since 4.4.0
     */
    private final String ownerPassword;

    /**
     * User password for encrypted PDF files.
     *
     * @since 4.4.0
     */
    private final String userPassword;

}
