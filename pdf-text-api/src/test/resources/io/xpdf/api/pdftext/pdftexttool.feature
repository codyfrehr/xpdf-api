Feature: PdfTextTool

  Scenario: Convert pdf to text
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile   | textFile |
      | small.pdf |          |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "This is a very small file\s*"


  Scenario: Convert pdf to text with explicit executable file path
    Given a PdfTextTool with 30 second timeout and dynamic executable file
    Given a PdfTextRequest with values
      | pdfFile   | textFile |
      | small.pdf |          |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "This is a very small file\s*"


  Scenario: Convert pdf to text with explicit output text file path
    Given a PdfTextTool
    Given a PdfTextRequest with pdf file small.pdf and dynamic text file
    When the PdfTextTool processes the PdfTextRequest
    Then the output text file should exist


  Scenario: Convert first page of pdf to text
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | multipage.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      | 1         | 1        | RAW    |          |           |                   |               |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "Page 1\r?\n?\f"


  Scenario: Convert pdf to text with utf-8 encoding
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | encodings.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    | UTF_8    |           |                   |               |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "abc джи ☎✈✉\r?\n?\f"


  Scenario: Convert pdf to text with ascii encoding
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | encodings.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    | ASCII_7  |           |                   |               |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "abc  \r?\n?\f"


  Scenario: Convert pdf to text with zapf dingbats encoding
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | encodings.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding      | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    | ZAPF_DINGBATS |           |                   |               |              |
    When the PdfTextTool processes the PdfTextRequest
    # https://help.adobe.com/en_US/framemaker/2015/using/using-framemaker-2015/Appendix/frm_character_sets_cs/frm_character_sets_cs-5.htm
    Then the output text should match "  %\(\)"


  Scenario: Convert pdf to text with unix line breaks
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | multiline.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    |          | UNIX      |                   |               |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "Line 1\nLine 2\nLine 3\n\f"


  Scenario: Convert pdf to text with no page breaks
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | multipage.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    |          |           | true              |               |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "Page 1\r?\n?Page 2\r?\n?Page 3\r?\n?"


  Scenario: Convert password protected pdf to text
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | protected.pdf |          |
    Given a PdfTextOptions with values
      | pageStart | pageStop | format | encoding | endOfLine | pageBreakExcluded | ownerPassword | userPassword |
      |           |          | RAW    |          |           |                   | Secret123     |              |
    When the PdfTextTool processes the PdfTextRequest
    Then the output text should match "This is a password protected file\r?\n?\f"


  Scenario: Convert pdf to text with native verbose option and get non-null standard output
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile   | textFile |
      | small.pdf |          |
    Given native PdfTextOptions with values
      | -verbose |
      |          |
    When the PdfTextTool processes the PdfTextRequest
    Then the standard output should not be null


  Scenario: Convert large pdf to text with small timeout and get exception
    Given a PdfTextTool with values
      | executableFile | timeoutSeconds |
      |                | 1              |
    Given a PdfTextRequest with values
      | pdfFile   | textFile |
      | large.pdf |          |
    When the PdfTextTool processes the PdfTextRequest expecting an XpdfException
    Then the XpdfException should be an XpdfTimeoutException


  Scenario: Convert password protected pdf to text without providing password and get exception
    Given a PdfTextTool
    Given a PdfTextRequest with values
      | pdfFile       | textFile |
      | protected.pdf |          |
    When the PdfTextTool processes the PdfTextRequest expecting an XpdfException
    Then the XpdfException should be an XpdfExecutionException
