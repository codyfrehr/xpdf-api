package io.cfrehr.xpdfutils.pdftotext.model;

import lombok.Builder;

@Builder
public class PdfToTextResponse {
    public PdfText pdfText;
    public int exitCode;
    public String standardOutput;
}
