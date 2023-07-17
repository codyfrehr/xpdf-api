package io.cfrehr.xpdfutils.common;

import lombok.Builder;

@Builder
public class XpdfException extends Exception {

    public XpdfException() {
        super();
    }

    public XpdfException(String message) {
        super(message);
    }

    public XpdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public XpdfException(Throwable cause) {
        super(cause);
    }
}
