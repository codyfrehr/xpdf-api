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
package io.xpdf.api.common;

import io.xpdf.api.common.exception.XpdfException;

/**
 * A wrapper of an <em>Xpdf</em> command line tool.
 *
 * @since 1.0.0
 */
public interface XpdfTool<RequestT extends XpdfRequest, ResponseT extends XpdfResponse> {

    /**
     * Invokes an <em>Xpdf</em> executable against a PDF file.
     *
     * @param request {@link XpdfRequest}
     * @return {@link XpdfResponse} result
     * @throws XpdfException if exception
     * @since 1.0.0
     */
    ResponseT process(RequestT request) throws XpdfException;

}
