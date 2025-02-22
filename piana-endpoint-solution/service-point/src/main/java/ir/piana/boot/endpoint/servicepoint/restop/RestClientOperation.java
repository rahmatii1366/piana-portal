package ir.piana.boot.endpoint.servicepoint.restop;

import ir.piana.boot.endpoint.core.limiter.Bucket4jLimitationException;
import ir.piana.boot.endpoint.core.limiter.SequentialLimitApplier;
import ir.piana.boot.endpoint.core.service.EndpointLogService;
import org.springframework.web.client.RestClient;

public final class RestClientOperation {
    private final RestClient restClient;
    private final EndpointInfo endpointInfo;
    private final EndpointLogService endpointLogService;
    private final SequentialLimitApplier sequentialLimitApplier;

    public RestClientOperation(
            RestClient restClient,
            EndpointLogService endpointLogService,
            SequentialLimitApplier sequentialLimitApplier,
            EndpointInfo endpointInfo) {
        this.restClient = restClient;
        this.sequentialLimitApplier = sequentialLimitApplier;
        this.endpointInfo = endpointInfo;
        this.endpointLogService = endpointLogService;
    }

    public RestClientResponse call(RestClientOperator restClientOperator)
            throws Bucket4jLimitationException {
        sequentialLimitApplier.applyAndThrows();
        endpointLogService.beforeLog(endpointInfo.servicePointId(),
                endpointInfo.endpointId(),
                baseServicePointRequest.getMerchantId(), baseServicePointRequest.getRequesterId(),
                baseServicePointRequest.getReferenceId(), requestDto.toString());
        endpointLogService.afterLog();
    }
}
