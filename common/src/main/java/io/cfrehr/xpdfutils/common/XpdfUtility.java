package io.cfrehr.xpdfutils.common;

public interface XpdfUtility<Request, Response> {
    Response process(Request request) throws XpdfException;
}
