package io.xpdfutils.pdftext;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PdfTextOptions {
    //todo: cleanup comments
    //todo: should we implement sensible default values?
    // probably better than leaving the decision to xpdf binaries, right..?
    // also, will make it more clear to end user whats happening...
//        @Builder.Default
    private Integer pageStart;						// -f <int> : first page to examine
    private Integer pageEnd;						// -l <int> : last page to examine
    private PdfTextFormat format;		// return format for text
    private PdfTextEncoding encoding;	// -enc : sets the encoding used for text output
    private PdfTextEndOfLine endOfLine;	// -eol : return eol convention for text
    @Builder.Default
    private boolean pageBreakIncluded = true;                  // -nopgbrk : include new page characters in text
    private String ownerPassword;				// -opw <string> : owner password (for encrypted files)
    private String userPassword;				// -upw <string> : user password (for encrypted files)
}