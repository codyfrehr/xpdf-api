import lombok.val;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextEncoding;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextOptions;

public class Test {
    public static void main(String[] args) {
        val options = PdfToTextOptions.builder().encoding(PdfToTextEncoding.UTF_8).build();

        val options2 = options.getCommandLineArgs();
        val stop = true;
    }
}
