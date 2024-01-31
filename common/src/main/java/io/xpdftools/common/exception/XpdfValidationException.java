package io.xpdftools.common.exception;

import lombok.experimental.StandardException;

/**
 * A {@link XpdfException} thrown when attempting to execute a native <em>Xpdf</em> process with invalid command options.
 *
 * @since 1.0.0
 */
@StandardException
public class XpdfValidationException extends XpdfException {

}
