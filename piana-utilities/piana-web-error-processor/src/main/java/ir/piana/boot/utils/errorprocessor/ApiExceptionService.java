package ir.piana.boot.utils.errorprocessor;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Locale;

public class ApiExceptionService {

    //region static builders

    public static ApiException customApiException(
            HttpStatus status, String messageKey) {
        return createCustomApiException((Throwable) null, status, messageKey, Locale.getDefault());
    }

    public static ApiException customApiException(
            Throwable throwable, HttpStatus status, String messageKey, Object... params) {
        return createCustomApiException(throwable, status, messageKey, Locale.getDefault(), params);
    }

    public static ApiException customApiException(
            HttpStatus status, String messageKey, Object... params) {
        return createCustomApiException((Throwable) null, status, messageKey, Locale.getDefault(), params);
    }

    public static ApiException customApiException(
            HttpStatus status, String messageKey, Locale locale, Object... params) {
        return createCustomApiException((Throwable) null, status, messageKey, locale, params);
    }

    public static ApiException customApiException(
            Throwable throwable, HttpStatus status, String messageKey, Locale locale, Object... params) {
        return createCustomApiException(throwable, status, messageKey, locale, params);
    }

    public static ApiException customApiException(
            Throwable throwable, HttpStatus httpStatus, ApiError apiError) {
        return createCustomApiException(throwable, httpStatus, apiError);
    }

    private static ApiException createCustomApiException(
            Throwable throwable, HttpStatus status, String messageKey, Locale locale, Object... params) {
        StackTraceElement[] array = LogPropertiesAndFunctionalityProvider.getStackTraceArray(
                throwable == null ? Thread.currentThread().getStackTrace() : throwable.getStackTrace()
        );

        return new ApiException(
                array,
                throwable, messageKey, locale, params) {
            @Override
            public HttpStatus getStatus() {
                return status;
            }
        };

        /*return switch (status) {
            case HttpStatus.BAD_REQUEST -> new AbstractBadRequestException(
                    throwable, code, message, locale, params) {
            };
            case HttpStatus.FORBIDDEN -> new AbstractForbiddenException(
                    throwable, code, message, locale, params) {
            };
            case HttpStatus.NOT_FOUND -> new AbstractNotFoundException(
                    throwable, code, message, locale, params) {
            };
            case HttpStatus.UNAUTHORIZED -> new AbstractUnauthorizedException(
                    throwable, code, message, locale, params) {
            };
            case HttpStatus.INTERNAL_SERVER_ERROR -> new AbstractInternalServerException(
                    throwable, code, message, locale, params) {
            };
            default -> new AbstractInternalServerException(
                    throwable, code, message, locale, params) {
            };
        };*/
    }

    private static ApiException createCustomApiException(
            Throwable throwable, HttpStatus httpStatus, ApiError apiError) {
        StackTraceElement[] array = LogPropertiesAndFunctionalityProvider.getStackTraceArray(
                throwable == null ? Thread.currentThread().getStackTrace() : throwable.getStackTrace()
        );

        return new ApiException(
                array,
                throwable,
                apiError) {
            @Override
            public HttpStatus getStatus() {
                return httpStatus;
            }
        };
    }

    //endregion
}
