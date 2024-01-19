package io.xpdftools.common.exception;

import lombok.Getter;

/**
 * A {@code XpdfException} thrown when a native <em>Xpdf</em> process returns a non-zero exit code.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Getter
public class XpdfNativeExecutionException extends XpdfException {
    private final String standardOutput;
    private final String errorOutput;

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput) {
        super();
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput,
                                        String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput,
                                        String message,
                                        Throwable cause) {
        super(message, cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput,
                                        Throwable cause) {
        super(cause);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}
