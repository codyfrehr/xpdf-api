package io.xpdftools.common.util;

import io.xpdftools.common.exception.XpdfRuntimeException;
import lombok.val;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helpers for a {@code XpdfTool}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
public class XpdfUtils {

    /**
     * Gets the resource name of the pdftotext library native to this system.
     *
     * @return the resource name as {@code String}
     * @since 4.4.0
     */
    static public String getPdfTextResourceName() {
        return String.format("xpdf/%s/%s", getTargetSystem(), getPdfTextLibraryName());
    }

    /**
     * Gets the {@code Path} where the pdftotext library should be copied so that it can be accessed by OS.
     *
     * @return the {@code Path} to copy resource
     * @since 4.4.0
     */
    static public Path getPdfTextLocalPath() {
        return getXpdfTempPath().resolve("pdf-text").resolve("bin").resolve(getPdfTextLibraryName());
    }

    /**
     * Gets the {@code Path} where the pdftotext library should write output.
     *
     * @return the {@code Path} to write output
     * @since 4.4.0
     */
    static public Path getPdfTextOutPath() {
        return getXpdfTempPath().resolve("pdf-text").resolve("out");
    }

    /**
     * Gets the name of the pdftotext library.
     *
     * @return the library name as {@code String}
     * @since 4.4.0
     */
    static protected String getPdfTextLibraryName() {
        return String.format("pdftotext%s", getTargetSystem().contains("windows") ? ".exe" : "");
    }

    /**
     * Gets the temporary directory utilized by Xpdf libraries.
     *
     * @return the directory {@code Path}
     * @since 4.4.0
     */
    static protected Path getXpdfTempPath() {
        return Paths.get(System.getProperty("java.io.tmpdir")).resolve( "xpdf-tools");
    }

    /**
     * Gets the OS and JVM bit architecture specific to this system.
     * This helps to locate the correct Xpdf library for this system in the project resources.
     *
     * @return a representation of OS and bits as {@code String}
     * @since 4.4.0
     */
    static protected String getTargetSystem() {
        // get JVM bit architecture
        val bit = System.getProperty("sun.arch.data.model");

        //todo: throw different exception type?
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
