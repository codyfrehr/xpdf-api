package io.xpdfutils.pdftext;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PdfTextResponse {
    private String pdfText;
    private String stdOut;
}
