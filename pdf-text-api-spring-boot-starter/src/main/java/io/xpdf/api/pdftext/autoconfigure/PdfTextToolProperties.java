/*
 * {{ project }}
 * Copyright (C) {{ year }} {{ organization }}
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.xpdf.api.pdftext.autoconfigure;

import io.xpdf.api.pdftext.PdfTextTool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * The configurable properties of a {@link PdfTextTool} that is autoconfigured in {@link PdfTextToolAutoConfiguration}.
 *
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "xpdf-api.pdf-text")
public class PdfTextToolProperties {

    private Path nativeLibraryPath;
    private Integer timeoutSeconds;

}
