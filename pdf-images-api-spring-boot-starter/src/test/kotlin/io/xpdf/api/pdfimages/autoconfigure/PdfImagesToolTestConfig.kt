package io.xpdf.api.pdfimages.autoconfigure

import io.xpdf.api.pdfimages.PdfImagesTool
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan
open class PdfImagesToolTestConfig {

    @Bean
    open fun pdfImagesTool(): PdfImagesTool = PdfImagesTool.builder().build()

}