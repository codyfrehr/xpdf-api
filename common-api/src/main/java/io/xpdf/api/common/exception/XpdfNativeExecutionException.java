package io.xpdf.api.common.exception;

import lombok.Getter;

/**
 * A {@link XpdfException} thrown when a native <em>Xpdf</em> process returns a non-zero exit code.
 *
 * @since 1.0.0
 */
@Getter
public class XpdfNativeExecutionException extends XpdfException {

    /**
     * The standard output of a native <em>Xpdf</em> process.
     *
     * @since 1.0.0
     */
    private final String standardOutput;

    /**
     * The error output of a native <em>Xpdf</em> process.
     *
     * @since 1.0.0
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
