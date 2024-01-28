package io.xpdftools.common;

import io.xpdftools.common.exception.XpdfException;

/**
 * A wrapper of a <em>Xpdf</em> command line tool.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
public interface XpdfTool<Request extends XpdfRequest, Response extends XpdfResponse> {

    /**
     * Invokes a native <em>Xpdf</em> library against a PDF file with a set of options.
     */
    Response process(Request request) throws XpdfException;

}
