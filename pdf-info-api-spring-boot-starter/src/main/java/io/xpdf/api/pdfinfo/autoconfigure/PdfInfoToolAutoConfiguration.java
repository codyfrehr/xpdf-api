/*
 * PdfInfo API Starter - A Spring Boot starter for PdfInfo API (https://xpdf.io)
 * Copyright © 2025 xpdf.io (info@xpdf.io)
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
 * @since 1.1.0
 */
@AutoConfiguration
@ConditionalOnClass(PdfInfoTool.class)
@EnableConfigurationProperties(PdfInfoToolProperties.class)
public class PdfInfoToolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PdfInfoTool pdfInfoTool(PdfInfoToolProperties pdfInfoToolProperties) {
        return PdfInfoTool.builder()
                .executableFile(pdfInfoToolProperties.getExecutablePath() != null ? pdfInfoToolProperties.getExecutablePath().toFile() : null)
                .timeoutSeconds(pdfInfoToolProperties.getTimeoutSeconds())
                .build();
    }

}
