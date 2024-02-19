package io.xpdf.api.pdftext.autoconfigure

import io.xpdf.api.pdftext.PdfTextTool
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan
open class PdfTextToolTestConfig {

    @Bean
    open fun pdfTextTool(): PdfTextTool = PdfTextTool.builder().build()

}