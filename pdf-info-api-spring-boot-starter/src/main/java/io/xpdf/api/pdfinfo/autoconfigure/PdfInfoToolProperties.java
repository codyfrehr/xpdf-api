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
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfInfoTool} that is autoconfigured in {@link PdfInfoToolAutoConfiguration}.
 *
 * @since 1.1.0
 */
@Data
@ConfigurationProperties(prefix = "io.xpdf.api.pdf-info")
public class PdfInfoToolProperties {

    private Path executablePath;
    private Integer timeoutSeconds;

}
