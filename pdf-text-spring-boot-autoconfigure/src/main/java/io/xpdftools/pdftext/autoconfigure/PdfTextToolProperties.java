package io.xpdftools.pdftext.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

//todo: javadoc
@Data
@ConfigurationProperties(prefix = "xpdftools.pdf-text")
public class PdfTextToolProperties {

    //todo: what is correct way to deal with "Path" type?
    private Path libraryPath;
    private String defaultOutputDirectory;
    //todo: can we use "Long" or should it be primitive?
    private Long timeoutMilliseconds;

}
