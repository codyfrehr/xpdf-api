# xpdf-api

## todo:
- licensing
  - what does GPLv3 specify i must do with original, unmodified xpdf source code? doesn't need to be included, does it?
  - what does GPLv3 specify i must do with my code? (just add license and info right?)
  - another really helpful post https://opensource.stackexchange.com/q/9141
  - good gpl3 license FAQ: https://opensource.stackexchange.com/a/6814
  - replace placeholders in licensing headers (ie, {{ organization }})
    maybe go find some examples online in github and see how others do it..
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
- WRITE THIS README!
- javadoc: is an actual javadoc artifact needed?
  ANSWER: YES. spring boot libs for example include javadoc and sources jars
- more javadoc...
  need to get plugin working that generates javadoc artifact (html pages)
  issues seem to arise in javadocs when you reference this library from other projects.
  for example, in xpdf-apis project, you get a bunch of broken links for types in the "common" package.
  those same links are NOT broken when we just exclude javadoc artifact from process altogether.
- another javadoc issue...
  should javadocs link to external website that will host javadocs?
  in other official javadocs, you can click a link to open up full docs for some type in your browser.
  is that an actual website hosting those docs?? or does it just open the html from the javadoc artifact?
