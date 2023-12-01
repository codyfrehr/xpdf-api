package jni.hello1;

public class HelloWorldJNI {

    static {
        //todo: test loading library from jar, before going any further
        //      https://stackoverflow.com/a/6511351/8784215
        String javaLibraryPathPrevious = System.getProperty("java.library.path");
        System.setProperty("java.library.path", "C:\\Users\\Cody\\repos\\xpdf-tools\\pdf-text-c\\src\\main\\java\\jni\\hello1;" + System.getProperty("java.library.path"));
        System.loadLibrary("hello1");
    }

    public static void main(String[] args) {
        new HelloWorldJNI().sayHello();
    }

    private native void sayHello();
}
