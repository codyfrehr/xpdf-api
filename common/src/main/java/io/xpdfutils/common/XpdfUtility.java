package io.xpdfutils.common;

public interface XpdfUtility<Request, Response> {
    Response process(Request request) throws XpdfProcessingException, XpdfValidationException;
}
