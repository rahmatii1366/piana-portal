package ir.piana.boot.endpoint.core.service;

public interface EndpointLogService {
    void beforeLog(
            long servicePointId, long endpointId, long merchantId, long requesterId,
            String referenceId, String requestBody);
    void afterLog(
            long servicePointId, long endpointId, long merchantId, long requesterId,
            String referenceId, int httpStatusCode, String responseBody);
}
