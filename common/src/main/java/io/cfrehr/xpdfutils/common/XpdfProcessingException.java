package io.cfrehr.xpdfutils.common;

import lombok.Getter;

@Getter
public class XpdfProcessingException extends XpdfException {
    private final String standardOutput;
    private final String errorOutput;

    public XpdfProcessingException(String standardOutput,
                                   String errorOutput) {
        super();
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfProcessingException(String standardOutput,
                                   String errorOutput,
                                   String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfProcessingException(String standardOutput,
                                   String errorOutput,
                                   String message,
                                   Throwable cause) {
        super(message, cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfProcessingException(String standardOutput,
                                   String errorOutput,
                                   Throwable cause) {
        super(cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}