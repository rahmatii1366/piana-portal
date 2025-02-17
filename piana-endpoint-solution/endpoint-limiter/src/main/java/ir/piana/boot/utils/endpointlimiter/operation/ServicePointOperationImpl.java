/*
package ir.piana.boot.utils.endpointlimiter.operation;

import ir.piana.boot.endpoint.dto.ServicePointDto;
import ir.piana.boot.utils.errorprocessor.InternalServerErrorTypes;
import org.springframework.context.ApplicationContext;

import java.util.List;

final class ServicePointOperationImpl<T, R> implements ServicePointOperation<T, R> {
    private final ApplicationContext applicationContext;
    private final ServicePointDto servicePointDto;
    private final List<RestClientOperationHandleable<T, R>> operationHandleableList;

    public ServicePointOperationImpl(
            ApplicationContext applicationContext,
            ServicePointDto servicePointDto,
            List<RestClientOperationHandleable<T, R>> operationHandleableList) {
        this.applicationContext = applicationContext;
        this.servicePointDto = servicePointDto;
        this.operationHandleableList = operationHandleableList.stream()
                .filter(restClientOperationHandleable ->
                        restClientOperationHandleable.servicePointName()
                                .equals(servicePointDto.name())
                ).toList();
    }

    @Override
    public final R apply(T requestDto) {
        for (RestClientOperationHandleable<T, R> restClientOperationHandleable : operationHandleableList) {
            try {
                R apply = restClientOperationHandleable.apply(requestDto);
                return apply;
            } catch (LimitationException e) {
                // limitation throws exception, so next handleable should be call
            }
        }
        throw InternalServerErrorTypes.NO_ACTIVE_ENDPOINT.newException();
    }
}
*/
