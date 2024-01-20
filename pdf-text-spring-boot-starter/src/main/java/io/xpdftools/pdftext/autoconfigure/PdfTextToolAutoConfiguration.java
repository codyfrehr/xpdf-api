package io.xpdftools.pdftext.autoconfigure;

import io.xpdftools.common.util.XpdfUtils;
import io.xpdftools.pdftext.PdfTextTool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

/**
 * An autoconfiguration for a {@code PdfTextTool} bean, enabling configuration properties in {@code PdfTextToolProperties}.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@AutoConfiguration
@ConditionalOnClass(PdfTextTool.class)
@EnableConfigurationProperties(PdfTextToolProperties.class)
public class PdfTextToolAutoConfiguration {

    //todo: add unit test
    @Bean
    @ConditionalOnMissingBean
    public PdfTextTool pdfTextTool(PdfTextToolProperties pdfTextToolProperties) {
        Path nativeLibraryPath = pdfTextToolProperties.getNativeLibraryPath() == null
                ? XpdfUtils.getPdfTextNativeLibraryPath()
                : pdfTextToolProperties.getNativeLibraryPath();

        Path defaultOutputPath = pdfTextToolProperties.getDefaultOutputPath() == null
                ? XpdfUtils.getPdfTextDefaultOutputPath()
                : pdfTextToolProperties.getDefaultOutputPath();

        Long timeoutMilliseconds = pdfTextToolProperties.getTimeoutMilliseconds() == null
                ? XpdfUtils.getPdfTextTimeoutMilliseconds()
                : pdfTextToolProperties.getTimeoutMilliseconds();

        return PdfTextTool.builder()
                .nativeLibraryPath(nativeLibraryPath)
                .defaultOutputPath(defaultOutputPath)
                .timeoutMilliseconds(timeoutMilliseconds)
                .build();
    }

}
