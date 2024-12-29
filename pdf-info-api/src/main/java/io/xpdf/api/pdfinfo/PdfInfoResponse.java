package io.xpdf.api.pdfinfo;

import io.xpdf.api.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the result of invoking the <em>pdfinfo</em> executable.
 *
 * @since 1.0.0
 */
@Builder
@Getter
@ToString
public class PdfInfoResponse extends XpdfResponse {

    /**
     * Standard output from the shell process that invoked the executable.
     *
     * @since 1.0.0
     */
    private final String standardOutput;

}
