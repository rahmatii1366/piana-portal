package ir.piana.boot.utils.errorprocessor;

import org.springframework.http.HttpStatus;

import java.util.Locale;

public abstract class ApiException extends RuntimeException implements ErrorType {
    protected final ApiError apiError;
    protected final StackTraceElement[] stackTrace;
//    protected final String stackTrace;

    /*protected ApiException(String code, Object... params) {
        this((Throwable) null, code, code, Locale.getDefault(), params);
    }

    protected ApiException(Throwable throwable, String code, Object... params) {
        this(throwable, code, code, Locale.getDefault(), params);
    }

    protected ApiException(Throwable throwable, String code, Locale locale, Object... params) {
        this(throwable, code, code, locale, params);
    }

    protected ApiException(String code, String message, Object... params) {
        this((Throwable) null, code, message, Locale.getDefault(), params);
    }

    protected ApiException(Throwable throwable, String code, String message, Object... params) {
        this(throwable, code, message, Locale.getDefault(), params);
    }

    protected ApiException(String code, String message, Locale locale, Object... params) {
        this((Throwable) null, code, message, locale, params);
    }

    protected ApiException(Throwable throwable, String code, String message, Locale locale, Object... params) {

    }*/

//    ApiException(
//            StackTraceElement[] stackTrace,
//            Throwable throwable, String code, String message, Locale locale, Object... params) {
//        super(code, throwable);
//        this.stackTrace = stackTrace;
//        this.apiError = new ApiError(
//                code,
//                new ApiError.MessageContainer(
//                        message == null ? code : message, locale, params));
//    }

    ApiException(
            StackTraceElement[] stackTrace,
            Throwable throwable, String messageKey, Locale locale, Object... params) {
        super(messageKey, throwable);
        this.stackTrace = stackTrace;
        this.apiError = new ApiError(
                messageKey,
                new ApiError.MessageContainer(messageKey, locale, params));
    }

    ApiException(
            StackTraceElement[] stackTrace,
            Throwable throwable,
            ApiError apiError) {
        super(apiError.getCode(), throwable);
        this.stackTrace = stackTrace;
        this.apiError = apiError;
    }

    @Override
    public final ApiError getApiError() {
        return apiError;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    @Override
    public abstract HttpStatus getStatus();
}
