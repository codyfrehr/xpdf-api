/*
 * Common - The components shared between Xpdf APIs.
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
package io.xpdf.api.common.exception;

import lombok.Getter;

/**
 * An {@link XpdfException} thrown when the invoked <em>Xpdf</em> executable returns a non-zero exit code.
 *
 * @since 1.0.0
 */
@Getter
public class XpdfExecutionException extends XpdfException {

    /**
     * Standard output streamed from invocation of <em>Xpdf</em> executable.
     *
     * @since 1.0.0
     */
    private final String standardOutput;

    /**
     * Error output streamed from invocation of <em>Xpdf</em> executable.
     *
     * @since 1.0.0
     */
    private final String errorOutput;

    public XpdfExecutionException(String standardOutput,
                                  String errorOutput,
                                  String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}
