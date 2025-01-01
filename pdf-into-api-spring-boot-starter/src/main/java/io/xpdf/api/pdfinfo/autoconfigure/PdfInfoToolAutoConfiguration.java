package io.xpdf.api.pdfinfo.autoconfigure;

import io.xpdf.api.pdfinfo.PdfInfoTool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * An autoconfiguration for {@link PdfInfoTool} with configuration properties defined in {@link PdfInfoToolProperties}.
 *
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(PdfInfoTool.class)
@EnableConfigurationProperties(PdfInfoToolProperties.class)
public class PdfInfoToolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PdfInfoTool pdfTextTool(PdfInfoToolProperties pdfInfoToolProperties) {
        return PdfInfoTool.builder()
                .executableFile(pdfInfoToolProperties.getExecutablePath() != null ? pdfInfoToolProperties.getExecutablePath().toFile() : null)
                .timeoutSeconds(pdfInfoToolProperties.getTimeoutSeconds())
                .build();
    }

}
