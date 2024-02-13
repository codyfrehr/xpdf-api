/*
 * Common - The components shared between Xpdf APIs.
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
package io.xpdf.api.common.util

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.xpdf.api.common.exception.XpdfRuntimeException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Paths

class XpdfUtilsTest {

    private val javaTmpDir = System.getProperty("java.io.tmpdir")
    private val originalSunArchDataModel = System.getProperty("sun.arch.data.model")
    private val originalOsName = System.getProperty("os.name")

    @BeforeEach
    fun beforeEach() {
        mockkStatic(XpdfUtils::class)
    }

    @AfterEach
    fun afterEach() {
        unmockkStatic(XpdfUtils::class)

        // reset system properties that test may have altered
        System.setProperty("java.io.tmpdir", javaTmpDir)
        System.setProperty("sun.arch.data.model", originalSunArchDataModel)
        System.setProperty("os.name", originalOsName)
    }

    @Test
    fun `should get pdf text native library resource name`() {
        // given
        every { XpdfUtils.getTargetSystem() } returns "targetSystem"
        every { XpdfUtils.getPdfTextNativeLibraryName() } returns "nativeLibraryName"

        // when then
        XpdfUtils.getPdfTextNativeLibraryResourceName() shouldBe "xpdf/targetSystem/nativeLibraryName"
    }

    @Test
    fun `should get pdf text native library path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")
        every { XpdfUtils.getPdfTextNativeLibraryName() } returns "nativeLibraryName"

        // when then
        XpdfUtils.getPdfTextNativeLibraryPath() shouldBe Paths.get("tempPath", "pdf-text", "bin", "nativeLibraryName")
    }

    @Test
    fun `should get pdf text temp output path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")

        // when then
        XpdfUtils.getPdfTextTempOutputPath() shouldBe Paths.get("tempPath", "pdf-text", "out")
    }

    @Test
    fun `should get pdf text timeout seconds`() {
        // given
        every { XpdfUtils.getPdfTextTimeoutSeconds() } returns 99

        // when then
        XpdfUtils.getPdfTextTimeoutSeconds() shouldBe 99
    }

    @ParameterizedTest
    @CsvSource(
            "linux/bin32, pdftotext",
            "linux/bin64, pdftotext",
            "mac/bin64, pdftotext",
            "windows/bin32, pdftotext.exe",
            "windows/bin64, pdftotext.exe",
    )
    fun `should get pdf text native library name`(targetSystem: String,
                                           nativeLibraryName: String) {
        // given
        every { XpdfUtils.getTargetSystem() } returns targetSystem

        // when then
        XpdfUtils.getPdfTextNativeLibraryName() shouldBe nativeLibraryName
    }

    @Test
    fun `should get xpdf temp path`() {
        // when then
        XpdfUtils.getXpdfTempPath() shouldBe Paths.get(javaTmpDir, "xpdf-api")
    }

    @ParameterizedTest
    @CsvSource(
            "32, Linux, linux/bin32",
            "64, Linux, linux/bin64",
            "64, Mac, mac/bin64",
            "32, Windows, windows/bin32",
            "64, Windows, windows/bin64",
    )
    fun `should get target system`(bit: String,
                                   os: String,
                                   targetSystem: String) {
        // given
        System.setProperty("sun.arch.data.model", bit)
        System.setProperty("os.name", os)

        // when then
        XpdfUtils.getTargetSystem() shouldBe targetSystem
    }

    @Test
    fun `should throw exception while getting target system if unknown jvm bit architecture`() {
        // given
        System.setProperty("sun.arch.data.model", "UNKNOWN")

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unexpected error getting JVM bit architecture") {
            XpdfUtils.getTargetSystem()
        }
    }

    @Test
    fun `should throw exception while getting target system if no os`() {
        // given
        System.clearProperty("os.name")

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unexpected error getting operating system name") {
            XpdfUtils.getTargetSystem()
        }
    }

    @Test
    fun `should throw exception while getting target system if not 64-bit jvm on mac os`() {
        // given
        System.setProperty("sun.arch.data.model", "32")
        System.setProperty("os.name", "Mac")

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("XpdfTools can only be run against 64-bit JVM on Mac operating system") {
            XpdfUtils.getTargetSystem()
        }
    }

    @Test
    fun `should throw exception while getting target system if incompatible os`() {
        // given
        System.setProperty("sun.arch.data.model", "64")
        System.setProperty("os.name", "Unhandled")

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("XpdfTools can only be run on Windows, Linux, or Mac operating systems") {
            XpdfUtils.getTargetSystem()
        }
    }

}