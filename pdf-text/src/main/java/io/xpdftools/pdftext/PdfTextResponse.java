package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

/**
 * Contains the output from native <em>pdftotext</em> process.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
public class PdfTextResponse {

    /**
     * Text file containing the text extracted from input PDF file.
     *
     * @since 4.4.0
     */
    private final File textFile;

    /**
     * Standard output streamed from native <em>pdftotext</em> process.
     *
     * @since 4.4.0
     */
    private final String standardOutput;

}
