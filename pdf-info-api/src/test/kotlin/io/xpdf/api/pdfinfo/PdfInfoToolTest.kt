/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
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
package io.xpdf.api.pdfinfo

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.mockk.*
import io.xpdf.api.common.exception.*
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdfinfo.options.PdfInfoEncoding
import io.xpdf.api.pdfinfo.util.PdfInfoUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

@ExtendWith(OutputCaptureExtension::class)
class PdfInfoToolTest {

    private val pdfInfoTool = PdfInfoTool.builder().build()

    companion object {
        @JvmStatic
        @AfterAll
        fun afterAll() {
            FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
        }
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }

    @Test
    fun `should initialize and copy executable to local system`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } just runs

        // when
        PdfInfoTool.builder().build()

        // then
        verify { FileUtils.copyInputStreamToFile(any(), any()) }
    }

    @Test
    fun `should initialize and not copy executable to local system`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath().toFile() } returns mockk {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)

        // when
        PdfInfoTool.builder().build()

        // then
        verify(exactly = 0) { FileUtils.copyInputStreamToFile(any(), any()) }
    }

    @Test
    fun `should initialize with executable`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }

        // when
        val result = PdfInfoTool.builder().executableFile(executableFile).build()

        // then
        result.executableFile shouldBe executableFile
    }

    @Test
    fun `should throw exception when initializing if unable to get executable resource stream`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }
        every { PdfInfoUtils.getPdfInfoExecutableResourceName() } returns "notexists"

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to locate executable in project resources") {
            PdfInfoTool.builder().build()
        }
    }

    @Test
    fun `should throw exception when initializing if unable to copy executable to local system`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } throws IOException()

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to copy executable from resources to local system") {
            PdfInfoTool.builder().build()
        }
    }

    @Test
    fun `should throw exception when initializing if unable to set execute permission`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath().toFile() } returns mockk {
            every { exists() } returns true
            every { setExecutable(any()) } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to set execute permissions on executable") {
            PdfInfoTool.builder().build()
        }
    }

    @Test
    fun `should throw exception when initializing with executable that does not exist`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("The configured executable does not exist") {
            PdfInfoTool.builder().executableFile(executableFile).build()
        }
    }

    @Test
    fun `should throw exception when initializing with executable if unable to set execute permission`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to set execute permissions on executable") {
            PdfInfoTool.builder().executableFile(executableFile).build()
        }
    }

    @Test
    fun `should initialize and get timeout from xpdf utils`() {
        // given
        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoTimeoutSeconds() } returns 99

        // when
        val result = PdfInfoTool.builder().build()

        // then
        result.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should initialize with timeout`() {
        // when
        val result = PdfInfoTool.builder().timeoutSeconds(99).build()

        // then
        result.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should process`(capturedOutput: CapturedOutput) {
        // given
        val standardOutputStream = mockk<ByteArrayInputStream>()
        val errorOutputStream = mockk<ByteArrayInputStream>()

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns standardOutputStream
            every { errorStream } returns errorOutputStream
            every { waitFor(any(), any()) } returns true
            every { exitValue() } returns 0
            every { destroy() } just runs
        }

        mockkStatic(IOUtils::class)
        every { IOUtils.toString(standardOutputStream, any<Charset>()) } returns "standardOutput"
        every { IOUtils.toString(errorOutputStream, any<Charset>()) } returns "errorOutput"

        val pdfInfoToolSpy = spyk(pdfInfoTool) {
            every { validate(any()) } just runs
            every { getCommandParts(any()) } returns listOf("part1", "part2", "part3")
        }

        // when
        val result = pdfInfoToolSpy.process(mockk())

        // then
        result.standardOutput shouldBe "standardOutput"

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking executable; command: [part1, part2, part3]"
        capturedOutput.all shouldContain "Invocation completed; exit code: 0, standard output: standardOutput"
        capturedOutput.all shouldContain "Invocation succeeded"
        capturedOutput.all shouldContain "Process finished"
    }

    @ParameterizedTest
    @CsvSource(
            "1, Error opening the PDF file",
            "2, Error opening the output file",
            "3, Error related to PDF permissions",
            "99, Other Xpdf error",
            "69, Unknown Xpdf error",
    )
    fun `should throw exception when processing if non-zero exit code`(exitCode: Int,
                                                                       message: String,
                                                                       capturedOutput: CapturedOutput) {
        // given
        val standardOutputStream = mockk<ByteArrayInputStream>()
        val errorOutputStream = mockk<ByteArrayInputStream>()

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns standardOutputStream
            every { errorStream } returns errorOutputStream
            every { waitFor(any(), any()) } returns true
            every { exitValue() } returns exitCode
            every { destroy() } just runs
        }

        mockkStatic(IOUtils::class)
        every { IOUtils.toString(standardOutputStream, any<Charset>()) } returns "standardOutput"
        every { IOUtils.toString(errorOutputStream, any<Charset>()) } returns "errorOutput"

        val pdfInfoToolSpy = spyk(pdfInfoTool) {
            every { validate(any()) } just runs
            every { getCommandParts(any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        val exception = shouldThrowWithMessage<XpdfExecutionException>(message) {
            pdfInfoToolSpy.process(mockk())
        }
        exception.standardOutput shouldBe "standardOutput"
        exception.errorOutput shouldBe "errorOutput"

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking executable; command: [part1, part2, part3]"
        capturedOutput.all shouldContain "Invocation completed; exit code: ${exitCode}, standard output: standardOutput"
        capturedOutput.all shouldContain "Invocation failed; error output: errorOutput"
        capturedOutput.all shouldContain "Process failed; exception message: $message"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should throw exception when processing if timout`(capturedOutput: CapturedOutput) {
        // given
        val standardOutputStream = mockk<ByteArrayInputStream>()
        val errorOutputStream = mockk<ByteArrayInputStream>()

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns standardOutputStream
            every { errorStream } returns errorOutputStream
            every { waitFor(any(), any()) } returns false
            every { destroy() } just runs
        }

        mockkStatic(IOUtils::class)
        every { IOUtils.toString(standardOutputStream, any<Charset>()) } returns "standardOutput"
        every { IOUtils.toString(errorOutputStream, any<Charset>()) } returns "errorOutput"

        val pdfInfoToolSpy = spyk(pdfInfoTool) {
            every { validate(any()) } just runs
            every { getCommandParts(any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        shouldThrowWithMessage<XpdfTimeoutException>("Timeout reached before process could finish") {
            pdfInfoToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking executable; command: [part1, part2, part3]"
        capturedOutput.all shouldContain "Invocation timed out"
        capturedOutput.all shouldContain "Process failed; exception message: Timeout reached before process could finish"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should throw exception when processing if caught non-xpdf exception`(capturedOutput: CapturedOutput) {
        // given
        val pdfInfoToolSpy = spyk(pdfInfoTool) {
            every { validate(any()) } throws Exception("some message")
        }

        // when then
        shouldThrow<XpdfProcessingException> {
            pdfInfoToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Process failed; exception message: some message"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should validate`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 1
            every { options.pageStop } returns 2
        }

        // when then
        shouldNotThrow<Exception> {
            pdfInfoTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if request is null`() {
        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfInfoRequest cannot be null") {
            pdfInfoTool.validate(null)
        }
    }

    @Test
    fun `should throw exception when validating if pdf file does not exist`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.exists() } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfFile does not exist") {
            pdfInfoTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive start page given`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 0
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStart must be greater than zero") {
            pdfInfoTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive end page given`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns null
            every { options.pageStop } returns -1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than zero") {
            pdfInfoTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if start page is larger than end page`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 2
            every { options.pageStop } returns 1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than or equal to PageStart") {
            pdfInfoTool.validate(request)
        }
    }

    @Test
    fun `should get command parts`() {
        // given
        val request = mockk<PdfInfoRequest>(relaxed = true) {
            every { pdfFile.canonicalPath } returns "pdfPath"
        }

        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
            every { canonicalPath } returns "cmdPath"
        }

        val pdfInfoTool = PdfInfoTool.builder().executableFile(executableFile).build()
        val pdfInfoToolSpy = spyk(pdfInfoTool) {
            every { getCommandOptions(any()) } returns listOf("opt1", "opt2")
        }

        // when then
        pdfInfoToolSpy.getCommandParts(request) shouldContainExactly listOf("cmdPath", "opt1", "opt2", "pdfPath")
    }

    @Test
    fun `should get empty list when getting command options if options null`() {
        // when then
        pdfInfoTool.getCommandOptions(null).shouldBeEmpty()
    }

    @Test
    fun `should get command options for pages`() {
        // given
        val options = PdfInfoOptions.builder()
                .pageStart(1)
                .pageStop(2)
                .build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-f", "1", "-l", "2")
    }

    @ParameterizedTest
    @CsvSource(
            "LATIN_1, Latin1",
            "ASCII_7, ASCII7",
            "UTF_8, UTF-8",
            "UCS_2, UCS-2",
            "SYMBOL, Symbol",
            "ZAPF_DINGBATS, ZapfDingbats",
    )
    fun `should get command options for encoding`(encoding: PdfInfoEncoding,
                                                  arg: String) {
        // given
        val options = PdfInfoOptions.builder().encoding(encoding).build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-enc", arg)
    }

    @Test
    fun `should get command options for including bounding boxes`() {
        // given
        val options = PdfInfoOptions.builder().boundingBoxesIncluded(true).build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-box")
    }

    @Test
    fun `should get command options for including metadata`() {
        // given
        val options = PdfInfoOptions.builder().metadataIncluded(true).build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-meta")
    }

    @Test
    fun `should get command options for undecoded dates`() {
        // given
        val options = PdfInfoOptions.builder().datesUndecoded(true).build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-rawdates")
    }

    @Test
    fun `should get command options for passwords`() {
        // given
        val options = PdfInfoOptions.builder()
                .ownerPassword("ownerPass")
                .userPassword("userPass")
                .build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-opw", "ownerPass", "-upw", "userPass")
    }

    @Test
    fun `should get command options for native options`() {
        // given
        val options = PdfInfoOptions.builder()
                .nativeOptions(mapOf(
                        "-option1" to "value1",
                        "-option2" to null,
                        "-option3" to "",
                        "-option4" to " "))
                .build()

        // when then
        pdfInfoTool.getCommandOptions(options) shouldContainExactly listOf("-option1", "value1", "-option2", "-option3", "-option4")
    }

    @Test
    fun `should convert to string`() {
        // given
        val pdfInfoTool = PdfInfoTool.builder()
            .timeoutSeconds(100)
            .build()

        // when then
        pdfInfoTool.toString() shouldMatch Regex("PdfInfoTool\\(executableFile=.+pdfinfo(\\.exe)?, timeoutSeconds=100\\)")
    }

}