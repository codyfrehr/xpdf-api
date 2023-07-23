import io.cfrehr.xpdfutils.common.XpdfException;
import io.cfrehr.xpdfutils.pdftotext.PdfToTextEncoding;
import io.cfrehr.xpdfutils.pdftotext.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.PdfToText;
import lombok.val;

import java.io.File;

public class Test {

    public static void main(String[] args) throws XpdfException {

        //todo: figure out what the fuck is going on with encoding...
        // runs find from command line with all the different encoding options
        // but not programmatically with same options..
        // something is up with how java runs process as opposed to doing so via command line
        // or maybe you are performing process incorrectly... maybe try that alternative runtime method
        //todo: in general, you need to understand encoding and how it works.
        // should probably be requirement that user specifies encoding, and that we have xpdfrc encoding map
        val request = PdfToTextRequest.builder()
                .pdfFile(new File("C:\\Users\\Cody\\Downloads\\xpdf test\\dummy.pdf"))
                .txtFile(new File("C:\\Users\\Cody\\Downloads\\xpdf test\\dummy.txt"))
                .options(PdfToTextRequest.Options.builder()
                        .pageStart(0)
                        .encoding(PdfToTextEncoding.LATIN_1)
                        .build())
                .build();

        val pdfToText = new PdfToText();

        val result = pdfToText.process(request);
    }

}
