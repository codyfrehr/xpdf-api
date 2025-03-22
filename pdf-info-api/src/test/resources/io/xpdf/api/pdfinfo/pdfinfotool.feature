Feature: PdfInfoTool

  Scenario: Extract info from pdf
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract info from pdf with explicit executable file path
    Given a PdfInfoTool with 30 second timeout and dynamic executable file
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract info from pdf with ascii encoding
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          | ASCII_7  |                       |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Creator:.*Microsoft\(R\)"


  Scenario: Extract info from first page of pdf with bounding boxes included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile               |
      | multi-sized-pages.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      | 1         | 1        |          | true                  |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:.*612.*792"


  Scenario: Extract info from first page of pdf with bounding boxes included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile               |
      | multi-sized-pages.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      | 2         | 2        |          | true                  |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:.*792.*612"


  Scenario: Extract info from pdf with metadata included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       | true             |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Metadata:"


  Scenario: Extract info from pdf with dates decoded
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  | false          |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "CreationDate:.*Fri Feb 23 07:05:43 2024"


  Scenario: Extract info from pdf with dates undecoded
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  | true           |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "CreationDate:.*2024-02-23T07:05:43-08:00"


  Scenario: Extract info from password protected pdf with owner password
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile                |
      | password-protected.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  |                | Secret123     |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract info from password protected pdf with user password
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile                |
      | password-protected.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  |                |               | Secret123    |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract info from pdf with native options
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given native PdfInfoOptions with values
      | -meta | -box |
      |       |      |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:[^\f]+Metadata:"


  Scenario: Extract info from password protected pdf without providing password and get exception
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile                |
      | password-protected.pdf |
    When the PdfInfoTool processes the PdfInfoRequest expecting an XpdfException
    Then the XpdfException should be an XpdfExecutionException
