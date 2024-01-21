import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.xpdftools.common.util.XpdfUtils
import io.xpdftools.pdftext.PdfTextTool
import io.xpdftools.pdftext.autoconfigure.PdfTextToolAutoConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths


class PdfTextToolAutoConfigurationTest {

    private var context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()

    @BeforeEach
    fun beforeEach() {
        context = AnnotationConfigApplicationContext()
    }

    @AfterEach
    fun afterEach() {
        context.close()
    }

    @Test
    fun `should autoconfigure pdf text tool from properties`() {
        // given
        val nativeLibrary: File = Paths.get(System.getProperty("java.io.tmpdir"), "nativeLibrary").toFile().apply {
            createNewFile()
            deleteOnExit()
        }

        TestPropertyValues.of(
                "xpdf-tools.pdf-text.native-library-path=${nativeLibrary.canonicalPath}",
                "xpdf-tools.pdf-text.default-output-path=defaultOutputPath",
                "xpdf-tools.pdf-text.timeout-seconds=99"
        ).applyTo(context)

        context.register(PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfTextTool::class.java)

        // then
        pdfTextTool.nativeLibraryPath shouldBe nativeLibrary.toPath()
        pdfTextTool.defaultOutputPath shouldBe Paths.get("defaultOutputPath")
        pdfTextTool.timeoutSeconds shouldBe 99
    }

    @Test
    fun `should autoconfigure pdf text tool from xpdf utils`() {
        // given
        val nativeLibraryPath = mockk<Path> {
            every { toFile().exists() } returns true
        }
        val defaultOutputPath = mockk<Path>()

        mockkStatic(XpdfUtils::class)
        every { XpdfUtils.getPdfTextNativeLibraryPath() } returns nativeLibraryPath
        every { XpdfUtils.getPdfTextDefaultOutputPath() } returns defaultOutputPath
        every { XpdfUtils.getPdfTextTimeoutSeconds() } returns 99

        context.register(PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when
        val pdfTextTool = context.getBean(PdfTextTool::class.java)

        // then
        pdfTextTool.nativeLibraryPath shouldBe nativeLibraryPath
        pdfTextTool.defaultOutputPath shouldBe defaultOutputPath
        pdfTextTool.timeoutSeconds shouldBe 99

        unmockkStatic(XpdfUtils::class)
    }

    @Test
    fun `should not autoconfigure pdf text tool when bean already exists`() {
        // given
        context.register(PdfTextToolConfig::class.java, PdfTextToolAutoConfiguration::class.java)
        context.refresh()

        // when then
        context.getBean(PdfTextTool::class.java) shouldBe pdfTextToolBean
    }

    companion object {
        private val pdfTextToolBean = mockk<PdfTextTool>()
    }

    @Configuration
    open class PdfTextToolConfig {
        @Bean
        open fun pdfTextTool(): PdfTextTool = pdfTextToolBean
    }

}