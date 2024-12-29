package io.xpdf.api.pdfinfo;

import io.xpdf.api.common.XpdfRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.File;

/**
 * Represents a command to invoke the <em>pdfinfo</em> executable with.
 *
 * <br><br> Example usage:
 * <blockquote><pre>
 *  PdfInfoRequest.builder()
 *      .pdfFile(new File("~/docs/some.pdf"))
 *      .options(PdfInfoOptions.builder().encoding(PdfInfoEncoding.UTF_8).build())
 *      .build();
 * </pre></blockquote>
 *
 * @implNote A {@code NullPointerException} will be thrown if the required arguments are not provided to the builder.
 * @since 1.0.0
 */
@Builder
@Getter
@ToString
public class PdfInfoRequest extends XpdfRequest {

    /**
     * Input PDF file.
     *
     * @implNote Required.
     * @since 1.0.0
     */
    @NonNull
    private final File pdfFile;

    /**
     * Command options to customize execution.
     *
     * @since 1.0.0
     */
    private final PdfInfoOptions options;

}
