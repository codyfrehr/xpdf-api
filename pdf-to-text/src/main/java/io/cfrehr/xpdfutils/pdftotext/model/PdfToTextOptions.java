package io.cfrehr.xpdfutils.pdftotext.model;

import lombok.Builder;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;

@Builder
public class PdfToTextOptions {
    private Integer firstPage;						// -f <int> : first page to examine
    private Integer lastPage;						// -l <int> : last page to examine
    private PdfToTextFormat format;		// return format for text
    private PdfToTextEncoding encoding;	// -enc : sets the encoding used for text output
    private PdfToTextEndOfLine endOfLine;	// -eol : return eol convention for text
    private Boolean noPageBreak;                  // -nopgbrk : include new page characters in text
    private String ownerPassword;				// -opw <string> : owner password (for encrypted files)
    private String userPassword;				// -upw <string> : user password (for encrypted files)

    protected String[] getCommandLineArgs() {
        val args = new ArrayList<String>();

        if (firstPage != null) args.add("-f %s".formatted(firstPage));
        if (lastPage != null) args.add("-l %s".formatted(lastPage));

        if (format != null) {
            switch (format) {
                case RAW -> args.add("-raw");
                case SIMPLE -> args.add("-simple");
                case TABLE -> args.add("-table");
                case LAYOUT -> args.add("-layout");
                case LINE_PRINTER -> args.add("-lineprinter");
            }
        }

        if (encoding != null) {
            switch (encoding) {
                case ASCII_7 -> args.add("-enc ASCII7");
                case LATIN_1 -> args.add("-enc Latin1");
                case SYMBOL -> args.add("-enc Symbol");
                case UCS_2 -> args.add("-enc UCS-2");
                case UTF_8 -> args.add("-enc UTF-8");
                case ZAPF_DINGBATS -> args.add("-enc ZapfDingbats");
            }
        }

        if (endOfLine != null) {
            switch (endOfLine) {
                case DOS -> args.add("-eol dos");
                case MAC -> args.add("-eol mac");
                case UNIX -> args.add("-eol unix");
            }
        }

        if (BooleanUtils.toBoolean(noPageBreak)) args.add("-nopgbrk");

        if (ownerPassword != null) args.add("-opw \"%s\"".formatted(ownerPassword));
        if (userPassword != null) args.add("-upw \"%s\"".formatted(userPassword));

        return args.toArray(new String[0]);
    }
}
