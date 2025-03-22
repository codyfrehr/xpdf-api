package io.xpdf.api.pdfimages.autoconfigure;

import io.xpdf.api.pdfimages.PdfImagesTool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * An autoconfiguration for {@link PdfImagesTool} with configuration properties defined in {@link PdfImagesToolProperties}.
 *
 * @since 1.2.0
 */
@AutoConfiguration
@ConditionalOnClass(PdfImagesTool.class)
@EnableConfigurationProperties(PdfImagesToolProperties.class)
public class PdfImagesToolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PdfImagesTool pdfImagesTool(PdfImagesToolProperties pdfImagesToolProperties) {
        return PdfImagesTool.builder()
                .executableFile(pdfImagesToolProperties.getExecutablePath() != null ? pdfImagesToolProperties.getExecutablePath().toFile() : null)
                .timeoutSeconds(pdfImagesToolProperties.getTimeoutSeconds())
                .build();
    }

}
