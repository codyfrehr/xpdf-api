package io.xpdftools.common.exception;

import lombok.Getter;

/**
 * A {@link XpdfException} thrown when a native <em>Xpdf</em> process returns a non-zero exit code.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@Getter
public class XpdfNativeExecutionException extends XpdfException {

    /**
     * The standard output of a native <em>Xpdf</em> process.
     */
    private final String standardOutput;

    /**
     * The error output of a native <em>Xpdf</em> process.
     */
    private final String errorOutput;

    public XpdfNativeExecutionException(String standardOutput,
                                        String errorOutput,
                                        String message) {
        super(message);
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
    }

}
