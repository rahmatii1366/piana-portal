package ir.piana.dev.openidc.auth;

public interface OperationContext {
    <T> T getBodyField(String key);
}
