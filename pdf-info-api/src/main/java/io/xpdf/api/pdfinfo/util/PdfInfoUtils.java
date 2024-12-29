package io.xpdf.api.pdfinfo.util;

import io.xpdf.api.common.util.XpdfUtils;
import io.xpdf.api.pdfinfo.PdfInfoTool;

import java.nio.file.Path;

/**
 * Helpers for a {@link PdfInfoTool}.
 *
 * @since 1.0.0
 */
public class PdfInfoUtils {

    private PdfInfoUtils() {
    }

    /**
     * Gets the resource name of the <em>pdfinfo</em> executable native to this system.
     *
     * @return executable resource name
     * @since 1.0.0
     */
    public static String getPdfInfoExecutableResourceName() {
        return String.format("xpdf/%s/%s", XpdfUtils.getTargetSystem(), getPdfInfoExecutableName());
    }

    /**
     * Gets the path where the native <em>pdfinfo</em> executable should be copied so that it can be accessed by OS.
     *
     * @return executable path
     * @since 1.0.0
     */
    public static Path getPdfInfoExecutablePath() {
        return XpdfUtils.getXpdfTempPath().resolve("pdf-info").resolve("bin").resolve(getPdfInfoExecutableName());
    }

    /**
     * Gets the maximum amount of time in seconds allotted to the <em>pdfinfo</em> process before timing out.
     *
     * @return timeout length in seconds for process
     * @since 1.0.0
     */
    public static Integer getPdfInfoTimeoutSeconds() {
        return 30;
    }

    /**
     * Gets the name of the native <em>pdfinfo</em> executable.
     *
     * @return executable name
     */
    protected static String getPdfInfoExecutableName() {
        return String.format("pdfinfo%s", XpdfUtils.getTargetSystem().contains("windows") ? ".exe" : "");
    }

}
