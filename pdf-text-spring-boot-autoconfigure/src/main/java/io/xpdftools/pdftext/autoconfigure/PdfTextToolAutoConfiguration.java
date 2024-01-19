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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.xpdftools.pdftext.config.PdfTextToolConfigParams.*;

//note: followed this guide at first:
//      https://www.baeldung.com/spring-boot-custom-starter
//      https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories
//note: at first, autoconfiguration not detected.
//      but some stack overflow comment mentioned that spring-boot 3 does NOT use spring.factories
//      instead, had to follow this guide: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration
//todo: now that you have AutoConfiguration working, read more in this guide to get better grasp of concepts so you can fine-tune the stuff here
//todo: what other configs do you want to add? (ie, debug mode)
//      also, remove the configurable exe path - that shouldnt be allowed. you code depends on correct version of exe being provided
//      but maybe what you can do is allow where bin file gets copied to, to be configurable?
//todo: then once configs decided on, need to cleanup PdfTextTool autoconfig constructor logic to properly handle config inputs, like so:
//      https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-library/src/main/java/com/baeldung/greeter/library/Greeter.java
//todo: javadoc
@AutoConfiguration
@ConditionalOnClass(PdfTextTool.class)
@EnableConfigurationProperties(PdfTextToolProperties.class)
public class PdfTextToolAutoConfiguration {

    @Autowired
    private PdfTextToolProperties pdfTextToolProperties;

    @Bean
    @ConditionalOnMissingBean
    public PdfTextToolConfig pdfTextToolConfig() throws IOException {
        Path nativeLibraryPath = pdfTextToolProperties.getNativeLibraryPath() == null
                ? XpdfUtils.getPdfTextNativeLibraryPath()
                : pdfTextToolProperties.getNativeLibraryPath();

        String defaultOutputDirectory = pdfTextToolProperties.getDefaultOutputDirectory() == null
                ? XpdfUtils.getPdfTextDefaultOutputPath().toFile().getCanonicalPath()
                : pdfTextToolProperties.getDefaultOutputDirectory();

        Long timeoutMilliseconds = pdfTextToolProperties.getTimeoutMilliseconds() == null
                ? XpdfUtils.getPdfTextTimeoutMilliseconds()
                : pdfTextToolProperties.getTimeoutMilliseconds();

        PdfTextToolConfig pdfTextToolConfig = new PdfTextToolConfig();
        pdfTextToolConfig.put(NATIVE_LIBRARY_PATH, nativeLibraryPath);
        pdfTextToolConfig.put(DEFAULT_OUTPUT_DIRECTORY, defaultOutputDirectory);
        pdfTextToolConfig.put(TIMEOUT_MILLISECONDS, timeoutMilliseconds);

        return pdfTextToolConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public PdfTextTool pdfTextTool(PdfTextToolConfig pdfTextToolConfig) {
        return PdfTextTool.builder()
                .nativeLibraryPath((Path) pdfTextToolConfig.get(NATIVE_LIBRARY_PATH))
                .defaultOutputDirectory(new File((String) pdfTextToolConfig.get(DEFAULT_OUTPUT_DIRECTORY)))
                .timeoutMilliseconds((Long) pdfTextToolConfig.get(TIMEOUT_MILLISECONDS))
                .build();
    }

}
