package service;

import common.XpdfUtility;
import model.PdfToTextRequest;
import model.PdfToTextResponse;

public class PdfToText implements XpdfUtility<PdfToTextRequest, PdfToTextResponse> {

    @Override
    public PdfToTextResponse process(PdfToTextRequest request) {

        return new PdfToTextResponse();
    }
}
