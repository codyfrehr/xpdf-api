package io.xpdftools.pdftext.autoconfigure;

import io.xpdftools.pdftext.PdfTextTool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfTextTool} that is autoconfigured in {@link PdfTextToolAutoConfiguration}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Data
@ConfigurationProperties(prefix = "xpdf-tools.pdf-text")
public class PdfTextToolProperties {

    private Path nativeLibraryPath;
    private Integer timeoutSeconds;

}
