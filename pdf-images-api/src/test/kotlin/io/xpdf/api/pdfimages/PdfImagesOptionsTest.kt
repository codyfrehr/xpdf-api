package io.xpdf.api.pdfimages

import io.kotest.matchers.shouldBe
import io.xpdf.api.pdfimages.options.PdfImagesFileFormat
import org.junit.jupiter.api.Test

class PdfImagesOptionsTest {

    @Test
    fun `should convert to string`() {
        // given
        val options = PdfImagesOptions.builder()
            .pageStart(1)
            .pageStop(5)
            .fileFormat(PdfImagesFileFormat.JPEG)
            .metadataIncluded(true)
            .ownerPassword("ownerPassword")
            .userPassword("userPassword")
            .build()

        // when then
        options.toString() shouldBe "PdfImagesOptions(pageStart=1, pageStop=5, fileFormat=JPEG, metadataIncluded=true, ownerPassword=ownerPassword, userPassword=userPassword, nativeOptions=null)"
    }

}