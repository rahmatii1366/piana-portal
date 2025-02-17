package ir.piana.boot.utils.endpointlimiter.operation;

import java.util.function.Function;

public interface ServicePointOperation<T, R> extends Function<T, R> {
}
