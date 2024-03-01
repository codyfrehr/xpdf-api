package io.xpdf.api.pdftext

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class PdfTextResponseTest {

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfTextResponse.builder()
            .textFile(File("some.text"))
            .standardOutput("some standard output")
            .build()

        // when then
        request.toString() shouldBe "PdfTextResponse(textFile=some.text, standardOutput=some standard output)"
    }

}