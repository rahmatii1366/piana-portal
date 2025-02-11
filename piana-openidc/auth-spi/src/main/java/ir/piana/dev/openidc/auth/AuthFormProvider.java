package ir.piana.dev.openidc.auth;

public interface AuthFormProvider {
    void initiate(OperationContext context);
    void authenticate(OperationContext context);
}
