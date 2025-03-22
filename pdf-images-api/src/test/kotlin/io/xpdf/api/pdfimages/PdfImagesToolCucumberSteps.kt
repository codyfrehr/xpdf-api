package io.xpdf.api.pdfimages

import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.xpdf.api.common.exception.XpdfException
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdfimages.options.PdfImagesFileFormat
import io.xpdf.api.pdfimages.util.PdfImagesUtils
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterEach
import org.springframework.test.context.event.annotation.AfterTestClass
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class PdfImagesToolCucumberSteps {

    private var toolDto: PdfImagesToolDto? = null
    private var requestDto: PdfImagesRequestDto? = null
    private var optionsDto: PdfImagesOptionsDto? = null
    private var nativeOptionsDto: NativeOptionsDto? = null
    private var response: PdfImagesResponse? = null
    private var exception: XpdfException? = null

    companion object {
        @JvmStatic
        @AfterTestClass
        fun afterAll() {
            FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
        }
    }

    @AfterEach
    fun afterEach() {
        FileUtils.deleteQuietly(PdfImagesUtils.getPdfImagesTempOutputPath().toFile())
    }

    @Given("a PdfImagesTool")
    fun `a PdfImagesTool`() {
        toolDto = PdfImagesToolDto(null, null)
    }

    @Given("a PdfImagesTool with values")
    fun `a PdfImagesTool with values`(toolDto: PdfImagesToolDto) {
        this.toolDto = toolDto
    }

    /**
     * Cannot specify executable file path in values in feature file because path structure is OS-specific.
     * We don't know which OS this test will be running against.
     * This allows us to define an executable file, but in an OS-independent fashion.
     */
    @Given("a PdfImagesTool with {int} second timeout and dynamic executable file")
    fun `a PdfImagesTool with TIMEOUT_SECONDS second timeout and dynamic executable file`(timeoutSeconds: Int) {
        val executableResourceStream = this::class.java.classLoader.getResourceAsStream(PdfImagesUtils.getPdfImagesExecutableResourceName())!!
        val executableFile = XpdfUtils.getXpdfTempPath().resolve("some.exe").toFile()
        FileUtils.copyInputStreamToFile(executableResourceStream, executableFile)
        executableFile.setExecutable(true)

        toolDto = PdfImagesToolDto(executableFile, timeoutSeconds)
    }

    @Given("a PdfImagesRequest with values")
    fun `a PdfImagesRequest with values`(requestDto: PdfImagesRequestDto) {
        this.requestDto = requestDto
    }

    /**
     * Cannot specify image file path prefix in values in feature file because path structure is OS-specific.
     * We don't know which OS this test will be running against.
     * This allows us to define an output image file path prefix, but in an OS-independent fashion.
     */
    @Given("a PdfImagesRequest with pdf file {word} and dynamic image file path prefix")
    fun `a PdfImagesRequest with pdf file PDF_FILE_NAME and dynamic image file path prefix`(pdfFileName: String) {
        val pdfFile = File(this.javaClass.classLoader.getResource("pdfs/${pdfFileName}")!!.toURI())
        val imageFilePathPrefix = PdfImagesUtils.getPdfImagesTempOutputPath().resolve("some-prefix")

        this.requestDto = PdfImagesRequestDto(pdfFile, imageFilePathPrefix)
    }

    @Given("a PdfImagesOptions with values")
    fun `a PdfImagesOptions with values`(optionsDto: PdfImagesOptionsDto) {
        this.optionsDto = optionsDto
    }

    @Given("native PdfImagesOptions with values")
    fun `native PdfImagesOptions with values`(nativeOptionsDto: NativeOptionsDto) {
        this.nativeOptionsDto = nativeOptionsDto
    }

    @When("the PdfImagesTool processes the PdfImagesRequest")
    fun `the PdfImagesTool processes the PdfImagesRequest`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfImagesOptions(nativeOptions) else PdfImagesOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfImagesRequest(options)
        val tool = toolDto!!.toPdfImagesTool()

        response = tool.process(request)
    }

    @When("the PdfImagesTool processes the PdfImagesRequest expecting an XpdfException")
    fun `the PdfImagesTool processes the PdfImagesRequest expecting an XpdfException`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfImagesOptions(nativeOptions) else PdfImagesOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfImagesRequest(options)
        val tool = toolDto!!.toPdfImagesTool()

        exception = shouldThrow<XpdfException> {
            tool.process(request)
        }
    }

    @Then("{int} output image files should exist")
    fun `IMAGE_FILE_COUNT output image files should exist`(imageFileCount: Int) {
        response!!.imageFiles shouldHaveSize imageFileCount
        response!!.imageFiles.forEach {
            it.exists() shouldBe true
        }
    }

    @Then("the output image files should have {string} extension")
    fun `the output image files should have FILE_EXTENSION extension `(fileExtension: String) {
        response!!.imageFiles shouldHaveAtLeastSize 1
        response!!.imageFiles.forEach {
            it.extension shouldBe fileExtension
        }
    }

    @Then("the standard output should not be empty")
    fun `the standard output should not be empty`() {
        response!!.standardOutput shouldNotBe null
        response!!.standardOutput shouldNotBe ""
    }

    @Then("the standard output should match {string}")
    fun `the standard output should match PATTERN`(pattern: String) {
        response!!.standardOutput shouldContain Regex(pattern)
    }

    @Then("the XpdfException should be an {word}")
    fun `the XpdfException should be an XPDF_EXCEPTION_NAME`(xpdfExceptionName: String) {
        exception!!::class.java shouldBe Class.forName("io.xpdf.api.common.exception.${xpdfExceptionName}")
    }

    @DataTableType
    fun pdfImagesToolDtoTransformer(row: Map<String, String?>) = PdfImagesToolDto(
        row["executableFile"]?.let { Paths.get(it).toFile() },
        row["timeoutSeconds"]?.toInt(),
    )

    @DataTableType
    fun pdfImagesRequestDtoTransformer(row: Map<String, String?>) = PdfImagesRequestDto(
        File(this.javaClass.classLoader.getResource("pdfs/${row["pdfFile"]!!}")!!.toURI()),
        row["imageFilePathPrefix"]?.let { PdfImagesUtils.getPdfImagesTempOutputPath().resolve(it) },
    )

    @DataTableType
    fun pdfImagesOptionsDtoTransformer(row: Map<String, String?>) = PdfImagesOptionsDto(
        row["pageStart"]?.toIntOrNull(),
        row["pageStop"]?.toIntOrNull(),
        row["fileFormat"]?.let { PdfImagesFileFormat.valueOf(it) },
        row["metadataIncluded"]?.toBooleanStrictOrNull(),
        row["ownerPassword"],
        row["userPassword"],
    )

    @DataTableType
    fun nativeOptionsDtoTransformer(row: Map<String, String?>) = NativeOptionsDto(row)

    class PdfImagesToolDto (
        private val executableFile: File?,
        private val timeoutSeconds: Int?,
    ) {
        fun toPdfImagesTool(): PdfImagesTool = PdfImagesTool.builder()
            .executableFile(executableFile)
            .timeoutSeconds(timeoutSeconds)
            .build()
    }

    class PdfImagesRequestDto (
        private val pdfFile: File,
        private val imageFilePathPrefix: Path?,
    ) {
        fun toPdfImagesRequest(options: PdfImagesOptions?): PdfImagesRequest = PdfImagesRequest.builder()
            .pdfFile(pdfFile)
            .imageFilePathPrefix(imageFilePathPrefix)
            .options(options)
            .build()
    }

    class PdfImagesOptionsDto(
        private val pageStart: Int?,
        private val pageStop: Int?,
        private val fileFormat: PdfImagesFileFormat?,
        private val metadataIncluded: Boolean?,
        private val ownerPassword: String?,
        private val userPassword: String?,
    ) {
        fun toPdfImagesOptions(nativeOptions: Map<String, String?>?): PdfImagesOptions = PdfImagesOptions.builder()
            .pageStart(pageStart)
            .pageStop(pageStop)
            .fileFormat(fileFormat)
            .metadataIncluded(metadataIncluded)
            .ownerPassword(ownerPassword)
            .userPassword(userPassword)
            .nativeOptions(nativeOptions)
            .build()
    }

    class NativeOptionsDto(
        private val nativeOptions: Map<String, String?>,
    ) {
        fun toNativeOptions(): Map<String, String?> = nativeOptions
    }

}