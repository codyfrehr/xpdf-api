package jni.pdftext;

public class PdfTextJNI {

    static {
        String javaLibraryPathPrevious = System.getProperty("java.library.path");
        System.setProperty("java.library.path", "C:\\Users\\Cody\\repos\\xpdf-tools\\pdf-text-c\\src\\main\\java\\jni\\pdftext;" + System.getProperty("java.library.path"));
        System.loadLibrary("pdftext");
    }

    public static void main(String[] args) {
        new PdfTextJNI().pdfToTextMain(new String[]{"C:\\Users\\Cody\\Downloads\\xpdftest\\jni.pdf", "C:\\Users\\Cody\\Downloads\\xpdftest\\jni.txt"});
    }

    private native void pdfToTextMain(String[] argv);
}
