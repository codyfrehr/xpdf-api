#include "io_xpdftools_pdftotextc_HelloWorldJNI.h"
#include <iostream>

/*
 * Class:     io_xpdftools_pdftotextc_HelloWorldJNI
 * Method:    sayHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_xpdftools_pdftotextc_HelloWorldJNI_sayHello (JNIEnv* env, jobject thisObject) {
	std::string hello = "Hello from C++ !!";
    std::cout << hello << std::endl;
    return env->NewStringUTF(hello.c_str());
}