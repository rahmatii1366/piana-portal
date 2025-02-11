package ir.piana.boot.inquiry.common.nats;

import java.util.function.Function;

public interface MessageHandler<T> extends Function<T, Object> {
    String subject();
    default String queue() {
        return null;
    }
    Class dtoType();
}
