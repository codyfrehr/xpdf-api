package io.cfrehr.xpdfutils.pdftotext.model;

import lombok.Builder;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;

@Builder
public class PdfToTextRequest {
    private PdfToTextOptions options;
    private String filePathPdf;
    private String filePathTxt;

    public String[] getCommandLineArgs() {
        val args = new ArrayList<String>(Arrays.stream(options.getCommandLineArgs()).toList());

        args.add("\"%s\"".formatted(filePathPdf));
        args.add("\"%s\"".formatted(filePathTxt));

        return args.toArray(new String[0]);
    }
}
