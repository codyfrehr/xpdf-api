package io.xpdf.api.pdfimages

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class PdfImagesRequestTest {

    @Test
    fun `should throw exception when initializing if pdf file is null`() {
        // when then
        shouldThrow<NullPointerException> {
            PdfImagesRequest.builder().build()
        }
    }

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfImagesRequest.builder()
            .pdfFile(File("some.pdf"))
            .imageFilePathPrefix(Paths.get("some.path"))
            .build()

        // when then
        request.toString() shouldBe "PdfImagesRequest(pdfFile=some.pdf, imageFilePathPrefix=some.path, options=null)"
    }

}