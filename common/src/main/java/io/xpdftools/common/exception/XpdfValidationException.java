package io.xpdftools.common.exception;

import lombok.experimental.StandardException;

/**
 * A {@link XpdfException} thrown when attempting to execute a native <em>Xpdf</em> process with invalid command options.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@StandardException
public class XpdfValidationException extends XpdfException {

}
