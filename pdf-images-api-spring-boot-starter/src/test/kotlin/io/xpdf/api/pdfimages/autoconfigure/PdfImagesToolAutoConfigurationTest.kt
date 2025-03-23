package io.xpdf.api.pdfimages.autoconfigure

import io.kotest.matchers.shouldBe
import io.mockk.*
import io.xpdf.api.common.util.XpdfUtils
import io.xpdf.api.pdfimages.PdfImagesTool
import io.xpdf.api.pdfimages.util.PdfImagesUtils
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

class PdfImagesToolAutoConfigurationTest {

    private var context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()

    companion object {
        private val pdfImagesToolBean = mockk<PdfImagesTool>()

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
    fun `should autoconfigure pdf images tool with properties`() {
        // given
        val executableFile: File = XpdfUtils.getXpdfTempPath().resolve("executableName").toFile().apply {
            mkdirs()
            createNewFile()
            deleteOnExit()
        }

        TestPropertyValues.of(
                "io.xpdf.api.pdf-images.executable-path=${executableFile.canonicalPath}",
                "io.xpdf.api.pdf-images.timeout-seconds=99"
        ).applyTo(context)

        context.register(PdfImagesToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfImagesTool = context.getBean(PdfImagesTool::class.java)

        // then
        pdfImagesTool.executableFile.canonicalPath shouldBe executableFile.canonicalPath
        pdfImagesTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should autoconfigure pdf images tool without properties`() {
        // given
        val executableFile = mockk<File> {
            every { exists() } returns true
            every { setExecutable(any()) } returns true
        }
        val executablePath = mockk<Path> {
            every { toFile() } returns executableFile
        }

        mockkStatic(PdfImagesUtils::class)
        every { PdfImagesUtils.getPdfImagesExecutablePath() } returns executablePath
        every { PdfImagesUtils.getPdfImagesTimeoutSeconds() } returns 99

        context.register(PdfImagesToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfImagesTool = context.getBean(PdfImagesTool::class.java)

        // then
        pdfImagesTool.executableFile shouldBe executableFile
        pdfImagesTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should not autoconfigure pdf images tool when bean already exists`() {
        // given
        context.register(PdfImagesToolConfig::class.java, PdfImagesToolAutoConfiguration::class.java)
        context.refresh()

        // when then
        context.getBean(PdfImagesTool::class.java) shouldBe pdfImagesToolBean
    }

    @Configuration
    open class PdfImagesToolConfig {
        @Bean
        open fun pdfImagesTool(): PdfImagesTool = pdfImagesToolBean
    }

}