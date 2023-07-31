package io.xpdfutils.pdftotextc;

import com.sun.jna.Native;

public class Main {

    static {
//        System.setProperty("java.library.path", "C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text-c\\src\\main\\resources");
        System.setProperty("jna.library.path", "C:\\Users\\Cody\\repos\\xpdf-utils\\pdf-to-text-c\\src\\main\\java\\io\\cfrehr\\xpdfutils\\pdftotextc");
        System.setProperty("jna.debug_load", "true");
        Native.register(HelloWorld.class, "native");
    }

    public static void main(String argv[])
    {
//        var cosh = CMath.INSTANCE.cosh(3);
        var helloWorld = HelloWorld.INSTANCE.sayHello();
//        System.out.println("Called C+, returned: " + cosh);
    }
}
