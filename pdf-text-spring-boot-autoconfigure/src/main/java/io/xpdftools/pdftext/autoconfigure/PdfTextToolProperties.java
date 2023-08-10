package io.xpdftools.pdftext.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xpdftools.pdftext")
public class PdfTextToolProperties {

    private String binaryPath;
    private String outputDirectory;

}
