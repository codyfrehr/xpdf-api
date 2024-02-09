# xpdf-api

## todo:
- licensing
  - what does GPLv3 specify i must do with my code? (just add license and info right?)
    - license and "copyright notice" must be included with every distribution? (including jar on maven) https://gist.github.com/kn9ts/cbe95340d29fc1aaeaa5dd5c059d2e60
      here's how you can include license in jar with a plugin: https://stackoverflow.com/a/39741925/8784215
      ...you should go see how other opensource projects do it.
      otherwise, maybe you can just add it to the project resources for each individual module, but thats annoying to duplicate https://stackoverflow.com/a/37655114/8784215
      see the maven license plugin https://www.mojohaus.org/license-maven-plugin/examples/example-add-license.html
    - understand difference between license and copyright, and what that means for this project https://stackoverflow.com/a/13437203/8784215
  - replace placeholders in licensing headers (ie, {{ organization }})
    maybe go find some examples online in github and see how others do it..
  - part of build process is to ensure all of your dependencies are gpl3 compatible.
    you should add licensing plugin to help in verification of that https://www.mojohaus.org/license-maven-plugin/
- clean up SCRIBBLES, or maybe organize into other docs at root that may be needed (ie, CONTRIBUTING https://github.com/spring-projects/spring-boot/blob/main/CONTRIBUTING.adoc)
- figure out process to deploy to maven central repo
  - take into consideration repos which provide download statistics: https://blog.sonatype.com/2010/12/now-available-central-download-statistics-for-oss-projects/
  - guide: https://maven.apache.org/repository/guide-central-repository-upload.html
    - requirements (link from within page above) https://central.sonatype.org/publish/requirements/ 
  - figure out maven-release-plugin, to control versioning and release process to maven central
    a good starting point for this would be to find a popular/respected open-source repo using pom configs, and see if they have maven release plugin configured, and how they do it.
- add key metadata to poms. see other open source project for examples (ie lombok has <license>, <issue management>, <developers>, etc)
- "Xpdf" doesn't seem to be copyrighted, but double check this... also, should you copyright "xpdf.io"?
- make xpdf.io repo public
  can/should you somehow segregate this repo from your personal github account?
  would be nice to have some general xpdf.io repo, with some public open-source stuff (xpdf-api) and some private stuff (xpdf-web)
  but from a legal point of view, what does that mean for your ownership of the repo?
- build really basic homepage for website, resembling layout of https://kotest.io/docs or even just the simpler https://mockk.io/
  maybe the homepage can redirect automatically to the repo readme until the site is built, so you can just release this thing!
- show xpdf creators your library, and get their okay to release
- request xpdf creators to add link to your library on their webpage. Per their readme, they would be willing to do this!
  "If you compile Xpdf for a system not listed on the web page, please
  let me know.  If you're willing to make your binary available by ftp
  or on the web, I'll be happy to add a link from the Xpdf web page.  I
  have decided not to host any binaries I didn't compile myself (for
  disk space and support reasons)."
- WRITE THIS README!
- javadoc:
  - is there a way to generate javadocs for lombok components?
    like, for example - Intellij can read sources and interpret everything just fine.
    if you download sources for this project from xpdf-web, and inspect PdfTextTool.builder().timeoutSeconds(), you get proper info.
    but if you look at javadoc generated for same component, you dont get any of that info (like @implNote)
