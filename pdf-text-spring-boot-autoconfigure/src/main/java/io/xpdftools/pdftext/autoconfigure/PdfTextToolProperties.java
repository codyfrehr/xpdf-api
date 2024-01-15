package io.xpdftools.pdftext.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

//todo: javadoc
@Data
@ConfigurationProperties(prefix = "xpdftools.pdf-text")
public class PdfTextToolProperties {

    private String defaultOutputDirectory;
    private Long timeoutMilliseconds;

}
