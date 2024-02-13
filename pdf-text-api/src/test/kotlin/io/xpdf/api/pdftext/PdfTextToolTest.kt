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
package io.xpdf.api.pdftext

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import io.xpdf.api.common.exception.*
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdftext.options.PdfTextEncoding
import io.xpdf.api.pdftext.options.PdfTextEndOfLine
import io.xpdf.api.pdftext.options.PdfTextFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
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
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.UUID.randomUUID

@ExtendWith(OutputCaptureExtension::class)
class PdfTextToolTest {

    private val pdfTextTool = PdfTextTool.builder().build()

    @Test
    fun `should initialize and copy native library to local system`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextNativeLibraryPath().toFile().exists() } returns false

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } just runs

        // when
        PdfTextTool.builder().build()

        // then
        verify { FileUtils.copyInputStreamToFile(any(), any()) }

        unmockkStatic(XpdfUtils::class)
        unmockkStatic(FileUtils::class)
    }

    @Test
    fun `should initialize and not copy native library to local system`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextNativeLibraryPath().toFile().exists() } returns true

        mockkStatic(FileUtils::class)

        // when
        PdfTextTool.builder().build()

        // then
        verify(exactly = 0) { FileUtils.copyInputStreamToFile(any(), any()) }

        unmockkStatic(XpdfUtils::class)
        unmockkStatic(FileUtils::class)
    }

    @Test
    fun `should initialize with native library`() {
        // given
        val nativeLibraryPath = mockk<Path> {
            every { toFile().exists() } returns true
        }

        // when
        val result = PdfTextTool.builder().nativeLibraryPath(nativeLibraryPath).build()

        // then
        result.nativeLibraryPath shouldBe nativeLibraryPath
    }

    @Test
    fun `should throw exception when initializing if unable to get native library resource stream`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextNativeLibraryPath().toFile().exists() } returns false
        every { XpdfUtils.getPdfTextNativeLibraryResourceName() } returns "notexists"

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to locate native library in project resources") {
            PdfTextTool.builder().build()
        }

        unmockkStatic(XpdfUtils::class)
    }

    @Test
    fun `should throw exception when initializing if unable to copy native library to local system`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextNativeLibraryPath().toFile().exists() } returns false

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } throws IOException()

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to copy native library from resources to local system") {
            PdfTextTool.builder().build()
        }

        unmockkStatic(XpdfUtils::class)
        unmockkStatic(FileUtils::class)
    }

    @Test
    fun `should throw exception when initializing with native library that does not exist`() {
        // given
        val nativeLibraryPath = mockk<Path> {
            every { toFile().exists() } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("The configured native library does not exist at the path specified") {
            PdfTextTool.builder().nativeLibraryPath(nativeLibraryPath).build()
        }
    }

    @Test
    fun `should initialize and get timeout from xpdf utils`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextTimeoutSeconds() } returns 99

        // when
        val result = PdfTextTool.builder().build()

        // then
        result.timeoutSeconds shouldBe 99

        unmockkStatic(XpdfUtils::class)
    }

    @Test
    fun `should initialize with timeout`() {
        // when
        val result = PdfTextTool.builder().timeoutSeconds(99).build()

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

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
        }

        // when
        val result = pdfTextToolSpy.process(mockk())

        // then
        result.textFile shouldBe textFile
        result.standardOutput shouldBe "standardOutput"

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output text file"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking native library; command: [part1, part2, part3]"
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

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        shouldThrowWithMessage<XpdfNativeExecutionException>(message) {
            pdfTextToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output text file"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking native library; command: [part1, part2, part3]"
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

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        shouldThrowWithMessage<XpdfNativeTimeoutException>("Timeout reached before process could finish") {
            pdfTextToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output text file"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking native library; command: [part1, part2, part3]"
        capturedOutput.all shouldContain "Invocation timed out"
        capturedOutput.all shouldContain "Process failed; exception message: Timeout reached before process could finish"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should throw exception when processing if caught non-xpdf exception`(capturedOutput: CapturedOutput) {
        // given
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } throws Exception("some message")
        }

        // when then
        shouldThrow<XpdfProcessingException> {
            pdfTextToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Process failed; exception message: some message"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should validate`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 1
            every { options.pageStop } returns 2
        }

        // when then
        shouldNotThrow<Exception> {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if request is null`() {
        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfTextRequest cannot be null") {
            pdfTextTool.validate(null)
        }
    }

    @Test
    fun `should throw exception when validating if pdf file does not exist`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfFile does not exist") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if cannot get canonical path of text file`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { textFile.canonicalPath } throws IOException()
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("Invalid path given for TextFile") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive start page given`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 0
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStart must be greater than zero") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive end page given`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns null
            every { options.pageStop } returns -1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than zero") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if start page is larger than end page`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 2
            every { options.pageStop } returns 1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than or equal to PageStart") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should initialize text file when file provided in request`() {
        // given
        val textFile = mockk<File>(relaxed = true)
        val request = mockk<PdfTextRequest> {
            every { getTextFile() } returns textFile
        }

        // when then
        pdfTextTool.initializeTextFile(request) shouldBe textFile
    }

    @Test
    fun `should initialize text file when file not provided in request`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextTempOutputPath().toFile().canonicalPath } returns "outPath"

        val randomUuid = randomUUID()
        mockkStatic(UUID::class)
        every { randomUUID() } returns randomUuid

        val request = mockk<PdfTextRequest> {
            every { getTextFile() } returns null
        }

        val textFile = mockk<File>(relaxed = true)
        val textFileName = "$randomUuid.txt"

        mockkStatic(Paths::class)
        every { Paths.get(any(), any()).toFile() } returns textFile

        // when then
        pdfTextTool.initializeTextFile(request) shouldBe textFile

        verify { Paths.get("outPath", textFileName) }

        unmockkStatic(XpdfUtils::class)
        unmockkStatic(UUID::class)
        unmockkStatic(Paths::class)
    }

    @Test
    fun `should get command parts`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.canonicalPath } returns "pdfPath"
        }

        val textFile = mockk<File> {
            every { canonicalPath } returns "textPath"
        }

        val nativeLibraryPath = mockk<Path> {
            every { toFile().exists() } returns true
            every { toFile().canonicalPath } returns "cmdPath"
        }

        val pdfTextTool = PdfTextTool.builder().nativeLibraryPath(nativeLibraryPath).build()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { getCommandOptions(any()) } returns listOf("opt1", "opt2")
        }

        // when then
        pdfTextToolSpy.getCommandParts(request, textFile) shouldContainExactly listOf("cmdPath", "opt1", "opt2", "pdfPath", "textPath")
    }

    @Test
    fun `should get empty list when getting command options if options null`() {
        // when then
        pdfTextTool.getCommandOptions(null).shouldBeEmpty()
    }

    @Test
    fun `should get command options for pages`() {
        // given
        val options = PdfTextOptions.builder()
                .pageStart(1)
                .pageStop(2)
                .build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-f", "1", "-l", "2")
    }

    @ParameterizedTest
    @CsvSource(
            "LAYOUT, -layout",
            "SIMPLE, -simple",
            "SIMPLE_2, -simple2",
            "TABLE, -table",
            "LINE_PRINTER, -lineprinter",
            "RAW, -raw",
    )
    fun `should get command options for format`(format: PdfTextFormat,
                                                arg: String) {
        // given
        val options = PdfTextOptions.builder().format(format).build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf(arg)
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
    fun `should get command options for encoding`(encoding: PdfTextEncoding,
                                                  arg: String) {
        // given
        val options = PdfTextOptions.builder().encoding(encoding).build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-enc", arg)
    }

    @ParameterizedTest
    @CsvSource(
            "DOS, dos",
            "MAC, mac",
            "UNIX, unix",
    )
    fun `should get command options for end of line`(endOfLine: PdfTextEndOfLine,
                                                     arg: String) {
        // given
        val options = PdfTextOptions.builder().endOfLine(endOfLine).build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-eol", arg)
    }

    @Test
    fun `should get command options for excluding page break`() {
        // given
        val options = PdfTextOptions.builder().pageBreakExcluded(true).build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-nopgbrk")
    }

    @Test
    fun `should get command options for passwords`() {
        // given
        val options = PdfTextOptions.builder()
                .ownerPassword("ownerPass")
                .userPassword("userPass")
                .build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-opw", "ownerPass", "-upw", "userPass")
    }

    @Test
    fun `should get command options for native options`() {
        // given
        val options = PdfTextOptions.builder()
                .nativeOptions(mapOf(
                        "-option1" to "value1",
                        "-option2" to null,
                        "-option3" to "",
                        "-option4" to " "))
                .build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-option1", "value1", "-option2", "-option3", "-option4")
    }

}