package ir.piana.boot.utils.endpointhandler;

public interface RestPathParamSettable {
    RestPathParamSettable pathParam(String key, String value);
    RestRequest then();
}
