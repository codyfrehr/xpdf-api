/*
 * PdfText API Starter - A Spring Boot starter for PdfText API.
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