package io.xpdftools.pdftext

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.xpdftools.common.exception.*
import io.xpdftools.common.util.ReadInputStreamTask
import io.xpdftools.common.util.XpdfUtils
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.UUID.randomUUID

class PdfTextToolTest {

    //todo: some kind of setup like this might be nicer...
//    private val nativeLibraryPath = mockk<Path>(relaxed = true)
//    private val defaultOutputDirectory = mockk<File>(relaxed = true)
//    private val timeoutMilliseconds = 10L
//    private val pdfTextTool = PdfTextTool.builder()
//            .nativeLibraryPath(nativeLibraryPath)
//            .defaultOutputDirectory(defaultOutputDirectory)
//            .timeoutMilliseconds(timeoutMilliseconds)
//            .build()

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
        every { FileUtils.copyInputStreamToFile(any(), any()) } just runs

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
    fun `should initialize and get default output directory from xpdf utils`() {
        // given
        mockkStatic(XpdfUtils::class)

        val defaultOutputDirectory = mockk<File>()

        every { XpdfUtils.getPdfTextDefaultOutputPath() } returns mockk<Path> {
            every { toFile() } returns defaultOutputDirectory
        }

        // when
        val result = PdfTextTool.builder().build()

        // then
        result.defaultOutputDirectory shouldBe defaultOutputDirectory

        unmockkStatic(XpdfUtils::class)
    }

    @Test
    fun `should initialize with default output directory`() {
        // given
        val defaultOutputDirectory = mockk<File>(relaxed = true)

        // when
        val result = PdfTextTool.builder().defaultOutputDirectory(defaultOutputDirectory).build()

        // then
        result.defaultOutputDirectory shouldBe defaultOutputDirectory
    }

    @Test
    fun `should initialize and get timeout from xpdf utils`() {
        // given
        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextTimeoutMilliseconds() } returns 100L

        // when
        val result = PdfTextTool.builder().build()

        // then
        result.timeoutMilliseconds shouldBe 100L

        unmockkStatic(XpdfUtils::class)
    }

    @Test
    fun `should initialize with timeout`() {
        // when
        val result = PdfTextTool.builder().timeoutMilliseconds(100L).build()

        // then
        result.timeoutMilliseconds shouldBe 100L
    }

    @Test
    fun `should process`() {
        // given
        val standardOutput = "standardOutput"
        val errorOutput = "errorOutput"
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

        mockkConstructor(ReadInputStreamTask::class)
        every { constructedWith<ReadInputStreamTask>(EqMatcher(standardOutputStream)).call() } returns standardOutput
        every { constructedWith<ReadInputStreamTask>(EqMatcher(errorOutputStream)).call() } returns errorOutput

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns mockk()
        }

        // when
        val result = pdfTextToolSpy.process(mockk())

        // then
        result.textFile shouldBe textFile
        result.standardOutput shouldBe standardOutput
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
                                                                       message: String) {
        // given
        val standardOutput = "standardOutput"
        val errorOutput = "errorOutput"
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

        mockkConstructor(ReadInputStreamTask::class)
        every { constructedWith<ReadInputStreamTask>(EqMatcher(standardOutputStream)).call() } returns standardOutput
        every { constructedWith<ReadInputStreamTask>(EqMatcher(errorOutputStream)).call() } returns errorOutput

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns mockk()
        }

        // when then
        shouldThrowWithMessage<XpdfNativeExecutionException>(message) {
            pdfTextToolSpy.process(mockk())
        }
    }

    @Test
    fun `should throw exception when processing if timout`() {
        // given
        val standardOutput = "standardOutput"
        val errorOutput = "errorOutput"
        val standardOutputStream = mockk<ByteArrayInputStream>()
        val errorOutputStream = mockk<ByteArrayInputStream>()

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockk {
            every { inputStream } returns standardOutputStream
            every { errorStream } returns errorOutputStream
            every { waitFor(any(), any()) } returns false
            every { destroy() } just runs
        }

        mockkConstructor(ReadInputStreamTask::class)
        every { constructedWith<ReadInputStreamTask>(EqMatcher(standardOutputStream)).call() } returns standardOutput
        every { constructedWith<ReadInputStreamTask>(EqMatcher(errorOutputStream)).call() } returns errorOutput

        val textFile = mockk<File>()
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } just runs
            every { initializeTextFile(any()) } returns textFile
            every { getCommandParts(any(), any()) } returns mockk()
        }

        // when then
        shouldThrowWithMessage<XpdfNativeTimeoutException>("Timeout reached before process could finish") {
            pdfTextToolSpy.process(mockk())
        }
    }

    @Test
    fun `should throw exception when processing if caught non-xpdf exception`() {
        // given
        val pdfTextToolSpy = spyk(pdfTextTool) {
            every { validate(any()) } throws Exception()
        }

        // when then
        shouldThrow<XpdfProcessingException> {
            pdfTextToolSpy.process(mockk())
        }
    }

    @Test
    fun `should validate`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 1
            every { options.pageEnd } returns 2
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
    fun `should throw exception when validating if negative start page given`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns -1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStart cannot be less than zero") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if negative end page given`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageEnd } returns -1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageEnd cannot be less than zero") {
            pdfTextTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if start page is larger than end page`() {
        // given
        val request = mockk<PdfTextRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 2
            every { options.pageEnd } returns 1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStart must come before PageEnd") {
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
        val randomUuid = randomUUID()
        mockkStatic(UUID::class)
        every { randomUUID() } returns randomUuid

        val request = mockk<PdfTextRequest> {
            every { textFile } returns null
        }

        // when then
        pdfTextTool.initializeTextFile(request) shouldBe Paths.get(pdfTextTool.defaultOutputDirectory.canonicalPath, "$randomUuid.txt").toFile()

        unmockkStatic(UUID::class)
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

        mockkStatic(XpdfUtils::class)
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
                .pageEnd(2)
                .build()

        // when then
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-f", "1", "-l", "2")
    }

    @ParameterizedTest
    @CsvSource(
            "RAW, -raw",
            "SIMPLE, -simple",
            "TABLE, -table",
            "LAYOUT, -layout",
            "LINE_PRINTER, -lineprinter",
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
            "ASCII_7, ASCII7",
            "LATIN_1, Latin1",
            "SYMBOL, Symbol",
            "UCS_2, UCS-2",
            "UTF_8, UTF-8",
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
    fun `should get command options for including page break`() {
        // given
        val options = PdfTextOptions.builder().pageBreakIncluded(false).build()

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
        pdfTextTool.getCommandOptions(options) shouldContainExactly listOf("-opw", "\"ownerPass\"", "-upw", "\"userPass\"")
    }

}