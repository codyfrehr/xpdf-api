package io.xpdf.api.pdfinfo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class PdfInfoRequestTest {

    @Test
    fun `should throw exception when initializing if pdf file is null`() {
        // when then
        shouldThrow<NullPointerException> {
            PdfInfoRequest.builder().build()
        }
    }

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfInfoRequest.builder()
            .pdfFile(File("some.pdf"))
            .build()

        // when then
        request.toString() shouldBe "PdfInfoRequest(pdfFile=some.pdf, options=null)"
    }

}