package io.xpdftools.common.util

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.xpdftools.common.exception.XpdfRuntimeException
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
    fun `should get pdf text resource name`() {
        // given
        every { XpdfUtils.getTargetSystem() } returns "targetSystem"
        every { XpdfUtils.getPdfTextLibraryName() } returns "libraryName"

        // when then
        XpdfUtils.getPdfTextResourceName() shouldBe "xpdf/targetSystem/libraryName"
    }

    @Test
    fun `should get pdf text local path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")
        every { XpdfUtils.getPdfTextLibraryName() } returns "libraryName"

        // when then
        XpdfUtils.getPdfTextLocalPath() shouldBe Paths.get("tempPath", "pdf-text", "bin", "libraryName")
    }

    @Test
    fun `should get pdf text out path`() {
        // given
        every { XpdfUtils.getXpdfTempPath() } returns Paths.get("tempPath")

        // when then
        XpdfUtils.getPdfTextOutPath() shouldBe Paths.get("tempPath", "pdf-text", "out")
    }

    @ParameterizedTest
    @CsvSource(
            "linux-32, pdftotext",
            "linux-64, pdftotext",
            "mac-64, pdftotext",
            "windows-32, pdftotext.exe",
            "windows-64, pdftotext.exe",
    )
    fun `should get pdf text library name`(targetSystem: String,
                                           libraryName: String) {
        // given
        every { XpdfUtils.getTargetSystem() } returns targetSystem

        // when then
        XpdfUtils.getPdfTextLibraryName() shouldBe libraryName
    }

    @Test
    fun `should get xpdf temp path`() {
        // when then
        XpdfUtils.getXpdfTempPath() shouldBe Paths.get(javaTmpDir, "xpdf-tools")
    }

    @ParameterizedTest
    @CsvSource(
            "32, Linux, linux-32",
            "64, Linux, linux-64",
            "64, Mac, mac-64",
            "32, Windows, windows-32",
            "64, Windows, windows-64",
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