package io.xpdf.api.pdfinfo

import io.cucumber.java.After
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.xpdf.api.common.util.XpdfUtils
import org.apache.commons.io.FileUtils
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasspathResource("io/xpdf/api/pdfinfo")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "io.xpdf.api.pdfinfo")
class PdfInfoToolCucumberIT {

    @After
    fun after() {
        FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
    }

}