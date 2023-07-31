package io.xpdfutils.pdftotext;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PdfToTextResponse {
    private String pdfText;
    private String stdOut;
}
