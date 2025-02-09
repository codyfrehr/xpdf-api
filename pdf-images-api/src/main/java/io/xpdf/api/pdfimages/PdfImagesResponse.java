package io.xpdf.api.pdfimages;

import io.xpdf.api.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.List;

/**
 * Represents the result of invoking the <em>pdfimages</em> executable.
 *
 * @since 1.2.0
 */
@Builder
@Getter
@ToString
public class PdfImagesResponse extends XpdfResponse {

    /**
     * List of image files extracted from input PDF.
     *
     * @implNote If these files are in a temporary directory, then they will be automatically deleted on JVM termination.
     * @since 1.2.0
     */
    private final List<File> imageFiles;

    /**
     * Standard output from the shell process that invoked the executable.
     *
     * @since 1.2.0
     */
    private final String standardOutput;

}
