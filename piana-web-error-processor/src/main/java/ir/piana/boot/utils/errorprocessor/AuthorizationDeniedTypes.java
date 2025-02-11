package ir.piana.boot.utils.errorprocessor;

import org.springframework.http.HttpStatus;

import java.util.Locale;

public enum AuthorizationDeniedTypes implements ErrorType {
    AccessDenied("access.denied"),
    ;

    private final ApiError apiError;

    AuthorizationDeniedTypes(String messageKey) {
        apiError = new ApiError(
                messageKey, new ApiError.MessageContainer(messageKey, Locale.getDefault()));
    }

    public ApiError getApiError() {
        return apiError;
    }

    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    public ApiException newException(Throwable throwable) {
        return ApiExceptionService.customApiException(throwable, this.getStatus(), this.apiError);

    }

    public ApiException newException() {
        return ApiExceptionService.customApiException(null, this.getStatus(), this.apiError);
    }
}
