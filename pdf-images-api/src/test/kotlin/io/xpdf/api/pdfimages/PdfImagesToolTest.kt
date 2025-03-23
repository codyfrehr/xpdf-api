package io.xpdf.api.pdfimages

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
import io.xpdf.api.pdfimages.options.PdfImagesFileFormat
import io.xpdf.api.pdfimages.util.PdfImagesUtils
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
import java.io.FilenameFilter
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*
import java.util.UUID.randomUUID

@ExtendWith(OutputCaptureExtension::class)
class PdfImagesToolTest {

    private val pdfImagesTool = PdfImagesTool.builder().build()

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
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } just runs

        // when
        PdfImagesTool.builder().build()

        // then
        verify { FileUtils.copyInputStreamToFile(any(), any()) }
    }

    @Test
    fun `should initialize and not copy executable to local system`() {
        // given
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath().toFile() } returns mockk {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)

        // when
        PdfImagesTool.builder().build()

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
        val result = PdfImagesTool.builder().executableFile(executableFile).build()

        // then
        result.executableFile shouldBe executableFile
    }

    @Test
    fun `should throw exception when initializing if unable to get executable resource stream`() {
        // given
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }
        every { PdfImagesUtils.getPdfImagesExecutableResourceName() } returns "notexists"

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to locate executable in project resources") {
            PdfImagesTool.builder().build()
        }
    }

    @Test
    fun `should throw exception when initializing if unable to copy executable to local system`() {
        // given
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath().toFile() } returns mockk {
            every { exists() } returns false
            every { setExecutable(any()) } returns true
        }

        mockkStatic(FileUtils::class)
        every { FileUtils.copyInputStreamToFile(any(), any()) } throws IOException()

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to copy executable from resources to local system") {
            PdfImagesTool.builder().build()
        }
    }

    @Test
    fun `should throw exception when initializing if unable to set execute permission`() {
        // given
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath().toFile() } returns mockk {
            every { exists() } returns true
            every { setExecutable(any()) } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfRuntimeException>("Unable to set execute permissions on executable") {
            PdfImagesTool.builder().build()
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
            PdfImagesTool.builder().executableFile(executableFile).build()
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
            PdfImagesTool.builder().executableFile(executableFile).build()
        }
    }

    @Test
    fun `should initialize and get timeout from xpdf utils`() {
        // given
        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesTimeoutSeconds() } returns 99

        // when
        val result = PdfImagesTool.builder().build()

        // then
        result.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should initialize with timeout`() {
        // when
        val result = PdfImagesTool.builder().timeoutSeconds(99).build()

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

        val imageFiles = listOf(File("some1.jpg"), File("some2.jpg"))

        val pdfImagesToolSpy = spyk(pdfImagesTool) {
            every { validate(any()) } just runs
            every { initializeImageFilePathPrefix(any()) } returns mockk()
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
            every { getImageFilesMatchingPathPrefix(any()) } returns imageFiles
        }

        // when
        val result = pdfImagesToolSpy.process(mockk())

        // then
        result.imageFiles shouldBe imageFiles
        result.standardOutput shouldBe "standardOutput"

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output image file path prefix"
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

        val pdfImagesToolSpy = spyk(pdfImagesTool) {
            every { validate(any()) } just runs
            every { initializeImageFilePathPrefix(any()) } returns mockk()
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        val exception = shouldThrowWithMessage<XpdfExecutionException>(message) {
            pdfImagesToolSpy.process(mockk())
        }
        exception.standardOutput shouldBe "standardOutput"
        exception.errorOutput shouldBe "errorOutput"

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output image file path prefix"
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

        val pdfImagesToolSpy = spyk(pdfImagesTool) {
            every { validate(any()) } just runs
            every { initializeImageFilePathPrefix(any()) } returns mockk()
            every { getCommandParts(any(), any()) } returns listOf("part1", "part2", "part3")
        }

        // when then
        shouldThrowWithMessage<XpdfTimeoutException>("Timeout reached before process could finish") {
            pdfImagesToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Configuring output image file path prefix"
        capturedOutput.all shouldContain "Building command"
        capturedOutput.all shouldContain "Invoking executable; command: [part1, part2, part3]"
        capturedOutput.all shouldContain "Invocation timed out"
        capturedOutput.all shouldContain "Process failed; exception message: Timeout reached before process could finish"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should throw exception when processing if caught non-xpdf exception`(capturedOutput: CapturedOutput) {
        // given
        val pdfImagesToolSpy = spyk(pdfImagesTool) {
            every { validate(any()) } throws Exception("some message")
        }

        // when then
        shouldThrow<XpdfProcessingException> {
            pdfImagesToolSpy.process(mockk())
        }

        capturedOutput.all shouldContain "Process starting"
        capturedOutput.all shouldContain "Validating request"
        capturedOutput.all shouldContain "Process failed; exception message: some message"
        capturedOutput.all shouldContain "Process finished"
    }

    @Test
    fun `should validate`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 1
            every { options.pageStop } returns 2
        }

        // when then
        shouldNotThrow<Exception> {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if request is null`() {
        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfImagesRequest cannot be null") {
            pdfImagesTool.validate(null)
        }
    }

    @Test
    fun `should throw exception when validating if pdf file does not exist`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns false
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PdfFile does not exist") {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if cannot get canonical path of image file path prefix`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { imageFilePathPrefix.toFile().canonicalPath } throws IOException()
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("Invalid path given for ImageFilePathPrefix") {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive start page given`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 0
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStart must be greater than zero") {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if non-positive end page given`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns null
            every { options.pageStop } returns -1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than zero") {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should throw exception when validating if start page is larger than end page`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.exists() } returns true
            every { options.pageStart } returns 2
            every { options.pageStop } returns 1
        }

        // when then
        shouldThrowWithMessage<XpdfValidationException>("PageStop must be greater than or equal to PageStart") {
            pdfImagesTool.validate(request)
        }
    }

    @Test
    fun `should initialize image file path prefix when path provided in request`() {
        // given
        val imageFilePathPrefix = mockk<Path>(relaxed = true)
        val request = mockk<PdfImagesRequest> {
            every { getImageFilePathPrefix() } returns imageFilePathPrefix
        }

        // when then
        pdfImagesTool.initializeImageFilePathPrefix(request) shouldBe imageFilePathPrefix
    }

    @Test
    fun `should initialize image file path prefix when path not provided in request`() {
        // given
        val randomUuid = randomUUID().toString()
        mockkStatic(UUID::class)
        every { randomUUID().toString() } returns randomUuid

        val imageFilePathPrefix = mockk<Path>(relaxed = true)

        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesTempOutputPath().resolve(randomUuid).resolve("image") } returns imageFilePathPrefix

        val request = mockk<PdfImagesRequest> {
            every { getImageFilePathPrefix() } returns null
        }

        // when then
        pdfImagesTool.initializeImageFilePathPrefix(request) shouldBe imageFilePathPrefix

        verify { imageFilePathPrefix.parent.toFile().deleteOnExit() }
    }

    @Test
    fun `should get command parts`() {
        // given
        val request = mockk<PdfImagesRequest>(relaxed = true) {
            every { pdfFile.canonicalPath } returns "pdfPath"
        }

        val imageFilePathPrefix = mockk<Path> {
            every { toFile().canonicalPath } returns "imageFilePathPrefix"
        }

        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
            every { canonicalPath } returns "cmdPath"
        }

        val pdfImagesTool = PdfImagesTool.builder().executableFile(executableFile).build()
        val pdfImagesToolSpy = spyk(pdfImagesTool) {
            every { getCommandOptions(any()) } returns listOf("opt1", "opt2")
        }

        // when then
        pdfImagesToolSpy.getCommandParts(request, imageFilePathPrefix) shouldContainExactly listOf("cmdPath", "opt1", "opt2", "pdfPath", "imageFilePathPrefix")
    }

    @Test
    fun `should get empty list when getting command options if options null`() {
        // when then
        pdfImagesTool.getCommandOptions(null).shouldBeEmpty()
    }

    @Test
    fun `should get command options for pages`() {
        // given
        val options = PdfImagesOptions.builder()
                .pageStart(1)
                .pageStop(2)
                .build()

        // when then
        pdfImagesTool.getCommandOptions(options) shouldContainExactly listOf("-f", "1", "-l", "2")
    }

    @ParameterizedTest
    @CsvSource(
            "JPEG, -j",
            "RAW, -raw",
    )
    fun `should get command options for file format`(fileFormat: PdfImagesFileFormat,
                                                     arg: String) {
        // given
        val options = PdfImagesOptions.builder().fileFormat(fileFormat).build()

        // when then
        pdfImagesTool.getCommandOptions(options) shouldContainExactly listOf(arg)
    }

    @Test
    fun `should get command options for including metadata`() {
        // given
        val options = PdfImagesOptions.builder().metadataIncluded(true).build()

        // when then
        pdfImagesTool.getCommandOptions(options) shouldContainExactly listOf("-list")
    }

    @Test
    fun `should get command options for passwords`() {
        // given
        val options = PdfImagesOptions.builder()
                .ownerPassword("ownerPass")
                .userPassword("userPass")
                .build()

        // when then
        pdfImagesTool.getCommandOptions(options) shouldContainExactly listOf("-opw", "ownerPass", "-upw", "userPass")
    }

    @Test
    fun `should get command options for native options`() {
        // given
        val options = PdfImagesOptions.builder()
                .nativeOptions(mapOf(
                        "-option1" to "value1",
                        "-option2" to null,
                        "-option3" to "",
                        "-option4" to " "))
                .build()

        // when then
        pdfImagesTool.getCommandOptions(options) shouldContainExactly listOf("-option1", "value1", "-option2", "-option3", "-option4")
    }

    @Test
    fun `should get multiple image files matching path prefix`() {
        // given
        val imageFiles = arrayOf(File("some1.jpg"), File("some2.jpg"))
        val imageFilePathPrefix = mockk<Path>(relaxed = true) {
            every { parent.toFile().listFiles(any<FilenameFilter>()) } returns imageFiles
        }

        // when then
        pdfImagesTool.getImageFilesMatchingPathPrefix(imageFilePathPrefix) shouldContainExactly imageFiles.asList()
    }

    @Test
    fun `should get no image files matching path prefix`() {
        // given
        val imageFiles = emptyArray<File>()
        val imageFilePathPrefix = mockk<Path>(relaxed = true) {
            every { parent.toFile().listFiles(any<FilenameFilter>()) } returns imageFiles
        }

        // when then
        pdfImagesTool.getImageFilesMatchingPathPrefix(imageFilePathPrefix) shouldContainExactly imageFiles.asList()
    }

    @Test
    fun `should convert to string`() {
        // given
        val pdfImagesTool = PdfImagesTool.builder()
            .timeoutSeconds(100)
            .build()

        // when then
        pdfImagesTool.toString() shouldMatch Regex("PdfImagesTool\\(executableFile=.+pdfimages(\\.exe)?, timeoutSeconds=100\\)")
    }

}