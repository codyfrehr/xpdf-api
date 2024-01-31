package io.xpdf.api.common.exception;

import lombok.experimental.StandardException;

/**
 * A {@link XpdfException} thrown when the duration of a native <em>Xpdf</em> process exceeds the configured timeout length.
 *
 * @since 1.0.0
 */
@StandardException
public class XpdfNativeTimeoutException extends XpdfException {

}
