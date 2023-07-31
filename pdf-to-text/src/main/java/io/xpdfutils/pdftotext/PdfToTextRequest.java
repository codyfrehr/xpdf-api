package io.xpdfutils.pdftotext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;

@Builder
@Getter
public class PdfToTextRequest {
    @NonNull
    private final File pdfFile;
    @NonNull
    private final File txtFile;
    private Options options;

    @Builder
    @Getter
    public static class Options {
        //todo: cleanup comments
        //todo: should we implement sensible default values?
        // probably better than leaving the decision to xpdf binaries, right..?
        // also, will make it more clear to end user whats happening...
//        @Builder.Default
        private Integer pageStart;						// -f <int> : first page to examine
        private Integer pageEnd;						// -l <int> : last page to examine
        private PdfToTextFormat format;		// return format for text
        private PdfToTextEncoding encoding;	// -enc : sets the encoding used for text output
        private PdfToTextEndOfLine endOfLine;	// -eol : return eol convention for text
        @Builder.Default
        private boolean pageBreakIncluded = true;                  // -nopgbrk : include new page characters in text
        private String ownerPassword;				// -opw <string> : owner password (for encrypted files)
        private String userPassword;				// -upw <string> : user password (for encrypted files)
    }
}
