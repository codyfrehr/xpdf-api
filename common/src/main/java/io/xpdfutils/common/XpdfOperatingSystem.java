package io.xpdfutils.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.SystemUtils;

@Getter
@RequiredArgsConstructor
//todo: are there other special methods i should override or anything, given that this is a pretty special type of enum..?
//todo: ensure that all xpdf utilities, not just pdftotext, follow this pattern.
//todo: use "Platform.isWindows()" instead?
public enum XpdfOperatingSystem {
    LINUX_32("linux", "32"),
    LINUX_64("linux", "64"),
    MAC_64("mac", "64"),
    WINDOWS_32("windows", "32"),
    WINDOWS_64("windows", "64");

    private final String operatingSystem;
    private final String bit;

    public static XpdfOperatingSystem get() {
        val bit = System.getProperty("sun.arch.data.model");

        //todo: throw different exception type?
        if ("UNKNOWN".equals(bit)) {
            throw new XpdfRuntimeException("Unexpected error getting bit architecture during instantiation");
        }

        if (SystemUtils.IS_OS_LINUX) {
            return "64".equals(bit) ? LINUX_64 : LINUX_32;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return "64".equals(bit) ? WINDOWS_64 : WINDOWS_32;
        } else if (SystemUtils.IS_OS_MAC) {
            if ("64".equals(bit)) {
                return MAC_64;
            } else {
                throw new XpdfRuntimeException("Xpdf can only be run on 64-bit Mac operating system");
            }
        } else {
            throw new XpdfRuntimeException("Xpdf can only be run on Linux, Mac, or Windows operating systems");
        }
    }

}
