#include "jni_hello1_HelloWorldJNI.h"
#include <iostream>

/*
 * Class:     jni_hello1_HelloWorldJNI
 * Method:    sayHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT void JNICALL Java_jni_hello1_HelloWorldJNI_sayHello
  (JNIEnv* env, jobject thisObject) {
    std::cout << "Hello from C++ !!" << std::endl;
}