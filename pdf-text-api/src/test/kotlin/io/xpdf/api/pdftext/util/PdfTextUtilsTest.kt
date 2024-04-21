/*
 * PdfText API - An API for accessing a native pdftotext library (https://xpdf.io)
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
package io.xpdf.api.pdftext.util

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.xpdf.api.common.util.XpdfUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Paths

class PdfTextUtilsTest {

    @BeforeEach
    fun beforeEach() {
        mockkStatic(XpdfUtils::class)
        mockkStatic(PdfTextUtils::class)
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }

    @Test
    fun `should get pdf text executable resource name`() {
        // given
        every { XpdfUtils.getTargetSystem() } returns "targetSystem"
        every { PdfTextUtils.getPdfTextExecutableName() } returns "executableName"

        // when then
        PdfTextUtils.getPdfTextExecutableResourceName() shouldBe "xpdf/targetSystem/executableName"
    }

    @Test
    fun `should get pdf text executable path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")
        every { PdfTextUtils.getPdfTextExecutableName() } returns "executableName"

        // when then
        PdfTextUtils.getPdfTextExecutablePath() shouldBe Paths.get("tempPath", "pdf-text", "bin", "executableName")
    }

    @Test
    fun `should get pdf text temp output path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")

        // when then
        PdfTextUtils.getPdfTextTempOutputPath() shouldBe Paths.get("tempPath", "pdf-text", "out")
    }

    @Test
    fun `should get pdf text timeout seconds`() {
        // given
        every { PdfTextUtils.getPdfTextTimeoutSeconds() } returns 99

        // when then
        PdfTextUtils.getPdfTextTimeoutSeconds() shouldBe 99
    }

    @ParameterizedTest
    @CsvSource(
            "linux/bin32, pdftotext",
            "linux/bin64, pdftotext",
            "mac/bin64, pdftotext",
            "windows/bin32, pdftotext.exe",
            "windows/bin64, pdftotext.exe",
    )
    fun `should get pdf text executable name`(targetSystem: String,
                                              executableName: String) {
        // given
        every { XpdfUtils.getTargetSystem() } returns targetSystem

        // when then
        PdfTextUtils.getPdfTextExecutableName() shouldBe executableName
    }

}