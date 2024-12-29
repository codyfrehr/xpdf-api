package io.xpdf.api.pdfinfo

import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.xpdf.api.common.exception.XpdfException
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdfinfo.options.PdfInfoEncoding
import io.xpdf.api.pdfinfo.util.PdfInfoUtils
import org.apache.commons.io.FileUtils
import org.springframework.test.context.event.annotation.AfterTestClass
import java.io.File
import java.nio.file.Paths

class PdfInfoToolCucumberSteps {

    private var toolDto: PdfInfoToolDto? = null
    private var requestDto: PdfInfoRequestDto? = null
    private var optionsDto: PdfInfoOptionsDto? = null
    private var nativeOptionsDto: NativeOptionsDto? = null
    private var response: PdfInfoResponse? = null
    private var exception: XpdfException? = null

    companion object {
        @JvmStatic
        @AfterTestClass
        fun afterAll() {
            FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
        }
    }

    @Given("a PdfInfoTool")
    fun `a PdfInfoTool`() {
        toolDto = PdfInfoToolDto(null, null)
    }

    /**
     * Cannot specify executable file path in values in feature file because path structure is OS-specific.
     * We don't know which OS this test will be running against.
     * This allows us to define an executable file, but in an OS-independent fashion.
     */
    @Given("a PdfInfoTool with {int} second timeout and dynamic executable file")
    fun `a PdfInfoTool with TIMEOUT_SECONDS second timeout and dynamic executable file`(timeoutSeconds: Int) {
        val executableResourceStream = this::class.java.classLoader.getResourceAsStream(PdfInfoUtils.getPdfInfoExecutableResourceName())!!
        val executableFile = XpdfUtils.getXpdfTempPath().resolve("some.exe").toFile()
        FileUtils.copyInputStreamToFile(executableResourceStream, executableFile)
        executableFile.setExecutable(true)

        toolDto = PdfInfoToolDto(executableFile, timeoutSeconds)
    }

    @Given("a PdfInfoRequest with values")
    fun `a PdfInfoRequest with values`(requestDto: PdfInfoRequestDto) {
        this.requestDto = requestDto
    }

    @Given("a PdfInfoOptions with values")
    fun `a PdfInfoOptions with values`(optionsDto: PdfInfoOptionsDto) {
        this.optionsDto = optionsDto
    }

    @Given("native PdfInfoOptions with values")
    fun `native PdfInfoOptions with values`(nativeOptionsDto: NativeOptionsDto) {
        this.nativeOptionsDto = nativeOptionsDto
    }

    @When("the PdfInfoTool processes the PdfInfoRequest")
    fun `the PdfInfoTool processes the PdfInfoRequest`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfInfoOptions(nativeOptions) else PdfInfoOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfInfoRequest(options)
        val tool = toolDto!!.toPdfInfoTool()

        response = tool.process(request)
    }

    @When("the PdfInfoTool processes the PdfInfoRequest expecting an XpdfException")
    fun `the PdfInfoTool processes the PdfInfoRequest expecting an XpdfException`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfInfoOptions(nativeOptions) else PdfInfoOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfInfoRequest(options)
        val tool = toolDto!!.toPdfInfoTool()

        exception = shouldThrow<XpdfException> {
            tool.process(request)
        }
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
    fun pdfInfoToolDtoTransformer(row: Map<String, String?>) = PdfInfoToolDto(
        row["executableFile"]?.let { Paths.get(it).toFile() },
        row["timeoutSeconds"]?.toInt(),
    )

    @DataTableType
    fun pdfInfoRequestDtoTransformer(row: Map<String, String?>) = PdfInfoRequestDto(
        File(this.javaClass.classLoader.getResource("pdfs/${row["pdfFile"]!!}")!!.toURI()),
    )

    @DataTableType
    fun pdfInfoOptionsDtoTransformer(row: Map<String, String?>) = PdfInfoOptionsDto(
        row["pageStart"]?.toIntOrNull(),
        row["pageStop"]?.toIntOrNull(),
        row["encoding"]?.let { PdfInfoEncoding.valueOf(it) },
        row["boundingBoxesIncluded"]?.toBooleanStrictOrNull(),
        row["metadataIncluded"]?.toBooleanStrictOrNull(),
        row["datesUndecoded"]?.toBooleanStrictOrNull(),
        row["ownerPassword"],
        row["userPassword"],
    )

    @DataTableType
    fun nativeOptionsDtoTransformer(row: Map<String, String?>) = NativeOptionsDto(row)

    class PdfInfoToolDto (
        private val executableFile: File?,
        private val timeoutSeconds: Int?,
    ) {
        fun toPdfInfoTool(): PdfInfoTool = PdfInfoTool.builder()
            .executableFile(executableFile)
            .timeoutSeconds(timeoutSeconds)
            .build()
    }

    class PdfInfoRequestDto (
        private val pdfFile: File,
    ) {
        fun toPdfInfoRequest(options: PdfInfoOptions?): PdfInfoRequest = PdfInfoRequest.builder()
            .pdfFile(pdfFile)
            .options(options)
            .build()
    }

    class PdfInfoOptionsDto(
        private val pageStart: Int?,
        private val pageStop: Int?,
        private val encoding: PdfInfoEncoding?,
        private val boundingBoxesIncluded: Boolean?,
        private val metadataIncluded: Boolean?,
        private val datesUndecoded: Boolean?,
        private val ownerPassword: String?,
        private val userPassword: String?,
    ) {
        fun toPdfInfoOptions(nativeOptions: Map<String, String?>?): PdfInfoOptions = PdfInfoOptions.builder()
            .pageStart(pageStart)
            .pageStop(pageStop)
            .encoding(encoding)
            .boundingBoxesIncluded(boundingBoxesIncluded)
            .metadataIncluded(metadataIncluded)
            .datesUndecoded(datesUndecoded)
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