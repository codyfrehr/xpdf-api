package io.cfrehr.xpdfutils.common;

import java.util.List;

public abstract class XpdfProcess<Request, Response> implements XpdfUtility<Request, Response> {
    protected abstract List<String> getArguments(Request request);
    protected abstract void validate(Request request) throws XpdfException;
}
