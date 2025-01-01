package io.xpdf.api.pdfinfo.autoconfigure;

import io.xpdf.api.pdfinfo.PdfInfoTool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfInfoTool} that is autoconfigured in {@link PdfInfoToolAutoConfiguration}.
 *
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "io.xpdf.api.pdf-info")
public class PdfInfoToolProperties {

    private Path executablePath;
    private Integer timeoutSeconds;

}
