package io.xpdf.api.pdfinfo.autoconfigure

import io.xpdf.api.pdfinfo.PdfInfoTool
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan
open class PdfInfoToolTestConfig {

    @Bean
    open fun pdfTextTool(): PdfInfoTool = PdfInfoTool.builder().build()

}