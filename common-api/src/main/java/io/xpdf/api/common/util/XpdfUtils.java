package io.xpdf.api.common.util;

import io.xpdf.api.common.XpdfTool;
import io.xpdf.api.common.exception.XpdfRuntimeException;
import lombok.val;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helpers for a {@link XpdfTool}.
 *
 * @since 1.0.0
 */
public class XpdfUtils {

    /**
     * Gets the resource name of the <em>pdftotext</em> library native to this system.
     *
     * @return native library resource name
     * @since 1.0.0
     */
    static public String getPdfTextNativeLibraryResourceName() {
        return String.format("xpdf/%s/%s", getTargetSystem(), getPdfTextNativeLibraryName());
    }

    /**
     * Gets the path where the native <em>pdftotext</em> library should be copied so that it can be accessed by OS.
     *
     * @return native library path
     * @since 1.0.0
     */
    static public Path getPdfTextNativeLibraryPath() {
        return getXpdfTempPath().resolve("pdf-text").resolve("bin").resolve(getPdfTextNativeLibraryName());
    }

    /**
     * Gets the temporary directory where the native <em>pdftotext</em> library should write output.
     *
     * @return temporary directory
     * @since 1.0.0
     */
    static public Path getPdfTextTempOutputPath() {
        return getXpdfTempPath().resolve("pdf-text").resolve("out");
    }

    /**
     * Gets the maximum amount of time in seconds allotted to the native <em>pdftotext</em> process before timing out.
     *
     * @return timeout length in seconds for native process
     * @since 1.0.0
     */
    static public Integer getPdfTextTimeoutSeconds() {
        return 30;
    }

    /**
     * Gets the name of the native <em>pdftotext</em> library.
     *
     * @return native library name
     */
    static protected String getPdfTextNativeLibraryName() {
        return String.format("pdftotext%s", getTargetSystem().contains("windows") ? ".exe" : "");
    }

    /**
     * Gets the temporary directory utilized by native <em>Xpdf</em> libraries.
     *
     * @return temporary directory
     */
    static protected Path getXpdfTempPath() {
        return Paths.get(System.getProperty("java.io.tmpdir")).resolve( "xpdf-api");
    }

    /**
     * Gets the OS and JVM bit architecture specific to this system.
     * This helps to locate the correct <em>Xpdf</em> library for this system in the project resources.
     *
     * @return representation of OS and bits
     */
    static protected String getTargetSystem() {
        // get JVM bit architecture
        val bit = System.getProperty("sun.arch.data.model");

        if ("UNKNOWN".equals(bit)) {
            throw new XpdfRuntimeException("Unexpected error getting JVM bit architecture");
        }

        // get operating system name
        val os = System.getProperty("os.name");

        if (os == null) {
            throw new XpdfRuntimeException("Unexpected error getting operating system name");
        }

        if (os.startsWith("Windows")) {
            return "64".equals(bit) ? "windows-64" : "windows-32";
        } else if (os.startsWith("Linux")) {
            return "64".equals(bit) ? "linux-64" : "linux-32";
        } else if (os.startsWith("Mac")) {
            if ("64".equals(bit)) {
                return "mac-64";
            } else {
                throw new XpdfRuntimeException("XpdfTools can only be run against 64-bit JVM on Mac operating system");
            }
        } else {
            throw new XpdfRuntimeException("XpdfTools can only be run on Windows, Linux, or Mac operating systems");
        }
    }

}
