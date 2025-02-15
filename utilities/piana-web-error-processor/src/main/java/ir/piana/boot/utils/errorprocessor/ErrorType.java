package ir.piana.boot.utils.errorprocessor;

import org.springframework.http.HttpStatus;

public interface ErrorType {
    ApiError getApiError();
    HttpStatus getStatus();
}
