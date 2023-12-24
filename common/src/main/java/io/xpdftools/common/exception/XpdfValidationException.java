package io.xpdftools.common.exception;

import lombok.experimental.StandardException;

/**
 * A {@code XpdfException} thrown when attempting to execute the Xpdf process with invalid command options.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@StandardException
public class XpdfValidationException extends XpdfException {

}
