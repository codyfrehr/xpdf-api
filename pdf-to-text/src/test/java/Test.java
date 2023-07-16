import lombok.val;
import model.PdfToTextEncoding;
import model.PdfToTextOptions;

public class Test {
    public static void main(String[] args) {
        val options = new PdfToTextOptions();
        options.setEncoding(PdfToTextEncoding.UTF_8);

        val options2 = options.getCommandLineArgs();
        val stop = true;
    }
}
