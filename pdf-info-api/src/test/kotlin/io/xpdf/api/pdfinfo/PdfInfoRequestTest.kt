/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
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
package io.xpdf.api.pdfinfo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File

class PdfInfoRequestTest {

    @Test
    fun `should throw exception when initializing if pdf file is null`() {
        // when then
        shouldThrow<NullPointerException> {
            PdfInfoRequest.builder().build()
        }
    }

    @Test
    fun `should convert to string`() {
        // given
        val request = PdfInfoRequest.builder()
            .pdfFile(File("some.pdf"))
            .build()

        // when then
        request.toString() shouldBe "PdfInfoRequest(pdfFile=some.pdf, options=null)"
    }

}