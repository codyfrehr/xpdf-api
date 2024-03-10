/*
 * Common - The components shared between Xpdf APIs (https://xpdf.io)
 * Copyright © 2024 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

    private XpdfUtils() {
    }

    /**
     * Gets the temporary directory utilized by native <em>Xpdf</em> executables.
     *
     * @return temporary directory
     */
    public static Path getXpdfTempPath() {
        return Paths.get(System.getProperty("java.io.tmpdir")).resolve( "xpdf-api");
    }

    /**
     * Gets the OS and JVM bit architecture specific to this system.
     * This helps to locate the correct <em>Xpdf</em> executable for this system in the project resources.
     *
     * @return representation of OS and bits
     */
    public static String getTargetSystem() {
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
            return "64".equals(bit) ? "windows/bin64" : "windows/bin32";
        } else if (os.startsWith("Linux")) {
            return "64".equals(bit) ? "linux/bin64" : "linux/bin32";
        } else if (os.startsWith("Mac")) {
            if ("64".equals(bit)) {
                return "mac/bin64";
            } else {
                throw new XpdfRuntimeException("XpdfTools can only be run against 64-bit JVM on Mac operating system");
            }
        } else {
            throw new XpdfRuntimeException("XpdfTools can only be run on Windows, Linux, or Mac operating systems");
        }
    }

}
