# xpdf-api

## local setup:
- install java 8 (ibm semeru) + set project to correct jdk https://developer.ibm.com/languages/java/semeru-runtimes/downloads/
- 

## todo:
- MUST PURCHASE XPDF.IO DOMAIN ASAP!!!!
- in the future, make PdfTextTool extend Callable so that users of sdk can run asynchronously if they would prefer?
  need to explore more on this topic... https://www.baeldung.com/java-single-thread-executor-service
  in general, need a way better understanding of threading concepts, and how that would play into manual process we run, and how native xpdf lib would be affected
- in PdfTextTool.process(), do we even need ExecutorService to accomplish what it is we are trying to accomplish?
  maybe we can instead post this question to stackoverflow, to learn a little more and get some decent feedback.
- decide on package "version" and cleanup corresponding @since javadoc tags
- javadoc...
  need to get plugin working that generates javadoc artifact (html pages)
  issues seem to arise in javadocs when you reference this library from other projects.
  for example, in xpdf-apis project, you get a bunch of broken links for types in the "common" package.
  those same links are NOT broken when we just exclude javadoc artifact from process altogether.
- another javadoc issue...
  should javadocs link to external website that will host javadocs?
  in other official javadocs, you can click a link to open up full docs for some type in your browser.
  is that an actual website hosting those docs?? or does it just open the html from the javadoc artifact?
- you should password protect your ssh keys. especially when you go live with servers. otherwise, hacker with access to your keys will have passwordless access to your repos
- read the xpdf sourcecode readme for licensing/distribution info (ie, must include all docs?)
- running list of things to do correctly when installing to maven repository:
  - push sources
  - write and push javadocs..? (is this mandatory for public sdks? or is this archaic stuff?)
- there is no way for the client to verify that we are including the authentic xpdf binaries in this solution... 
  - how can you package the binaries with this solution in a credible way?
  - maybe some way to incorporate the pgp key provided on xpdf website into build/distribution process? https://www.xpdfreader.com/download.html

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
  cd C:\Users\Cody\repos\xpdf-api\pdf-to-text-c\src\main\java\io\xpdftools\pdftotextc
  javac -h . HelloWorldJNI.java
  g++ -c -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include" -I"C:\Program Files\Semeru\jdk-17.0.7.7-openj9\include\win32" io_xpdftools_pdftotextc_HelloWorldJNI.cpp -o io_xpdftools_pdftotextc_HelloWorldJNI.o
  g++ -shared -o native.dll io_xpdftools_pdftotextc_HelloWorldJNI.o -Wl,--add-stdcall-alias
  java -cp . -Djava.library.path="C:\Users\Cody\repos\xpdf-api\pdf-to-text-c\src\main\java\io\xpdftools\pdftotextc" HelloWorldJNI.java
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

## user documentation website:
- add section for debugging that explains how to:
  - configure xpdf-api for debug-level logging
  - configure request options for "-verbose" output from native library (which will then be logged in debug mode)

## why design decisions were made:
## todo: start recording new notes here. this is important for future development, so we can understand why we took certain design approaches.
## some of this will be important to include as end notes to user
- mission of this library
  - this library should:
    - facilitate programmatic access to xpdf
    - act as an interface to xpdf
    - represent xpdf as purely as possible
  - this library should NOT:
      - replace xpdf
      - obscure xpdf commands
      - try to explain why xpdf works the way it works (this lib is just an interface for xpdf, not a user guide)
  - therefore:
      - individual request options should be nullable because they are *options*.
        we want our request object to represent the command options relayed to xpdf as clearly as possible.
        for example, making "pageBreakExcluded" primitive would result in the option having value "false" by default.
        although this is probably a better coding practice, it makes request object look like user has chosen false (when in reality, they may have chosen nothing, and nothing will be added to actual xpdf commands)
      - individual request options should also be nullable, and not defaulted, because sometimes xpdf does things differently when no option is given!
        for example, the choice to not include a "format" option results in a different format than any of the options, themselves.
        choosing nothing/null is an option, in and of itself, and may result in different functionality under the hood of xpdf (and we dont want to obscure that)
- slf4j chosen because we should let end-user of library choose implementation https://www.slf4j.org/faq.html#configure_logging
- 