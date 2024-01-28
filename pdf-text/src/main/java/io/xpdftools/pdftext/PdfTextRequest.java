package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.File;

/**
 * Represents the command to invoke the native <em>pdftotext</em> process.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfTextRequest.builder()
 *      .pdfFile(Paths.get("C:/docs/some.pdf"))
 *      .textFile(Paths.get("C:/docs/some.txt"))
 *      .options(PdfTextOptions.builder().format(PdfTextFormat.RAW).build())
 *      .build();
 * </pre></blockquote>
 *
 * @implNote A {@code NullPointerException} will be thrown if the required arguments are not provided to the builder.
 * @author Cody Frehr
 * @since 4.4.0
 */
@Builder
@Getter
@ToString
public class PdfTextRequest {

    /**
     * Input PDF file.
     *
     * @implNote Required.
     * @since 4.4.0
     */
    @NonNull
    private final File pdfFile;

    /**
     * Output text file.
     *
     * @implNote If unassigned, a temporary file will get created and automatically deleted on JVM termination.
     * @since 4.4.0
     */
    private final File textFile;

    /**
     * Command options to customize native process.
     *
     * @since 4.4.0
     */
    private final PdfTextOptions options;

}
