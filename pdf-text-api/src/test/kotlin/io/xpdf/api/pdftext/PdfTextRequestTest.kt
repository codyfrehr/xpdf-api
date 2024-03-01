package io.xpdf.api.pdftext

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class PdfTextRequestTest {

    @Test
    fun `should throw exception when initializing if pdf file is null`() {
        // when then
        shouldThrow<NullPointerException> {
            PdfTextRequest.builder().build()
        }
    }

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfTextRequest.builder()
            .pdfFile(File("some.pdf"))
            .textFile(File("some.text"))
            .build()

        // when then
        request.toString() shouldBe "PdfTextRequest(pdfFile=some.pdf, textFile=some.text, options=null)"
    }

}