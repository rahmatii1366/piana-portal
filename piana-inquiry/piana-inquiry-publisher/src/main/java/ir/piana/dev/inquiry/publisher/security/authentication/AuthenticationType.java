package ir.piana.dev.inquiry.publisher.security.authentication;

import java.util.Arrays;
import java.util.Optional;

public enum AuthenticationType {
    Basic("basic"),
    Bearer("bearer"),
    BearerJWT("bearer-jwt")
    ;

    private String name;

    AuthenticationType(String name) {
        this.name = name;
    }

    public static Optional<AuthenticationType> byName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        Optional<AuthenticationType> any = Arrays.stream(AuthenticationType.values())
                .filter(type -> name.equalsIgnoreCase(type.name))
                .findAny();
        return any;
    }

    public static Optional<AuthenticationType> byNameOrThrows(String name) {
//        ApiException apiException = pre.customApiException(HttpStatus.BAD_REQUEST, "authentication-type-header.not-specified");
        if (name == null) {
            return null;
        }
        Optional<AuthenticationType> any = Arrays.stream(AuthenticationType.values())
                .filter(type -> name.equalsIgnoreCase(type.name))
                .findAny();
        return any;
    }
}
