package io.xpdf.api.pdfinfo

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PdfInfoResponseTest {

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfInfoResponse.builder()
            .standardOutput("some standard output")
            .build()

        // when then
        request.toString() shouldBe "PdfInfoResponse(standardOutput=some standard output)"
    }

}