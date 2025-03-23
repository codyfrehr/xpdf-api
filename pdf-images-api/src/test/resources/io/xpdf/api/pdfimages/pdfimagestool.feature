Feature: PdfImagesTool

  Scenario: Extract images from pdf
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile   | imageFilePathPrefix |
      | small.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract images from pdf with explicit executable file path
    Given a PdfImagesTool with 30 second timeout and dynamic executable file
    Given a PdfImagesRequest with values
      | pdfFile   | imageFilePathPrefix |
      | small.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract images from pdf with explicit output image file path prefix
    Given a PdfImagesTool
    Given a PdfImagesRequest with pdf file small.pdf and dynamic image file path prefix
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract images from pdf with no images
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile      | imageFilePathPrefix |
      | 0-images.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 0 output image files should exist


  Scenario: Extract images from pdf with multiple images
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile      | imageFilePathPrefix |
      | 3-images.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 3 output image files should exist


  Scenario: Extract pbm image from pdf that contains pbm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p1.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pbm" extension


  Scenario: Extract pbm image from pdf that contains plain pbm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p4.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pbm" extension


  Scenario: Extract pgm image from pdf that contains pgm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p2.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pgm" extension


  Scenario: Extract pgm image from pdf that contains plain pgm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p5.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pgm" extension


  Scenario: Extract ppm image from pdf that contains ppm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p3.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "ppm" extension


  Scenario: Extract ppm image from pdf that contains plain ppm image
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p6.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "ppm" extension


  Scenario: Extract images from first page of pdf
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile        | imageFilePathPrefix |
      | multi-page.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      | 1         | 1        |            |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract pbm image from pdf that contains pbm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p1.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pbm" extension


  Scenario: Extract pbm image from pdf that contains plain pbm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p4.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pbm" extension


  Scenario: Extract pgm image from pdf that contains pgm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p2.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pgm" extension


  Scenario: Extract pgm image from pdf that contains plain pgm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p5.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "pgm" extension


  Scenario: Extract jpg image from pdf that contains ppm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p3.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "jpg" extension


  Scenario: Extract jpg image from pdf that contains plain ppm image with jpeg file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p6.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | JPEG       |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "jpg" extension


  Scenario: Extract fax image from pdf that contains pbm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p1.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "fax" extension


  Scenario: Extract fax image from pdf that contains plain pbm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pbm-p4.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "fax" extension


  Scenario: Extract flate image from pdf that contains pgm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p2.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "flate" extension


  Scenario: Extract flate image from pdf that contains plain pgm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | pgm-p5.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "flate" extension


  Scenario: Extract jpg image from pdf that contains ppm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p3.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "jpg" extension


  Scenario: Extract jpg image from pdf that contains plain ppm image with raw file format
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile    | imageFilePathPrefix |
      | ppm-p6.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          | RAW        |                  |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the output image files should have "jpg" extension


  Scenario: Extract images from pdf with metadata included
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile      | imageFilePathPrefix |
      | 3-images.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          |            | true             |               |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the standard output should match ".*-0000\.ppm:[^\f]+.*-0001\.ppm:[^\f]+.*-0002\.ppm"


  Scenario: Extract images from password protected pdf with owner password
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile                | imageFilePathPrefix |
      | password-protected.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          |            |                  | Secret123     |              |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract images from password protected pdf with user password
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile                | imageFilePathPrefix |
      | password-protected.pdf |                     |
    Given a PdfImagesOptions with values
      | pageStart | pageStop | fileFormat | metadataIncluded | ownerPassword | userPassword |
      |           |          |            |                  |               | Secret123    |
    When the PdfImagesTool processes the PdfImagesRequest
    Then 1 output image files should exist


  Scenario: Extract images from pdf with native verbose option and get non-null standard output
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile   | imageFilePathPrefix |
      | small.pdf |                     |
    Given native PdfImagesOptions with values
      | -verbose |
      |          |
    When the PdfImagesTool processes the PdfImagesRequest
    Then the standard output should not be empty


  Scenario: Extract images from large pdf with small timeout and get exception
    Given a PdfImagesTool with values
      | executableFile | timeoutSeconds |
      |                | 1              |
    Given a PdfImagesRequest with values
      | pdfFile   | imageFilePathPrefix |
      | large.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest expecting an XpdfException
    Then the XpdfException should be an XpdfTimeoutException


  Scenario: Extract images from password protected pdf without providing password and get exception
    Given a PdfImagesTool
    Given a PdfImagesRequest with values
      | pdfFile                | imageFilePathPrefix |
      | password-protected.pdf |                     |
    When the PdfImagesTool processes the PdfImagesRequest expecting an XpdfException
    Then the XpdfException should be an XpdfExecutionException
