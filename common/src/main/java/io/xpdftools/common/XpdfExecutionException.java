package io.xpdftools.common;

import lombok.Getter;

/**
 * An {@code Exception} thrown when the executed Xpdf command returns a non-zero exit code.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Getter
public class XpdfExecutionException extends XpdfException {
    private final String standardOutput;
    private final String errorOutput;

    public XpdfExecutionException(String standardOutput,
                                  String errorOutput) {
        super();
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfExecutionException(String standardOutput,
                                  String errorOutput,
                                  String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfExecutionException(String standardOutput,
                                  String errorOutput,
                                  String message,
                                  Throwable cause) {
        super(message, cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfExecutionException(String standardOutput,
                                  String errorOutput,
                                  Throwable cause) {
        super(cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}
