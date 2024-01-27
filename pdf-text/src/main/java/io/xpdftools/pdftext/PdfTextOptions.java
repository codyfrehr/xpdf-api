package io.xpdftools.pdftext;

import io.xpdftools.pdftext.options.PdfTextEncoding;
import io.xpdftools.pdftext.options.PdfTextEndOfLine;
import io.xpdftools.pdftext.options.PdfTextFormat;
import lombok.Builder;
import lombok.Getter;

//todo: implement custom xpdfrc config file
//      add @implNote about how some options may be globally configured with xpdfrc config, instead of manually declaring in each request
//todo: implement an additional property that allows custom commands args to be inserted.
//      include @implNote about this, and include it in the javadoc example.
/**
 * Represents the command options to invoke with the native <em>pdftotext</em> library.
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

    /**
     * Page number of PDF file to begin text extraction from.
     *
     * @implNote Must be positive.
     * If unassigned, native library starts from first page.
     * @since 4.4.0
     */
    private final Integer pageStart;

    /**
     * Page number of PDF file to end text extraction on.
     *
     * @implNote Must be positive and greater than or equal to {@link #pageStart}.
     * If unassigned, native library stops on last page.
     * @since 4.4.0
     */
    private final Integer pageStop;

    /**
     * Format, or structure, of text output.
     *
     * @implNote If unassigned, native library outputs text in reading order.
     * @since 4.4.0
     */
    private final PdfTextFormat format;

    //todo: in user docs website, you should recommend using UTF-8, since most other encodings are backwards compatible
    /**
     * Encoding of text output.
     *
     * @implNote If unassigned, native library defaults to {@link PdfTextEncoding#LATIN_1}.
     * @since 4.4.0
     */
    private final PdfTextEncoding encoding;

    /**
     * Line-ending convention for text output.
     *
     * @implNote If unassigned, native library uses line-ending convention matching OS it targets.
     * @since 4.4.0
     */
    private final PdfTextEndOfLine endOfLine;

    /**
     * Flag to exclude new page characters from text output.
     *
     * @implNote If unassigned, native library includes page breaks.
     * @since 4.4.0
     */
    private final Boolean pageBreakExcluded;

    /**
     * Owner password for encrypted PDF file.
     *
     * @since 4.4.0
     */
    private final String ownerPassword;

    /**
     * User password for encrypted PDF file.
     *
     * @since 4.4.0
     */
    private final String userPassword;

}
