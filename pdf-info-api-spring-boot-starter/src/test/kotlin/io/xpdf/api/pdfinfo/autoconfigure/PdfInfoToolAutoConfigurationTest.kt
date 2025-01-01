/*
 * PdfInfo API Starter - A Spring Boot starter for PdfInfo API (https://xpdf.io)
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
package io.xpdf.api.pdfinfo.autoconfigure

import io.kotest.matchers.shouldBe
import io.mockk.*
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdfinfo.PdfInfoTool
import io.xpdf.api.pdfinfo.util.PdfInfoUtils
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

class PdfInfoToolAutoConfigurationTest {

    private var context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()

    companion object {
        private val pdfInfoToolBean = mockk<PdfInfoTool>()

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
    fun `should autoconfigure pdf info tool with properties`() {
        // given
        val executableFile: File = XpdfUtils.getXpdfTempPath().resolve("executableName").toFile().apply {
            mkdirs()
            createNewFile()
            deleteOnExit()
        }

        TestPropertyValues.of(
                "io.xpdf.api.pdf-info.executable-path=${executableFile.canonicalPath}",
                "io.xpdf.api.pdf-info.timeout-seconds=99"
        ).applyTo(context)

        context.register(PdfInfoToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfInfoTool::class.java)

        // then
        pdfTextTool.executableFile.canonicalPath shouldBe executableFile.canonicalPath
        pdfTextTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should autoconfigure pdf info tool without properties`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }
        val executablePath = mockk<Path> {
            every { toFile() } returns executableFile
        }

        mockkStatic(PdfInfoUtils::class)
        every { PdfInfoUtils.getPdfInfoExecutablePath() } returns executablePath
        every { PdfInfoUtils.getPdfInfoTimeoutSeconds() } returns 99

        context.register(PdfInfoToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfInfoTool::class.java)

        // then
        pdfTextTool.executableFile shouldBe executableFile
        pdfTextTool.timeoutSeconds shouldBe 99

        unmockkStatic(PdfInfoUtils::class)
    }

    @Test
    fun `should not autoconfigure pdf info tool when bean already exists`() {
        // given
        context.register(PdfInfoToolConfig::class.java, PdfInfoToolAutoConfiguration::class.java)
        context.refresh()

        // when then
        context.getBean(PdfInfoTool::class.java) shouldBe pdfInfoToolBean
    }

    @Configuration
    open class PdfInfoToolConfig {
        @Bean
        open fun pdfInfoTool(): PdfInfoTool = pdfInfoToolBean
    }

}