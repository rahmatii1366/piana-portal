package ir.piana.boot.utils.endpointhandler;

public interface RestQueryParameterSettable {
    RestQueryParameterSettable queryParam(String key, String value);
    RestRequestBuilder then();
//    RestPathParamSettable then();
}
