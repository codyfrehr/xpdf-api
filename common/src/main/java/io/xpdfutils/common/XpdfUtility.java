package io.xpdfutils.common;

//todo: rename this to XpdfTool and rename project artifact to xpdftools and rename PdfText to PdfTextTool
//todo: create Abstract or parent class XpdfRequest and XpdfResponse, and enforce those types in this interface?
public interface XpdfUtility<Request, Response> {
    Response process(Request request) throws XpdfProcessingException, XpdfValidationException;
}
