package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;

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
     * PDF text.
     *
     * @implNote This value should be retrieved by reading the text file output by the <em>pdftotext</em> command.
     * @since 4.4.0
     */
    private String pdfText;

    /**
     * Standard output streamed from execution of <em>pdftotext</em> command.
     *
     * @since 4.4.0
     */
    private String stdOut;

}
