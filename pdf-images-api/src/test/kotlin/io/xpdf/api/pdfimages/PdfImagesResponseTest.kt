package io.xpdf.api.pdfimages

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class PdfImagesResponseTest {

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfImagesResponse.builder()
            .imageFiles(listOf(File("some1.jpg"), File("some2.jpg")))
            .standardOutput("some standard output")
            .build()

        // when then
        request.toString() shouldBe "PdfImagesResponse(imageFiles=[some1.jpg, some2.jpg], standardOutput=some standard output)"
    }

}