/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
 * Copyright © 2025 xpdf.io (info@xpdf.io)
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