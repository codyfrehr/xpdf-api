package io.xpdftools.pdftext.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xpdftools.pdf-text")
public class PdfTextToolProperties {

    private String defaultOutputDirectory;

}
