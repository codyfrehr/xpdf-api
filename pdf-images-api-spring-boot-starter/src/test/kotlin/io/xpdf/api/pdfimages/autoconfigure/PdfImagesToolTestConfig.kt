/*
 * PdfImages API Starter - A Spring Boot starter for PdfImages API (https://xpdf.io)
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