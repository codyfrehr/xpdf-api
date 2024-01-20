package io.xpdftools.pdftext.autoconfigure;

import io.xpdftools.common.util.XpdfUtils;
import io.xpdftools.pdftext.PdfTextTool;
import io.xpdftools.pdftext.config.PdfTextToolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

import static io.xpdftools.pdftext.config.PdfTextToolConfigParams.*;

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

    @Autowired
    private PdfTextToolProperties pdfTextToolProperties;

    @Bean
    @ConditionalOnMissingBean
    public PdfTextToolConfig pdfTextToolConfig() {
        Path nativeLibraryPath = pdfTextToolProperties.getNativeLibraryPath() == null
                ? XpdfUtils.getPdfTextNativeLibraryPath()
                : pdfTextToolProperties.getNativeLibraryPath();

        Path defaultOutputPath = pdfTextToolProperties.getDefaultOutputPath() == null
                ? XpdfUtils.getPdfTextDefaultOutputPath()
                : pdfTextToolProperties.getDefaultOutputPath();

        Long timeoutMilliseconds = pdfTextToolProperties.getTimeoutMilliseconds() == null
                ? XpdfUtils.getPdfTextTimeoutMilliseconds()
                : pdfTextToolProperties.getTimeoutMilliseconds();

        PdfTextToolConfig pdfTextToolConfig = new PdfTextToolConfig();
        pdfTextToolConfig.put(NATIVE_LIBRARY_PATH, nativeLibraryPath);
        pdfTextToolConfig.put(DEFAULT_OUTPUT_PATH, defaultOutputPath);
        pdfTextToolConfig.put(TIMEOUT_MILLISECONDS, timeoutMilliseconds);

        return pdfTextToolConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public PdfTextTool pdfTextTool(PdfTextToolConfig pdfTextToolConfig) {
        return PdfTextTool.builder()
                .nativeLibraryPath((Path) pdfTextToolConfig.get(NATIVE_LIBRARY_PATH))
                .defaultOutputPath((Path) pdfTextToolConfig.get(DEFAULT_OUTPUT_PATH))
                .timeoutMilliseconds((Long) pdfTextToolConfig.get(TIMEOUT_MILLISECONDS))
                .build();
    }

}
