package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

/**
 * Represents the result of invoking the native <em>pdftotext</em> library.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
public class PdfTextResponse {

    //todo: in user guide, you should give examples of how they should read text from this file depending on encoding...
    //      if they chose LATIN_1, ASCII_7, UTF_8, or ZAPF_DINGBATS -> then StandardCharsets.UTF_8
    //      if they chose UCS_2 -> then  StandardCharsets.UTF_16
    //      if they chose SYMBOL -> GOOD LUCK LOL ("extended" ASCII, which has no built-in java charset)
    /**
     * Text file containing text extracted from input PDF file.
     *
     * @since 4.4.0
     */
    private final File textFile;

    /**
     * Standard output streamed from native process.
     *
     * @since 4.4.0
     */
    private final String standardOutput;

}
