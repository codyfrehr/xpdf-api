package io.xpdf.api.pdftext.autoconfigure;

import io.xpdf.api.common.util.XpdfUtils;
import io.xpdf.api.pdftext.PdfTextTool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

/**
 * An autoconfiguration for {@link PdfTextTool} with configuration properties defined in {@link PdfTextToolProperties}.
 *
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(PdfTextTool.class)
@EnableConfigurationProperties(PdfTextToolProperties.class)
public class PdfTextToolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PdfTextTool pdfTextTool(PdfTextToolProperties pdfTextToolProperties) {
        Path nativeLibraryPath = pdfTextToolProperties.getNativeLibraryPath() == null
                ? XpdfUtils.getPdfTextNativeLibraryPath()
                : pdfTextToolProperties.getNativeLibraryPath();

        Integer timeoutSeconds = pdfTextToolProperties.getTimeoutSeconds() == null
                ? XpdfUtils.getPdfTextTimeoutSeconds()
                : pdfTextToolProperties.getTimeoutSeconds();

        return PdfTextTool.builder()
                .nativeLibraryPath(nativeLibraryPath)
                .timeoutSeconds(timeoutSeconds)
                .build();
    }

}
