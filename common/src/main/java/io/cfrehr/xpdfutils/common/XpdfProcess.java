package io.cfrehr.xpdfutils.common;

import java.util.List;

//todo: do you really need this abstract class?
// you are only using this as a means of enforcing that everything that implements the interface will have the same hidden helper methods...
// kind of overkill, and sort of confusing.
// there are only like 6 or 7 possible implementations of interface - this could be unnecessary (but not sure)
//todo: come back to this later and decide if something like this needs to be implemented...
public abstract class XpdfProcess<Request, Response> implements XpdfUtility<Request, Response> {
    protected abstract List<String> getCommandOptions(Request request);
    protected abstract void validate(Request request) throws XpdfException;
}
