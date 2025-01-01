/*
 * PdfText API - An API for accessing a native pdftotext library (https://xpdf.io)
 * Copyright © 2024 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.xpdf.api.pdftext

import io.cucumber.java.After
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.xpdf.api.common.util.XpdfUtils
import org.apache.commons.io.FileUtils
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasspathResource("io/xpdf/api/pdftext")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "io.xpdf.api.pdftext")
class PdfTextToolCucumberIT {

    @After
    fun after() {
        FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
    }

}