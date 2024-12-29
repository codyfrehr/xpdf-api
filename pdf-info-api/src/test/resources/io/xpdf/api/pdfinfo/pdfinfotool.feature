Feature: PdfInfoTool

  Scenario: Extract pdf info
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract pdf info with explicit executable file path
    Given a PdfInfoTool with 30 second timeout and dynamic executable file
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract pdf info with latin encoding
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          | LATIN_1  |                       |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Creator:.*Microsoft�"


  Scenario: Extract pdf info with utf-8 encoding
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          | UTF_8    |                       |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Creator:.*Microsoft®"


  Scenario: Extract pdf info with ascii encoding
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          | ASCII_7  |                       |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Creator:.*Microsoft\(R\)"


  Scenario: Extract pdf info for first page with bounding boxes included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile       |
      | multisized.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      | 1         | 1        |          | true                  |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:.*612.*792"


  Scenario: Extract pdf info for second page with bounding boxes included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile       |
      | multisized.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      | 2         | 2        |          | true                  |                  |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:.*792.*612"


  Scenario: Extract pdf info with metadata included
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       | true             |                |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Metadata:"


  Scenario: Extract pdf info with dates decoded
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  | false          |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "CreationDate:.*Fri Feb 23 07:05:43 2024"


  Scenario: Extract pdf info with dates undecoded
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  | true           |               |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "CreationDate:.*2024-02-23T07:05:43-08:00"


  Scenario: Extract pdf info from password protected pdf
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile       |
      | protected.pdf |
    Given a PdfInfoOptions with values
      | pageStart | pageStop | encoding | boundingBoxesIncluded | metadataIncluded | datesUndecoded | ownerPassword | userPassword |
      |           |          |          |                       |                  |                | Secret123     |              |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "Author:[^\f]+PDF version:"


  Scenario: Extract pdf info with native options
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile   |
      | small.pdf |
    Given native PdfInfoOptions with values
      | -meta | -box |
      |       |      |
    When the PdfInfoTool processes the PdfInfoRequest
    Then the standard output should match "MediaBox:[^\f]+Metadata:"


  Scenario: Extract pdf info from password protected pdf without providing password and get exception
    Given a PdfInfoTool
    Given a PdfInfoRequest with values
      | pdfFile       |
      | protected.pdf |
    When the PdfInfoTool processes the PdfInfoRequest expecting an XpdfException
    Then the XpdfException should be an XpdfExecutionException
