package model;

import lombok.Data;

@Data
public class PdfToTextResponse {
    public PdfText pdfText;
    public int exitCode;
    public String standardOutput;
}
