package io.xpdftools.common;

//todo: create Abstract or parent class XpdfRequest and XpdfResponse, and enforce those types in this interface?

import io.xpdftools.common.exception.XpdfException;

/**
 * A wrapper of a Xpdf command line tool that is capable of invoking an executable.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
public interface XpdfTool<Request, Response> {
    Response process(Request request) throws XpdfException;

}
