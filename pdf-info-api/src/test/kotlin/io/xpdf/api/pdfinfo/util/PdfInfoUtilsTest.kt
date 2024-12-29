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