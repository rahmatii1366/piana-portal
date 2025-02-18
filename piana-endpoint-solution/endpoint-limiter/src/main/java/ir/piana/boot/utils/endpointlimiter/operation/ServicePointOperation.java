package ir.piana.boot.utils.endpointlimiter.operation;

import java.util.function.Function;

public interface ServicePointOperation<T extends BaseServicePointRequest, R> extends Function<T, R> {
}
