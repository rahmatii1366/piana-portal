package ir.piana.boot.endpoint.manager;

public interface RestClientManager {
    void validateAuth();
    void invalidateAuth();
    void doRequest();
}
