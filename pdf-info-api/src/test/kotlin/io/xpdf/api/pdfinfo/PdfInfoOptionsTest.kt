package io.xpdf.api.pdfinfo

import io.kotest.matchers.shouldBe
import io.xpdf.api.pdfinfo.options.PdfInfoEncoding
import org.junit.jupiter.api.Test

class PdfInfoOptionsTest {

    @Test
    fun `should convert to string`() {
        // given
        val options = PdfInfoOptions.builder()
            .pageStart(1)
            .pageStop(5)
            .encoding(PdfInfoEncoding.UTF_8)
            .boundingBoxesIncluded(true)
            .metadataIncluded(true)
            .datesUndecoded(true)
            .ownerPassword("ownerPassword")
            .userPassword("userPassword")
            .build()

        // when then
        options.toString() shouldBe "PdfInfoOptions(pageStart=1, pageStop=5, encoding=UTF_8, boundingBoxesIncluded=true, metadataIncluded=true, datesUndecoded=true, ownerPassword=ownerPassword, userPassword=userPassword, nativeOptions=null)"
    }

}