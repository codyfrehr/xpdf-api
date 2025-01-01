/*
 * PdfInfo API - An API for accessing a native pdfinfo library (https://xpdf.io)
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
package io.xpdf.api.pdfinfo;

import io.xpdf.api.common.XpdfResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the result of invoking the <em>pdfinfo</em> executable.
 *
 * @since 1.1.0
 */
@Builder
@Getter
@ToString
public class PdfInfoResponse extends XpdfResponse {

    /**
     * Standard output from the shell process that invoked the executable.
     *
     * @since 1.1.0
     */
    private final String standardOutput;

}
