package io.xpdf.api.pdftext.autoconfigure;

import io.xpdf.api.pdftext.PdfTextTool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfTextTool} that is autoconfigured in {@link PdfTextToolAutoConfiguration}.
 *
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "xpdf-api.pdf-text")
public class PdfTextToolProperties {

    private Path nativeLibraryPath;
    private Integer timeoutSeconds;

}
