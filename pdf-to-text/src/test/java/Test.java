import io.cfrehr.xpdfutils.common.XpdfException;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextEncoding;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.service.PdfToText;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Test {
    public static void main(String[] args) throws XpdfException, URISyntaxException, IOException, InterruptedException, ExecutionException {

//        val processArgs = new ArrayList<String>();
////        processArgs.add("C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\target\\classes\\xpdf\\windows\\64\\pdftotext");
////        processArgs.add("-enc Latin1");
////        processArgs.add("\"C:\\Users\\Cody\\Downloads\\xpdftest\\BlueShield.pdf\"");
////        processArgs.add("\"C:\\Users\\Cody\\Downloads\\xpdftest\\BlueShield.txt\"");
//
//        //works - must use full path and quotes around command and files
//        processArgs.add("\"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\pdftotext.exe\" -verbose -enc Latin1 \"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.pdf\" \"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.txt\"");
//
//        // error 1
////        processArgs.add("\"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/pdftotext.exe\" -verbose -enc UTF-8 \"test.pdf\" \"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/test.txt\"");
//
//        // error 2
////        processArgs.add("\"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/pdftotext.exe\" -verbose -encUTF-8 \"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/test.pdf\" \"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/test.txt\"");
//
//        val builder = new ProcessBuilder(processArgs.toArray(new String[0]));
//        val process = builder.start();
//
//        val executorService = Executors.newSingleThreadExecutor();
//        val futureStandardOutput = executorService.submit(new ReadInputStreamTask(process.getInputStream()));
//        val futureErrorOutput = executorService.submit(new ReadInputStreamTask(process.getErrorStream()));
//
//        //todo: add configurable timeout
//        val exitValue = process.waitFor();
//
//        if (exitValue == 0) {
//            System.out.println("SUCCESS");
//        } else {
//            System.out.println("FAILURE");
//            if (!StringUtils.isBlank(futureErrorOutput.get()))
//                System.out.println("Error Output: " + futureErrorOutput.get());
//            switch (exitValue) {
//
//                case 1 -> throw new XpdfException("Error opening the PDF file");
//                case 2 -> throw new XpdfException("Error opening the output file");
//                case 3 -> throw new XpdfException("Error related to PDF permissions");
//                case 99 -> throw new XpdfException("Other Xpdf error");
//                default -> throw new XpdfException("Unknown Xpdf error");
//            }
//        }

//        var file = new File("C:\\Users\\Cody\\Downloads\\xpdf test\\zzz.txt");
        //todo: figure out what the fuck is going on with encoding...
        // runs find from command line with all the different encoding options
        // but not programmatically with same options..
        // something is up with how java runs process as opposed to doing so via command line
        // or maybe you are performing process incorrectly... maybe try that alternative runtime method
        //todo: in general, you need to understand encoding and how it works.
        // should probably be requirement that user specifies encoding, and that we have xpdfrc encoding map
        val request = PdfToTextRequest.builder()
                .pdfFile(new File("C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.pdf"))
                .txtFile(new File("C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.txt"))
                .options(PdfToTextRequest.Options.builder()
                        .pageStart(0)
                        .encoding(PdfToTextEncoding.LATIN_1)
                        .build())
                .build();

//        val pdfToText = PdfToText.builder().build();
        val pdfToText = new PdfToText();

        val result = pdfToText.process(request);
    }

}
