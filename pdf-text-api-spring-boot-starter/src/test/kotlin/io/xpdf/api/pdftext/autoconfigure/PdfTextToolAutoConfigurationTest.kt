/*
 * PdfText API Starter - A Spring Boot starter for PdfText API (https://xpdf.io)
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

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdftext.PdfTextTool
import io.xpdf.api.pdftext.util.PdfTextUtils
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Path

class PdfTextToolAutoConfigurationTest {

    private var context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()

    companion object {
        private val pdfTextToolBean = mockk<PdfTextTool>()

        @JvmStatic
        @AfterAll
        fun afterAll() {
            FileUtils.deleteQuietly(XpdfUtils.getXpdfTempPath().toFile())
        }
    }

    @BeforeEach
    fun beforeEach() {
        context = AnnotationConfigApplicationContext()
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
        context.close()
    }

    @Test
    fun `should autoconfigure pdf text tool with properties`() {
        // given
        val executableFile: File = XpdfUtils.getXpdfTempPath().resolve("executableName").toFile().apply {
            mkdirs()
            createNewFile()
            deleteOnExit()
        }

        TestPropertyValues.of(
                "io.xpdf.api.pdf-text.executable-path=${executableFile.canonicalPath}",
                "io.xpdf.api.pdf-text.timeout-seconds=99"
        ).applyTo(context)

        context.register(PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfTextTool::class.java)

        // then
        pdfTextTool.executableFile.canonicalPath shouldBe executableFile.canonicalPath
        pdfTextTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should autoconfigure pdf text tool without properties`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }
        val executablePath = mockk<Path> {
            every { toFile() } returns executableFile
        }

        mockkStatic(PdfTextUtils::class)
        every { PdfTextUtils.getPdfTextExecutablePath() } returns executablePath
        every { PdfTextUtils.getPdfTextTimeoutSeconds() } returns 99

        context.register(PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfTextTool::class.java)

        // then
        pdfTextTool.executableFile shouldBe executableFile
        pdfTextTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should not autoconfigure pdf text tool when bean already exists`() {
        // given
        context.register(PdfTextToolConfig::class.java, PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when then
        context.getBean(PdfTextTool::class.java) shouldBe pdfTextToolBean
    }

    @Configuration
    open class PdfTextToolConfig {
        @Bean
        open fun pdfTextTool(): PdfTextTool = pdfTextToolBean
    }

}