/*
 * PdfImages API - An API for accessing a native pdfimages library (https://xpdf.io)
 * Copyright © 2025 xpdf.io (info@xpdf.io)
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
package io.xpdf.api.pdfimages.util

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

class PdfImagesUtilsTest {

    @BeforeEach
    fun beforeEach() {
        mockkStatic(XpdfUtils::class)
        mockkStatic(PdfImagesUtils::class)
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }

    @Test
    fun `should get pdf images executable resource name`() {
        // given
        every { XpdfUtils.getTargetSystem() } returns "targetSystem"
        every { PdfImagesUtils.getPdfImagesExecutableName() } returns "executableName"

        // when then
        PdfImagesUtils.getPdfImagesExecutableResourceName() shouldBe "xpdf/targetSystem/executableName"
    }

    @Test
    fun `should get pdf images executable path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")
        every { PdfImagesUtils.getPdfImagesExecutableName() } returns "executableName"

        // when then
        PdfImagesUtils.getPdfImagesExecutablePath() shouldBe Paths.get("tempPath", "pdf-images", "bin", "executableName")
    }

    @Test
    fun `should get pdf images temp output path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")

        // when then
        PdfImagesUtils.getPdfImagesTempOutputPath() shouldBe Paths.get("tempPath", "pdf-images", "out")
    }

    @Test
    fun `should get pdf images timeout seconds`() {
        // given
        every { PdfImagesUtils.getPdfImagesTimeoutSeconds() } returns 99

        // when then
        PdfImagesUtils.getPdfImagesTimeoutSeconds() shouldBe 99
    }

    @ParameterizedTest
    @CsvSource(
            "linux/bin32, pdfimages",
            "linux/bin64, pdfimages",
            "mac/bin64, pdfimages",
            "windows/bin32, pdfimages.exe",
            "windows/bin64, pdfimages.exe",
    )
    fun `should get pdf images executable name`(targetSystem: String,
                                                executableName: String) {
        // given
        every { XpdfUtils.getTargetSystem() } returns targetSystem

        // when then
        PdfImagesUtils.getPdfImagesExecutableName() shouldBe executableName
    }

}