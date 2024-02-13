/*
 * Xpdf API Starter - A Spring Boot starter for Xpdf API.
 * Copyright © 2024 xpdf.io (info@xpdf.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
