package ir.piana.boot.utils.endpointhandler;

public interface RestHeaderSettable {
    RestHeaderSettable header(String key, String... value);
    RestQueryParameterSettable then();
}
