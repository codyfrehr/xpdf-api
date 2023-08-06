package io.xpdftools.pdftext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;

@Builder
@Getter
public class PdfTextRequest {
    @NonNull
    private final File pdfFile;
    @NonNull
    private final File txtFile;
    private PdfTextOptions options;
}
