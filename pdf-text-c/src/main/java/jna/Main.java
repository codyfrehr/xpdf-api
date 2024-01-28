package jna;

import com.sun.jna.Native;
import io.xpdftools.pdftextc.HelloWorld;

public class Main {

    static {
//        System.setProperty("java.library.path", "C:\\Users\\Cody\\repos\\xpdf-tools\\pdf-to-text-c\\src\\main\\resources");
        System.setProperty("jna.library.path", "C:\\Users\\Cody\\repos\\xpdf-tools\\pdf-to-text-c\\src\\main\\java\\jna");
        Native.register(HelloWorld.class, "native");
    }

    public static void main(String argv[])
    {
        double cosh = CMath.INSTANCE.cosh(3);
//        var helloWorld = HelloWorld.INSTANCE.sayHello();
        System.out.println("Called C+, returned: " + cosh);
    }
}
