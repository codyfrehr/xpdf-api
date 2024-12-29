/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
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
package io.xpdf.api.pdfinfo.util

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

class PdfInfoUtilsTest {

    @BeforeEach
    fun beforeEach() {
        mockkStatic(XpdfUtils::class)
        mockkStatic(PdfInfoUtils::class)
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }

    @Test
    fun `should get pdf info executable resource name`() {
        // given
        every { XpdfUtils.getTargetSystem() } returns "targetSystem"
        every { PdfInfoUtils.getPdfInfoExecutableName() } returns "executableName"

        // when then
        PdfInfoUtils.getPdfInfoExecutableResourceName() shouldBe "xpdf/targetSystem/executableName"
    }

    @Test
    fun `should get pdf info executable path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")
        every { PdfInfoUtils.getPdfInfoExecutableName() } returns "executableName"

        // when then
        PdfInfoUtils.getPdfInfoExecutablePath() shouldBe Paths.get("tempPath", "pdf-info", "bin", "executableName")
    }

    @Test
    fun `should get pdf info timeout seconds`() {
        // given
        every { PdfInfoUtils.getPdfInfoTimeoutSeconds() } returns 99

        // when then
        PdfInfoUtils.getPdfInfoTimeoutSeconds() shouldBe 99
    }

    @ParameterizedTest
    @CsvSource(
            "linux/bin32, pdfinfo",
            "linux/bin64, pdfinfo",
            "mac/bin64, pdfinfo",
            "windows/bin32, pdfinfo.exe",
            "windows/bin64, pdfinfo.exe",
    )
    fun `should get pdf info executable name`(targetSystem: String,
                                              executableName: String) {
        // given
        every { XpdfUtils.getTargetSystem() } returns targetSystem

        // when then
        PdfInfoUtils.getPdfInfoExecutableName() shouldBe executableName
    }

}