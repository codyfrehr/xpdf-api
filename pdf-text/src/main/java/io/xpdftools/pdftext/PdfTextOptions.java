package io.xpdftools.pdftext;

import io.xpdftools.pdftext.options.PdfTextEncoding;
import io.xpdftools.pdftext.options.PdfTextEndOfLine;
import io.xpdftools.pdftext.options.PdfTextFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * Represents the command options to invoke with the native <em>pdftotext</em> library.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfTextOptions.builder()
 *      .format(PdfTextFormat.RAW)
 *      .encoding(PdfTextEncoding.UTF_8)
 *      .ownerPassword("Secret123")
 *      .nativeOptions(Map.ofEntries(Map.entry("-cfg", "C:/config/xpdfrc")))
 *      .build();
 * </pre></blockquote>
 *
 * @implNote Not all options specified by the native <em>pdftotext</em> library have been implemented by this class.
 * See {@link #nativeOptions} for a complete list of options.
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
@ToString
public class PdfTextOptions {

    /**
     * Page number of PDF file to begin text extraction from.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -f <number>}
     * </pre></blockquote>
     *
     * @implNote Must be positive.
     * If unassigned, native library starts from first page.
     * @since 4.4.0
     */
    private final Integer pageStart;

    /**
     * Page number of PDF file to end text extraction on.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -l <number>}
     * </pre></blockquote>
     *
     * @implNote Must be positive and greater than or equal to {@link #pageStart}.
     * If unassigned, native library stops on last page.
     * @since 4.4.0
     */
    private final Integer pageStop;

    /**
     * Format, or structure, of text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -layout}
     *  {@literal -simple}
     *  {@literal -simple2}
     *  {@literal -table}
     *  {@literal -lineprinter}
     *  {@literal -raw}
     * </pre></blockquote>
     *
     * @implNote If unassigned, native library outputs text in reading order.
     * @since 4.4.0
     */
    private final PdfTextFormat format;

    /**
     * Encoding of text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -enc <string>}
     * </pre></blockquote>
     *
     * @implNote If unassigned, native library defaults to {@link PdfTextEncoding#LATIN_1}.
     * @since 4.4.0
     */
    private final PdfTextEncoding encoding;

    /**
     * Line-ending convention for text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -eol <string>}
     * </pre></blockquote>
     *
     * @implNote If unassigned, native library uses line-ending convention matching OS it targets.
     * @since 4.4.0
     */
    private final PdfTextEndOfLine endOfLine;

    /**
     * Flag to exclude new page characters from text output.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -nopgbrk}
     * </pre></blockquote>
     *
     * @implNote If unassigned, native library includes page breaks.
     * @since 4.4.0
     */
    private final Boolean pageBreakExcluded;

    /**
     * Owner password for encrypted PDF file.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -opw <string>}
     * </pre></blockquote>
     *
     * @since 4.4.0
     */
    private final String ownerPassword;

    /**
     * User password for encrypted PDF file.
     *
     * <br><br> Native options:
     * <blockquote><pre>
     *  {@literal -upw <string>}
     * </pre></blockquote>
     *
     * @since 4.4.0
     */
    private final String userPassword;

    /**
     * Map of options to include directly in the invocation of the native library.
     *
     * <p> Useful for including options that have not been implemented by this class.
     * Each entry in the map should represent a native option, where the key is the option and the value is the parameter associated with that option.
     * If the native option has no associated parameter, then just map it to a null or empty string.
     *
     * <br><br> Example usage:
     * <blockquote><pre>
     *  PdfTextOptions.builder()
     *      .nativeOptions(Map.ofEntries(
     *          Map.entry("-raw", null),
     *          Map.entry("-enc", "UTF-8"),
     *          Map.entry("-opw", "Secret123")))
     *      .build();
     * </pre></blockquote>
     *
     * Native options:
     * <blockquote><pre>
     *  {@literal -f <int>               : first page to convert}
     *  {@literal -l <int>               : last page to convert}
     *  {@literal -layout                : maintain original physical layout}
     *  {@literal -simple                : simple one-column page layout}
     *  {@literal -simple2               : simple one-column page layout, version 2}
     *  {@literal -table                 : similar to -layout, but optimized for tables}
     *  {@literal -lineprinter           : use strict fixed-pitch/height layout}
     *  {@literal -raw                   : keep strings in content stream order}
     *  {@literal -fixed <number>        : assume fixed-pitch (or tabular) text}
     *  {@literal -linespacing <number>  : fixed line spacing for LinePrinter mode}
     *  {@literal -clip                  : separate clipped text}
     *  {@literal -nodiag                : discard diagonal text}
     *  {@literal -enc <string>          : output text encoding name}
     *  {@literal -eol <string>          : output end-of-line convention (unix, dos, or mac)}
     *  {@literal -nopgbrk               : don't insert a page break at the end of each page}
     *  {@literal -bom                   : insert a Unicode BOM at the start of the text file}
     *  {@literal -marginl <number>      : left page margin}
     *  {@literal -marginr <number>      : right page margin}
     *  {@literal -margint <number>      : top page margin}
     *  {@literal -marginb <number>      : bottom page margin}
     *  {@literal -opw <string>          : owner password (for encrypted files)}
     *  {@literal -upw <string>          : user password (for encrypted files)}
     *  {@literal -verbose               : print per-page status information}
     *  {@literal -q                     : don't print any messages or errors}
     *  {@literal -cfg <string>          : configuration file to use in place of .xpdfrc}
     *  {@literal -listencodings         : list all available output text encodings}
     *  {@literal -v                     : print copyright and version info}
     *  {@literal -h                     : print usage information}
     *  {@literal -help                  : print usage information}
     *  {@literal --help                 : print usage information}
     *  {@literal -?                     : print usage information}
     * </pre></blockquote>
     *
     * @implNote No validation is performed against this input.
     * Any parameter values fed into an option entry will be automatically wrapped in quotes to ensure that the native library correctly interprets the command.
     * @since 4.4.0
     */
    private final Map<String, String> nativeOptions;

}
