package io.xpdftools.common;

//todo: add some kind of javadoc

//todo: create Abstract or parent class XpdfRequest and XpdfResponse, and enforce those types in this interface?
public interface XpdfTool<Request, Response> {
    Response process(Request request) throws XpdfProcessingException, XpdfValidationException;
}
