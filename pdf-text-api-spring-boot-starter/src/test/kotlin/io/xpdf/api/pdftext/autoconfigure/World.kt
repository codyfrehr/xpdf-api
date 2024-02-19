package io.xpdf.api.pdftext.autoconfigure

//import io.cucumber.spring.ScenarioScope
import io.xpdf.api.pdftext.PdfTextRequest
import org.springframework.boot.test.context.TestComponent

@TestComponent
//@ScenarioScope
open class World {
    lateinit var pdfTextRequest: PdfTextRequest
}