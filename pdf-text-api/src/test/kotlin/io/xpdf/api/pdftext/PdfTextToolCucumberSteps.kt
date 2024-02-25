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

import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.xpdf.api.common.exception.XpdfException
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdftext.options.PdfTextEncoding
import io.xpdf.api.pdftext.options.PdfTextEndOfLine
import io.xpdf.api.pdftext.options.PdfTextFormat
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.file.Paths

class PdfTextToolCucumberSteps {
    private var toolDto: PdfTextToolDto? = null
    private var requestDto: PdfTextRequestDto? = null
    private var optionsDto: PdfTextOptionsDto? = null
    private var nativeOptionsDto: NativeOptionsDto? = null
    private var response: PdfTextResponse? = null
    private var exception: XpdfException? = null

    @Given("a PdfTextTool")
    fun `a PdfTextTool`() {
        toolDto = PdfTextToolDto(null, null)
    }

    @Given("a PdfTextTool with values")
    fun `a PdfTextTool with values`(toolDto: PdfTextToolDto) {
        this.toolDto = toolDto
    }

    /**
     * Cannot specify executable file path in values in feature file because path structure is OS-specific.
     * We don't know which OS this test will be running against.
     * This allows us to define an executable file, but in an OS-independent fashion.
     */
    @Given("a PdfTextTool with {int} second timeout and dynamic executable file")
    fun `a PdfTextTool with TIMEOUT_SECONDS second timeout and dynamic executable file`(timeoutSeconds: Int) {
        val executableResourceStream = this::class.java.classLoader.getResourceAsStream(XpdfUtils.getPdfTextExecutableResourceName())!!
        val executableFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("some.exe").toFile()
        FileUtils.copyInputStreamToFile(executableResourceStream, executableFile)
        executableFile.setExecutable(true)

        toolDto = PdfTextToolDto(executableFile, timeoutSeconds)
    }

    @Given("a PdfTextRequest with values")
    fun `a PdfTextRequest with values`(requestDto: PdfTextRequestDto) {
        this.requestDto = requestDto
    }

    /**
     * Cannot specify text file path in values in feature file because path structure is OS-specific.
     * We don't know which OS this test will be running against.
     * This allows us to define an output text file, but in an OS-independent fashion.
     */
    @Given("a PdfTextRequest with pdf file {word} and dynamic text file")
    fun `a PdfTextRequest with pdf file PDF_FILE_NAME and dynamic text file`(pdfFileName: String) {
        val pdfFile = File(this.javaClass.classLoader.getResource("pdfs/${pdfFileName}")!!.toURI())
        val txtFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("some.txt").toFile()

        this.requestDto = PdfTextRequestDto(pdfFile, txtFile)
    }

    @Given("a PdfTextOptions with values")
    fun `a PdfTextOptions with values`(optionsDto: PdfTextOptionsDto) {
        this.optionsDto = optionsDto
    }

    @Given("native PdfTextOptions with values")
    fun `native PdfTextOptions with values`(nativeOptionsDto: NativeOptionsDto) {
        this.nativeOptionsDto = nativeOptionsDto
    }

    @When("the PdfTextTool processes the PdfTextRequest")
    fun `the PdfTextTool processes the PdfTextRequest`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfTextOptions(nativeOptions) else PdfTextOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfTextRequest(options)
        val tool = toolDto!!.toPdfTextTool()

        response = tool.process(request)
    }

    @When("the PdfTextTool processes the PdfTextRequest expecting an XpdfException")
    fun `the PdfTextTool processes the PdfTextRequest expecting an XpdfException`() {
        val nativeOptions = nativeOptionsDto?.toNativeOptions()
        val options = if (optionsDto != null) optionsDto?.toPdfTextOptions(nativeOptions) else PdfTextOptions.builder().nativeOptions(nativeOptions).build()
        val request = requestDto!!.toPdfTextRequest(options)
        val tool = toolDto!!.toPdfTextTool()

        exception = shouldThrow<XpdfException> {
            tool.process(request)
        }
    }

    @Then("the output text should match {string}")
    fun `the output text should match PATTERN`(pattern: String) {
        FileUtils.readFileToString(response!!.textFile, Charsets.UTF_8) shouldMatch Regex(pattern)
    }

    @Then("the output text file should exist")
    fun `the output text file should exist`() {
        response!!.textFile.exists() shouldBe true
    }

    @Then("the standard output should not be null")
    fun `the standard output should not be empty`() {
        response!!.standardOutput shouldNotBe null
        response!!.standardOutput shouldNotBe ""
    }

    @Then("the XpdfException should be an {word}")
    fun `the XpdfException should be an XPDF_EXCEPTION_NAME`(xpdfExceptionName: String) {
        exception!!::class.java shouldBe Class.forName("io.xpdf.api.common.exception.${xpdfExceptionName}")
    }

    @DataTableType
    fun pdfTextToolDtoTransformer(row: Map<String, String?>) = PdfTextToolDto(
        row["executableFile"]?.let { Paths.get(it).toFile() },
        row["timeoutSeconds"]?.toInt(),
    )

    @DataTableType
    fun pdfTextRequestDtoTransformer(row: Map<String, String?>) = PdfTextRequestDto(
        File(this.javaClass.classLoader.getResource("pdfs/${row["pdfFile"]!!}")!!.toURI()),
        row["textFile"]?.let { Paths.get(System.getProperty("java.io.tmpdir")).resolve(it).toFile() },
    )

    @DataTableType
    fun pdfTextOptionsDtoTransformer(row: Map<String, String?>) = PdfTextOptionsDto(
        row["pageStart"]?.toIntOrNull(),
        row["pageStop"]?.toIntOrNull(),
        row["format"]?.let { PdfTextFormat.valueOf(it) },
        row["encoding"]?.let { PdfTextEncoding.valueOf(it) },
        row["endOfLine"]?.let { PdfTextEndOfLine.valueOf(it) },
        row["pageBreakExcluded"]?.toBooleanStrictOrNull(),
        row["ownerPassword"],
        row["userPassword"],
    )

    @DataTableType
    fun nativeOptionsDtoTransformer(row: Map<String, String?>) = NativeOptionsDto(row)

    class PdfTextToolDto (
        private val executableFile: File?,
        private val timeoutSeconds: Int?,
    ) {
        fun toPdfTextTool(): PdfTextTool = PdfTextTool.builder()
            .executableFile(executableFile)
            .timeoutSeconds(timeoutSeconds)
            .build()
    }

    class PdfTextRequestDto (
        private val pdfFile: File,
        private val textFile: File?,
    ) {
        fun toPdfTextRequest(options: PdfTextOptions?): PdfTextRequest = PdfTextRequest.builder()
            .pdfFile(pdfFile)
            .textFile(textFile)
            .options(options)
            .build()
    }

    class PdfTextOptionsDto(
        private val pageStart: Int?,
        private val pageStop: Int?,
        private val format: PdfTextFormat?,
        private val encoding: PdfTextEncoding?,
        private val endOfLine: PdfTextEndOfLine?,
        private val pageBreakExcluded: Boolean?,
        private val ownerPassword: String?,
        private val userPassword: String?,
    ) {
        fun toPdfTextOptions(nativeOptions: Map<String, String?>?): PdfTextOptions = PdfTextOptions.builder()
            .pageStart(pageStart)
            .pageStop(pageStop)
            .format(format)
            .encoding(encoding)
            .endOfLine(endOfLine)
            .pageBreakExcluded(pageBreakExcluded)
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