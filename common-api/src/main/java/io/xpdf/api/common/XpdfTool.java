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
package io.xpdf.api.common;

import io.xpdf.api.common.exception.*;

/**
 * A wrapper of a <em>Xpdf</em> command line tool.
 *
 * @since 1.0.0
 */
public interface XpdfTool<Request extends XpdfRequest, Response extends XpdfResponse> {

    /**
     * Invokes a native <em>Xpdf</em> library against a PDF file.
     *
     * @param request {@link XpdfRequest}
     * @return {@link XpdfResponse} result
     * @throws XpdfException if exception
     * @since 1.0.0
     */
    Response process(Request request) throws XpdfException;

}