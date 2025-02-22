package ir.piana.boot.endpoint.servicepoint;

import java.util.function.Function;

public interface ServicePointOperation<T, R> extends Function<ServicePointRequest<T>, R> {
}
