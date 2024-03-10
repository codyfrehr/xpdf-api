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

import io.kotest.matchers.shouldBe
import io.xpdf.api.pdftext.options.PdfTextEncoding
import io.xpdf.api.pdftext.options.PdfTextEndOfLine
import io.xpdf.api.pdftext.options.PdfTextFormat
import org.junit.jupiter.api.Test

class PdfTextOptionsTest {

    @Test
    fun `should convert to string`() {
        // given
        val options = PdfTextOptions.builder()
            .pageStart(1)
            .pageStop(5)
            .format(PdfTextFormat.LAYOUT)
            .encoding(PdfTextEncoding.UTF_8)
            .endOfLine(PdfTextEndOfLine.UNIX)
            .pageBreakExcluded(true)
            .ownerPassword("ownerPassword")
            .userPassword("userPassword")
            .build()

        // when then
        options.toString() shouldBe "PdfTextOptions(pageStart=1, pageStop=5, format=LAYOUT, encoding=UTF_8, endOfLine=UNIX, pageBreakExcluded=true, ownerPassword=ownerPassword, userPassword=userPassword, nativeOptions=null)"
    }

}