#include "jni_pdftext_PdfTextJNI.h"
#include <iostream>

//from: https://stackoverflow.com/a/22546607/8784215
JNIEXPORT jint JNICALL Java_jni_pdftext_PdfTextJNI_pdfToTextMain
  (JNIEnv *env, jclass class, jobjectArray jargv) {

    //jargv is a Java array of Java strings
    int argc = env->GetArrayLength(jargv);
    typedef char *pchar;
    pchar *argv = new pchar[argc];
    int i;
    for(i=0; i<argc; i++)
    {
        jstring js = env->GetObjectArrayElement(jargv, i); //A Java string
        const char *pjc = env->GetStringUTFChars(js); //A pointer to a Java-managed char buffer
        size_t jslen = strlen(pjc);
        argv[i] = new char[jslen+1]; //Extra char for the terminating null
        strcpy(argv[i], pjc); //Copy to *our* buffer. We could omit that, but IMHO this is cleaner. Also, const correctness.
        env->ReleaseStringUTFChars(js, pjc);
    }

    //Call main
    main(argc, argv);

    //Now free the array
    for(i=0;i<argc;i++)
        delete [] argv[i];
    delete [] argv;
}