package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

/**
 * Contains the {@code PdfTextTool} process output.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
public class PdfTextResponse {

    /**
     * Text {@code File} containing the text extracted from input PDF {@code File}.
     *
     * @since 4.4.0
     */
    private File textFile;

    /**
     * Standard output streamed from execution of <em>pdftotext</em> command.
     *
     * @since 4.4.0
     */
    private String standardOutput;

}
