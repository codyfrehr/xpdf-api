package io.xpdftools.pdftext;

import io.xpdftools.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.File;

/**
 * Represents the result of invoking a native <em>pdftotext</em> library.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
@ToString
public class PdfTextResponse extends XpdfResponse {

    /**
     * Text file containing text extracted from input PDF file.
     *
     * @implNote If this is a temporary file, it will be automatically deleted on JVM termination.
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
