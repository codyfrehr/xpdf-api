package io.xpdf.api.pdfimages.util;

import io.xpdf.api.common.util.XpdfUtils;
import io.xpdf.api.pdfimages.PdfImagesTool;

import java.nio.file.Path;

/**
 * Helpers for a {@link PdfImagesTool}.
 *
 * @since 1.2.0
 */
public class PdfImagesUtils {

    private PdfImagesUtils() {
    }

    /**
     * Gets the resource name of the <em>pdfimages</em> executable native to this system.
     *
     * @return executable resource name
     * @since 1.2.0
     */
    public static String getPdfImagesExecutableResourceName() {
        return String.format("xpdf/%s/%s", XpdfUtils.getTargetSystem(), getPdfImagesExecutableName());
    }

    /**
     * Gets the path where the native <em>pdfimages</em> executable should be copied so that it can be accessed by OS.
     *
     * @return executable path
     * @since 1.2.0
     */
    public static Path getPdfImagesExecutablePath() {
        return XpdfUtils.getXpdfTempPath().resolve("pdf-images").resolve("bin").resolve(getPdfImagesExecutableName());
    }

    /**
     * Gets the temporary directory where the <em>pdfimages</em> executable should write output.
     *
     * @return temporary directory
     * @since 1.2.0
     */
    public static Path getPdfImagesTempOutputPath() {
        return XpdfUtils.getXpdfTempPath().resolve("pdf-images").resolve("out");
    }

    /**
     * Gets the maximum amount of time in seconds allotted to the <em>pdfimages</em> process before timing out.
     *
     * @return timeout length in seconds for process
     * @since 1.2.0
     */
    public static Integer getPdfImagesTimeoutSeconds() {
        return 30;
    }

    /**
     * Gets the name of the native <em>pdfimages</em> executable.
     *
     * @return executable name
     */
    protected static String getPdfImagesExecutableName() {
        return String.format("pdfimages%s", XpdfUtils.getTargetSystem().contains("windows") ? ".exe" : "");
    }

}
