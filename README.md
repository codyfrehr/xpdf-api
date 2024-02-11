# xpdf-api

## todo:
- licensing
  - understand difference between license, copyright, and trademark, and what that means for this project https://stackoverflow.com/a/13437203/8784215
    do i have to reference original Glyph/Cog copyright or anything?
  - use this awesome open-source licensing plugin to do the following.. https://oss.carbou.me/license-maven-plugin/
    - (TODO) add copy of license to package, if possible (in correct part of lifecycle)
- create official xpdf.io email address, replace property in license plugin, and rerun formatter
- fix maven license plugin to add copy of license to release package (if it can do that)
- add key metadata to poms. see other open source project for examples (ie lombok has <license>, <issue management>, <developers>, etc)
- clean up SCRIBBLES, or maybe organize into other docs at root that may be needed (ie, CONTRIBUTING https://github.com/spring-projects/spring-boot/blob/main/CONTRIBUTING.adoc)
- figure out process to deploy to maven central repo
  - take into consideration repos which provide download statistics: https://blog.sonatype.com/2010/12/now-available-central-download-statistics-for-oss-projects/
  - guide: https://maven.apache.org/repository/guide-central-repository-upload.html
    - requirements (link from within page above) https://central.sonatype.org/publish/requirements/ 
  - figure out maven-release-plugin, to control versioning and release process to maven central
    a good starting point for this would be to find a popular/respected open-source repo using pom configs, and see if they have maven release plugin configured, and how they do it.
- "Xpdf" doesn't seem to be trademarked, but double check this... should you trademark it "xpdf.io" and create business with that name??
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
