# xpdf-utils

todo:
- figure out best place to put xpdf binaries
- configure Service to use correct binaries (windows/mac/linux) based on OS
- read the xpdf utils readme for licensing/distribution info (ie, must include all docs?)

c++ stuff:
- install Windows c++ compiler MinGW via MYSYS2: https://code.visualstudio.com/docs/cpp/config-mingw
- JNI: https://www.baeldung.com/jni
  cd C:\Users\Cody\repos\xpdf-utils\pdf-to-text-c\src\main\java\io\cfrehr\xpdfutils\pdftotextc
  javac -h . HelloWorldJNI.java
  g++ -c -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include" -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include\win32" io_cfrehr_xpdfutils_pdftotextc_HelloWorldJNI.cpp -o io_cfrehr_xpdfutils_pdftotextc_HelloWorldJNI.o
  g++ -shared -o native.dll io_cfrehr_xpdfutils_pdftotextc_HelloWorldJNI.o -Wl,--add-stdcall-alias
  java -cp . -Djava.library.path="C:\Users\Cody\repos\xpdf-utils\pdf-to-text-c\src\main\java\io\cfrehr\xpdfutils\pdftotextc" HelloWorldJNI.java
- JNI: https://blog.gitnux.com/code/java-c/
- JNA: https://github.com/java-native-access/jna/tree/7ac44fee3d6b0e47de6d5d10c32be258b9ed1bef#readme
- JNA sample projects: https://github.com/java-native-access/jna/tree/master/contrib
- JNA: https://www.baeldung.com/java-jna-dynamic-libraries
  - JNA build dll from xpdf source code:
  - download source code
  - compile c++ with cmake using MinGW build system generator and vcpkg toolchain
    cmake -S "C:\Users\Cody\Downloads\xpdf-4.04" -B "C:\Users\Cody\Downloads\xpdf-4.04" -G "MinGW Makefiles" -DCMAKE_TOOLCHAIN_FILE="C:\Users\Cody\source\repos\other\vcpkg\scripts\buildsystems\vcpkg.cmake"
  - generate native lib
    g++ -c -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include" -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include\win32" -I"C:\Users\Cody\Downloads\xpdf-4.04" -I"C:\Users\Cody\Downloads\xpdf-4.04\goo" "C:\Users\Cody\Downloads\xpdf-4.04\xpdf\pdftotext.cc" -o "C:\Users\Cody\Downloads\xpdf-4.04\xpdf\pdftotext.o"
    (DOESNT WORK) g++ -shared -o "C:\Users\Cody\Downloads\xpdf-4.04\xpdf\pdftotext.dll" "C:\Users\Cody\Downloads\xpdf-4.04\xpdf\pdftotext.o" -Wl,--add-stdcall-alias
    **** see if you get replies on your xpdf forum post... https://forum.xpdfreader.com/viewforum.php?f=3
- exe maven plugin: https://www.mojohaus.org/exec-maven-plugin/examples/example-exec-using-executabledependency.html
- exe maven plugin: https://support.huaweicloud.com/intl/en-us/codeci_faq/codeci_faq_1036.html
- 