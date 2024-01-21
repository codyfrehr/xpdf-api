package io.xpdftools.common.exception;

import lombok.experimental.StandardException;

/**
 * A {@link XpdfException} thrown when the duration of a native <em>Xpdf</em> process exceeds the configured timeout length.
 *
 * @author Cody Frehr
 * @since 4.4.0
 */
@StandardException
public class XpdfNativeTimeoutException extends XpdfException {

}
