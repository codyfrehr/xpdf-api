package io.xpdftools.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helpers for {@code XpdfTool}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Getter
@RequiredArgsConstructor
public class XpdfUtils {

    /**
     * OS and JVM bit architecture specific to this system.
     * This determines the correct Xpdf libraries to use, so that the correct system is targeted.
     * @since 4.4.0
     */
    private static final String TARGET_SYSTEM;

    /**
     * Combinations of OS and JVM bit architecture that Xpdf libraries can target.
     * These values correspond to the location of native libraries in the project resources.
     * @since 4.4.0
     */
    private static final String LINUX_32 = "linux-32";
    private static final String LINUX_64 = "linux-64";
    private static final String MAC_64 = "mac-64";
    private static final String WINDOWS_32 = "windows-32";
    private static final String WINDOWS_64 = "windows-64";

    /**
     * Names of the Xpdf libraries.
     * @since 4.4.0
     */
    private static final String PDF_TEXT_BIN_NAME;

    /**
     * Resource names of the Xpdf libraries.
     * @since 4.4.0
     */
    private static final String PDF_TEXT_RESOURCE_NAME;

    /**
     * {@code Path} where Xpdf resources should be copied so that they can be accessed by OS.
     * @since 4.4.0
     */
    private static final Path PDF_TEXT_BIN_TMP_PATH;

    /**
     * Various temporary directories utilized by a {@code XpdfTool}.
     * @since 4.4.0
     */
    private static final Path TMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "xpdf-tools");
    private static final Path TMP_DIR_BIN = TMP_DIR.resolve("bin");
    private static final Path TMP_DIR_OUT_PDF_TEXT = TMP_DIR.resolve("pdf-text");

    static {
        TARGET_SYSTEM = getTargetSystem();
        PDF_TEXT_BIN_NAME = String.format("%s%s", "pdftotext", TARGET_SYSTEM.contains("windows") ? ".exe" : "");
        PDF_TEXT_RESOURCE_NAME = String.format("xpdf/%s/%s", TARGET_SYSTEM, PDF_TEXT_BIN_NAME);
        PDF_TEXT_BIN_TMP_PATH = TMP_DIR_BIN.resolve(PDF_TEXT_BIN_NAME);
    }

    /**
     * Gets the OS and JVM bit architecture that a {@code XpdfTool} should target.
     *
     * @return a string representation of OS and bits
     * @throws XpdfRuntimeException if incompatible OS or JVM bit architecture
     * @since 4.4.0
     */
    private static String getTargetSystem() {
        // get JVM bit architecture
        val bit = System.getProperty("sun.arch.data.model");

        //todo: throw different exception type?
        if ("UNKNOWN".equals(bit)) {
            throw new XpdfRuntimeException("Unexpected error getting JVM bit architecture");
        }

        //todo: use "Platform.isWindows()" instead, if/when JNA dependency added
        if (SystemUtils.IS_OS_WINDOWS) {
            return "64".equals(bit) ? WINDOWS_64 : WINDOWS_32;
        } else if (SystemUtils.IS_OS_LINUX) {
            return "64".equals(bit) ? LINUX_64 : LINUX_32;
        } else if (SystemUtils.IS_OS_MAC) {
            if ("64".equals(bit)) {
                return MAC_64;
            } else {
                throw new XpdfRuntimeException("XpdfTools can only be run against 64-bit JVM on Mac operating system");
            }
        } else {
            throw new XpdfRuntimeException("XpdfTools can only be run on Windows, Linux, or Mac operating systems");
        }
    }

    //todo: add proper javadoc
    public static String getBinCommand(XpdfCommandType xpdfCommandType) throws IOException {
        switch (xpdfCommandType) {
            case PDF_TEXT:
                return PDF_TEXT_BIN_TMP_PATH.toFile().getCanonicalPath();
            default:
                throw new XpdfRuntimeException(String.format("XpdfCommandType case %s is missing from bin command switch statement", xpdfCommandType.name()));
        }
    }

    //todo: add proper javadoc
    //      this is the method that makes bin accessible to OS
    public static void createTemporaryBin(XpdfCommandType xpdfCommandType) {
        // get temporary bin path
        final Path tempBinPath;
        switch (xpdfCommandType) {
            case PDF_TEXT:
                tempBinPath = PDF_TEXT_BIN_TMP_PATH;
                break;
            default:
                throw new XpdfRuntimeException(String.format("XpdfCommandType case %s is missing from create temporary bin switch statement", xpdfCommandType.name()));
        }

        // do not create file if already exists
        if (tempBinPath.toFile().exists()) {
            return;
        }

        // create temporary bin file
        final InputStream binResourceStream;
        switch (xpdfCommandType) {
            case PDF_TEXT:
                binResourceStream = XpdfUtils.class.getClassLoader().getResourceAsStream(PDF_TEXT_RESOURCE_NAME);
                break;
            default:
                throw new XpdfRuntimeException(String.format("XpdfCommandType case %s is missing from create temporary bin switch statement", xpdfCommandType.name()));
        }

        if (binResourceStream == null) {
            throw new XpdfRuntimeException("Unable to locate xpdf binaries");
        }

        try {
            FileUtils.copyInputStreamToFile(binResourceStream, tempBinPath.toFile());
        } catch (Exception e) {
            throw new XpdfRuntimeException("Unable to copy xpdf binaries to directory accessible by OS", e);
        }
    }

    //todo: add a unit test for this switch (and ALL switches in codebase) that verify all cases accounted for
    /**
     * Gets the temporary output directory for a given {@code XpdfCommandType}.
     *
     * @param xpdfCommandType the {@code XpdfCommandType} to build a temporary output directory for
     * @return a {@code Path}
     * @since 4.4.0
     */
    public static Path getTemporaryOutputDirectory(XpdfCommandType xpdfCommandType) {
        switch (xpdfCommandType) {
            case PDF_TEXT:
                return TMP_DIR_OUT_PDF_TEXT;
            default:
                throw new XpdfRuntimeException(String.format("XpdfCommandType case %s is missing from temporary output directory switch statement", xpdfCommandType.name()));
        }
    }

}
