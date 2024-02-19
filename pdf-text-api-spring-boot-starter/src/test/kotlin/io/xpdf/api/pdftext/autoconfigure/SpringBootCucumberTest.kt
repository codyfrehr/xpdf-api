package io.xpdf.api.pdftext.autoconfigure

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
//    properties = [],
    classes = [
        PdfTextToolTestConfig::class,
        World::class,
    ]
)
class SpringBootCucumberTest