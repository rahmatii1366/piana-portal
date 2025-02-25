package ir.piana.boot.utils.restclient.request;

public interface QueryParamSettable {
    PathParamSettable noQueryParam();
    QueryParamSettable setQueryParam(String name, String value);
    PathParamSettable then();
}
