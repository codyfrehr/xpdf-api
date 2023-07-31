package io.xpdfutils.pdftotextc;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface HelloWorld extends Library {
    HelloWorld INSTANCE = Native.load("native", HelloWorld.class);
//    HelloWorld INSTANCE = Native.register(HelloWorld.class, "native.dll");
    int sayHello();
}