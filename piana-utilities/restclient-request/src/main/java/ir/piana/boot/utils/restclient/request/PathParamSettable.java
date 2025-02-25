package ir.piana.boot.utils.restclient.request;

public interface PathParamSettable {
    RestRequestBuilder noPathParam();
    PathParamSettable setPathParam(String name, String value);
    RestRequestBuilder then();
}
