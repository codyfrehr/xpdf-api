# xpdf-tools

## local setup:
- install java 8 (ibm semeru) + set project to correct jdk https://developer.ibm.com/languages/java/semeru-runtimes/downloads/
- 

## todo:
- you should password protect your ssh keys. especially when you go live with servers. otherwise, hacker with access to your keys will have passwordless access to your repos
- figure out best place to put xpdf binaries
- configure Service to use correct binaries (windows/mac/linux) based on OS
- read the xpdf sourcecode readme for licensing/distribution info (ie, must include all docs?)
- running list of things to do correctly when installing to maven repository:
  - push sources
  - write and push javadocs..? (is this mandatory for public sdks? or is this archaic stuff?)
- make project spring configurable
  - https://docs.spring.io/spring-boot/docs/1.5.11.RELEASE/reference/html/boot-features-developing-auto-configuration.html
  - 3 packages
    - core (non-spring stuff)
    - autoconfigure (all auto-config stuff with conditional bean to override. create bean, but give user option to override)
    - starter (springboot and all spring dependencies)

## autoconfiguration:
- followed this guide at first:
  - https://www.baeldung.com/spring-boot-custom-starter
  - https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories
- at first, autoconfiguration not detected. but some stack overflow comment mentioned that spring-boot 3 does NOT use spring.factories. instead, had to follow this guide:
  - https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration
  - https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-custom-starter/greeter-library/src/main/java/com/baeldung/greeter/library/Greeter.java

## c++ stuff:
- install Windows c++ compiler MinGW via MYSYS2: https://code.visualstudio.com/docs/cpp/config-mingw
- JNI: https://www.baeldung.com/jni
  cd C:\Users\Cody\repos\xpdf-tools\pdf-to-text-c\src\main\java\io\xpdftools\pdftotextc
  javac -h . HelloWorldJNI.java
  g++ -c -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include" -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include\win32" io_xpdftools_pdftotextc_HelloWorldJNI.cpp -o io_xpdftools_pdftotextc_HelloWorldJNI.o
  g++ -shared -o native.dll io_xpdftools_pdftotextc_HelloWorldJNI.o -Wl,--add-stdcall-alias
  java -cp . -Djava.library.path="C:\Users\Cody\repos\xpdf-tools\pdf-to-text-c\src\main\java\io\xpdftools\pdftotextc" HelloWorldJNI.java
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

## c++ NEXT STEPS:
  - video explaining how to build c++ project: https://www.youtube.com/watch?v=AJRGU_XgVMQ&list=WL&index=34
    - do this yourself and build static windows library that you can import into this project and test running via JNA.
  - next, build xpdf library from source code and test running via JNA
    - you may need to alter source code to build as static lib instead of executable (add_executable() in xpdf/CMakeLists.txt)
    - https://cmake.org/cmake/help/latest/command/add_library.html
    - got all this JNI stuff to work! just need to figure out how to compile xpdf stuff in windows.
      part of problem is figuring out linking and stuff like that. seems like you would need to create header file for pdftext.cc.
      before going any further on this, really need to understand better how c++ works and compile process works and linking, etc