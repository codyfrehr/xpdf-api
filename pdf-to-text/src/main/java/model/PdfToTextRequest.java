package model;

import lombok.Data;

@Data
public class PdfToTextRequest {
    private PdfToTextOptions options;
    private String filePathPdf;
//    private String filePathTxt = Path.GetTempPath() + Guid.NewGuid() + ".txt";
}
