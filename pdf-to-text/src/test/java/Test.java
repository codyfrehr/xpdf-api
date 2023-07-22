import io.cfrehr.xpdfutils.common.XpdfException;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextEncoding;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextRequest;
import io.cfrehr.xpdfutils.pdftotext.model.PdfToTextResponse;
import io.cfrehr.xpdfutils.pdftotext.service.PdfToText;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) throws XpdfException, URISyntaxException, IOException, InterruptedException {

//        val processArgs = new ArrayList<String>();
////        processArgs.add("C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\target\\classes\\xpdf\\windows\\64\\pdftotext");
////        processArgs.add("-enc Latin1");
////        processArgs.add("\"C:\\Users\\Cody\\Downloads\\xpdftest\\BlueShield.pdf\"");
////        processArgs.add("\"C:\\Users\\Cody\\Downloads\\xpdftest\\BlueShield.txt\"");
//
//        //works - must use full path and quotes around command and files
////        processArgs.add("\"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\pdftotext.exe\" -verbose -enc UTF-8 \"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.pdf\" \"C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text\\src\\main\\resources\\xpdf\\windows\\64\\test.txt\"");
////        processArgs.add("\"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/pdftotext.exe\" -verbose -enc UTF-8 \"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/test.pdf\" \"C:/Users/Cody/repos/xpdf-utils/pdf-to-text/src/main/resources/xpdf/windows/64/test.txt\"");
//
//        val builder = new ProcessBuilder(processArgs.toArray(new String[0]));
//        val process = builder.start();
//        watch(process);
//        process.waitFor();
//
////        todo: log exitCode/standardOutput/errorOutput?
////        todo: use instead IOUtils.toString(inputStream, StandardCharsets.UTF_8)
//////            val standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
//////                    .lines()
//////                    .collect(Collectors.joining("\n"));
//
//        if (process.exitValue() == 0) {
//            System.out.println("SUCCESS");
//        } else {
//            System.out.println("FAILURE");
//
//            //todo: log exitCode/standardOutput/errorOutput?
//            //todo: use instead IOUtils.toString(inputStream, StandardCharsets.UTF_8)
//            val standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
//                    .lines()
//                    .collect(Collectors.joining("\n"));
//            System.out.println("standardOutput: " + standardOutput);
//
//            val errorOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
//                    .lines()
//                    .collect(Collectors.joining("\n"));
//            System.out.println("errorOutput: " + errorOutput);
//
//            switch (process.exitValue()) {
//
//                case 1 -> throw new XpdfException("Error opening the PDF file");
//                case 2 -> throw new XpdfException("Error opening the output file");
//                case 3 -> throw new XpdfException("Error related to PDF permissions");
//                case 99 -> throw new XpdfException("Other Xpdf error");
//                default -> throw new XpdfException("Unknown Xpdf error");
//            }
//        }

        //todo: figure out what the fuck is going on with encoding...
        // runs find from command line with all the different encoding options
        // but not programmatically with same options..
        // something is up with how java runs process as opposed to doing so via command line
        // or maybe you are performing process incorrectly... maybe try that alternative runtime method
        //todo: in general, you need to understand encoding and how it works.
        // should probably be requirement that user specifies encoding, and that we have xpdfrc encoding map
        val request = PdfToTextRequest.builder()
                .options(PdfToTextRequest.Options.builder()
//                        .pageStart(1)
//                        .encoding(PdfToTextEncoding.LATIN_1)
                        .build())
                .filePathPdf("C:\\Users\\Cody\\Downloads\\xpdftest\\test.pdf")
                .filePathTxt("C:\\Users\\Cody\\Downloads\\xpdftest\\test.txt")
                .build();

//        val pdfToText = PdfToText.builder().build();
        val pdfToText = new PdfToText();

        val result = pdfToText.process(request);
    }

    private static void watch(final Process process) {
        new Thread() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                try {
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
