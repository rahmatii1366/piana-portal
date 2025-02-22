package ir.piana.boot.endpoint.servicepoint.dto;

import java.util.function.Function;

public interface ServicePointOperation<T extends BaseServicePointRequest, R>
        extends Function<T, R> {
}
