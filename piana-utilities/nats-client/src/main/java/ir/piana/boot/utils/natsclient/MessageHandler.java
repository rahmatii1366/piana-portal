package ir.piana.boot.utils.natsclient;

import java.util.function.Function;

public interface MessageHandler<T, R> extends Function<T, R> {
    String subject();
    default String queue() {
        return null;
    }
    Class dtoType();
}
