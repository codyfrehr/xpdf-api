package io.xpdf.api.pdfinfo.options;

/**
 * Defines the encoding to use for text output from a processed PDF file.
 *
 * @implNote Encodings defined here are those included with a native <em>pdfinfo</em> executable.
 * Additional encodings can be configured in an <em>xpdfrc</em> config file, which you can then pass as a command option.
 * See <em>Xpdf</em> documentation for more detailed information about configuration.
 * You can find source documentation alongside the executable file in the package resources.
 * @since 1.0.0
 */
public enum PdfInfoEncoding {

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
