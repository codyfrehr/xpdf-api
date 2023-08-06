import io.xpdftools.common.XpdfException;
import io.xpdftools.pdftext.*;
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
        val request = PdfTextRequest.builder()
                .pdfFile(new File("C:\\Users\\Cody\\Downloads\\xpdf test\\dummy.pdf"))
                .txtFile(new File("C:\\Users\\Cody\\Downloads\\xpdf test\\dummy.txt"))
                .options(PdfTextOptions.builder()
                        .pageStart(0)
                        .encoding(PdfTextEncoding.LATIN_1)
                        .format(PdfTextFormat.LAYOUT)
                        .build())
                .build();

        val pdfText = new PdfTextTool();

        val result = pdfText.process(request);
    }

}
