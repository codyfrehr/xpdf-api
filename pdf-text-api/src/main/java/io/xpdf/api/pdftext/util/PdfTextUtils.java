/*
 * PdfText API - An API for accessing a native pdftotext library.
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
package io.xpdf.api.pdftext.util;

import io.xpdf.api.common.util.XpdfUtils;
import io.xpdf.api.pdftext.PdfTextTool;

import java.nio.file.Path;

/**
 * Helpers for a {@link PdfTextTool}.
 *
 * @since 1.0.0
 */
public class PdfTextUtils {

    /**
     * Gets the resource name of the <em>pdftotext</em> executable native to this system.
     *
     * @return executable resource name
     * @since 1.0.0
     */
    public static String getPdfTextExecutableResourceName() {
        return String.format("xpdf/%s/%s", XpdfUtils.getTargetSystem(), getPdfTextExecutableName());
    }

    /**
     * Gets the path where the native <em>pdftotext</em> executable should be copied so that it can be accessed by OS.
     *
     * @return executable path
     * @since 1.0.0
     */
    public static Path getPdfTextExecutablePath() {
        return XpdfUtils.getXpdfTempPath().resolve("pdf-text").resolve("bin").resolve(getPdfTextExecutableName());
    }

    /**
     * Gets the temporary directory where the <em>pdftotext</em> executable should write output.
     *
     * @return temporary directory
     * @since 1.0.0
     */
    public static Path getPdfTextTempOutputPath() {
        return XpdfUtils.getXpdfTempPath().resolve("pdf-text").resolve("out");
    }

    /**
     * Gets the maximum amount of time in seconds allotted to the <em>pdftotext</em> process before timing out.
     *
     * @return timeout length in seconds for process
     * @since 1.0.0
     */
    public static Integer getPdfTextTimeoutSeconds() {
        return 30;
    }

    /**
     * Gets the name of the native <em>pdftotext</em> executable.
     *
     * @return executable name
     */
    protected static String getPdfTextExecutableName() {
        return String.format("pdftotext%s", XpdfUtils.getTargetSystem().contains("windows") ? ".exe" : "");
    }

}
