package common;

public interface XpdfUtility<Request, Response> {
    Response process(Request request);
}
