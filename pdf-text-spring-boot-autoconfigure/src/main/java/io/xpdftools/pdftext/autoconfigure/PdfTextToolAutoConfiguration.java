package io.xpdftools.pdftext.autoconfigure;

import io.xpdftools.pdftext.PdfTextTool;
import io.xpdftools.pdftext.PdfTextToolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.xpdftools.pdftext.PdfTextToolConfigParams.BINARY_PATH;
import static io.xpdftools.pdftext.PdfTextToolConfigParams.OUTPUT_DIRECTORY;

//note: followed this guide at first:
//      https://www.baeldung.com/spring-boot-custom-starter
//      https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories
//note: at first, autoconfiguration not detected.
//      but some stack overflow comment mentioned that spring-boot 3 does NOT use spring.factories
//      instead, had to follow this guide: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration
//todo: now that you have AutoConfiguration working, read more in this guide to get better grasp of concepts so you can fine-tune the stuff here
//todo: need PdfTextTool constructor for non-autoconfiguration
//todo: need to cleanup PdfTextTool autoconfig constructor logic to properly handle config inputs, like so:
//      https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-library/src/main/java/com/baeldung/greeter/library/Greeter.java
//      also, will need to consider how to manage fact that user may provide incompatible library for jdk/OS arch...
//      ...or should you just not allow this to be configurable, at the moment?
//todo: what other configs do you want to add? (ie, debug mode)
@Configuration
@ConditionalOnClass(PdfTextTool.class)
@EnableConfigurationProperties(PdfTextToolProperties.class)
public class PdfTextToolAutoConfiguration {

    @Autowired
    private PdfTextToolProperties pdfTextToolProperties;

    @Bean
    @ConditionalOnMissingBean
    public PdfTextToolConfig pdfTextToolConfig() {

        String binaryPath = pdfTextToolProperties.getBinaryPath() == null
                ? "/default/path/pdftext.exe" //todo: correct default path
                : pdfTextToolProperties.getBinaryPath();
        String outputDirectory = pdfTextToolProperties.getOutputDirectory() == null
                ? "/default/directory"
                : pdfTextToolProperties.getOutputDirectory();

        PdfTextToolConfig pdfTextToolConfig = new PdfTextToolConfig();
        pdfTextToolConfig.put(BINARY_PATH, binaryPath);
        pdfTextToolConfig.put(OUTPUT_DIRECTORY, outputDirectory);

        return pdfTextToolConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public PdfTextTool pdfTextTool(PdfTextToolConfig pdfTextToolConfig) {
        return new PdfTextTool(pdfTextToolConfig);
    }

}
