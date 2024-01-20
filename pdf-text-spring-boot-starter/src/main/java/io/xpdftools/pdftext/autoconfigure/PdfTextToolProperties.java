package io.xpdftools.pdftext.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@code PdfTextTool} that are enabled in {@code PdfTextToolAutoConfiguration}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Data
@ConfigurationProperties(prefix = "xpdf-tools.pdf-text")
public class PdfTextToolProperties {

    private Path nativeLibraryPath;
    private Path defaultOutputPath;
    private Long timeoutMilliseconds;

}
