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

import io.xpdf.api.common.exception.XpdfException;

/**
 * A wrapper of a <em>Xpdf</em> command line tool.
 *
 * @since 1.0.0
 */
public interface XpdfTool<Request extends XpdfRequest, Response extends XpdfResponse> {

    /**
     * Invokes a native <em>Xpdf</em> library against a PDF file with a set of options.
     */
    Response process(Request request) throws XpdfException;

}
