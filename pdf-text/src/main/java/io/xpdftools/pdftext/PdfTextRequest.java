package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;

/**
 * Contains the {@code PdfTextTool} process input.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfTextRequest.builder()
 *      .pdfFile(new File("/home/cody/docs/some.pdf"))
 *      .textFile(new File("/home/cody/docs/some.txt"))
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
public class PdfTextRequest {

    /**
     * Input PDF {@code File}.
     *
     * @implNote Required.
     * @since 4.4.0
     */
    @NonNull
    private final File pdfFile;

    /**
     * Output text {@code File}.
     *
     * @implNote If {@code null}, a temporary {@code File} will get created.
     * @since 4.4.0
     */
    private final File textFile;

    /**
     * Command options to customize processing.
     *
     * @since 4.4.0
     */
    private PdfTextOptions options;
}
