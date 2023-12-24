#include <jni.h>

#ifndef _Included_jni_pdftext_PdfTextJNI
#define _Included_jni_pdftext_PdfTextJNI
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_jni_pdftext_PdfTextJNI_pdfToTextMain
  (JNIEnv *env, jclass class, jobjectArray argv);

#ifdef __cplusplus
}
#endif
#endif
