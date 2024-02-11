/*
 * Common - A library of common components.
 * Copyright © 2024 xpdf.io (cfrehr@gmail.com)
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
 * A {@link XpdfException} thrown when a native <em>Xpdf</em> process returns a non-zero exit code.
 *
 * @since 1.0.0
 */
@Getter
public class XpdfNativeExecutionException extends XpdfException {

    /**
     * The standard output of a native <em>Xpdf</em> process.
     *
     * @since 1.0.0
     */
    private final String standardOutput;

    /**
     * The error output of a native <em>Xpdf</em> process.
     *
     * @since 1.0.0
     */
    private final String errorOutput;

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput,
                                        String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}
