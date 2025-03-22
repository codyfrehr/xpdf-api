package io.xpdf.api.pdfimages.autoconfigure;

import io.xpdf.api.pdfimages.PdfImagesTool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfImagesTool} that is autoconfigured in {@link PdfImagesToolAutoConfiguration}.
 *
 * @since 1.2.0
 */
@Data
@ConfigurationProperties(prefix = "io.xpdf.api.pdf-images")
public class PdfImagesToolProperties {

    private Path executablePath;
    private Integer timeoutSeconds;

}
