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
package io.xpdf.api.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.File;

/**
 * Represents a command to invoke a native <em>Xpdf</em> library.
 *
 * @since 1.0.0
 */
@SuperBuilder
@Getter
public abstract class XpdfRequest<OptionsT extends XpdfOptions> {

    /**
     * Input PDF file.
     *
     * @implNote Required.
     * @since 1.0.0
     */
    @NonNull
    private final File pdfFile;

    /**
     * Command options to customize native process.
     *
     * @since 1.0.0
     */
    private final OptionsT options;

}
