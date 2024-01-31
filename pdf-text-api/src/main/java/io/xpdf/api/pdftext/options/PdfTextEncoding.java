package io.xpdf.api.pdftext.options;

//todo: what does this mean exactly? the encoding of the text in the PDF? or the encoding to apply to text output? or do both of those mean the same thing..?
//      think about it in terms of the fonts...
//      a special encoding is needed to interpret ZapfDingbats...
//      but does that mean the output text is "encoded" that way too? and how does my computer (or the application reading text?) understand that output?
/**
 * Defines the encoding of text extracted from a PDF file.
 *
 * @implNote Encodings defined here are those included with native <em>pdftotext</em> library.
//todo: fix how you phrase implNote below, after you implement global xpdfrc configs
//todo: add similar note for EOL option and other options classes
 * Other encodings can be configured via <em>xpdfrc</em> config file.
 * @since 1.0.0
 */
public enum PdfTextEncoding {

    /**
     * Latin alphabet (No. 1) Unicode character set.
     *
     * @implNote Use {@link #UTF_8}, unless you specifically intend to limit the scope of encoding.
     * @since 1.0.0
     */
    LATIN_1,

    /**
     * 7-bit ASCII character set.
     *
     * @implNote Use {@link #UTF_8}, unless you specifically intend to limit the scope of encoding.
     * @since 1.0.0
     */
    ASCII_7,

    /**
     * 8-bit Unicode character set.
     *
     * @implNote Backward compatible with {@link #LATIN_1}, {@link #ASCII_7}, and {@link #ZAPF_DINGBATS}.
     * @since 1.0.0
     */
    UTF_8,

    /**
     * 2-bit Unicode character set.
     *
     * @implNote Predecessor to UTF-16 encoding.
     * @since 1.0.0
     */
    UCS_2,

    /**
     * "Extended" ASCII character set for encoding the Symbol font.
     *
     * @implNote Warning: This extension of the ASCII character set is a legacy encoding that is rarely used.
     * The Symbol font and its encoding were adopted by PostScript, but never became part of any official encoding standard.
     * @since 1.0.0
     */
    SYMBOL,

    /**
     * ZapfDingbats Unicode character set.
     *
     * @implNote Use {@link #UTF_8}, unless you specifically intend to limit the scope of encoding.
     * @since 1.0.0
     */
    ZAPF_DINGBATS,

}
