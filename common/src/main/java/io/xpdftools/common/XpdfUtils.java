package io.xpdftools.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.SystemUtils;

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
     * Combinations of OS and JVM bit architecture that Xpdf libraries can target.
     * These values correspond to the location of native libraries in the project resources.
     */
    private static final String LINUX_32 = "linux-32";
    private static final String LINUX_64 = "linux-64";
    private static final String MAC_64 = "mac-64";
    private static final String WINDOWS_32 = "windows-32";
    private static final String WINDOWS_64 = "windows-64";

    /**
     * Gets the OS and JVM bit architecture that a {@code XpdfTool} should target.
     *
     * @return a string representation of OS and bits
     * @throws XpdfRuntimeException if incompatible OS or JVM bit architecture
     * @since 4.4.0
     */
    public static String getTargetSystem() {
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

}
