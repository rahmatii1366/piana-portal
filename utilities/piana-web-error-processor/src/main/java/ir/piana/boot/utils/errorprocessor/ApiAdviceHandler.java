package ir.piana.boot.utils.errorprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * handle followed exceptions:
 * ApiException
 * RuntimeException
 * MethodArgumentNotValidException
 * ConversionFailedException
 * HttpMessageNotReadableException
 * AuthorizationDeniedException
 * <p>
 * please provide message for bellow codes
 * <p>
 * request.input.not_valid
 * request.body.not_valid
 * access.denied
 * internal.server.error
 * unknown
 * resource.not_found
 * login.loggedIn.already
 * channel.unknown
 * loginFailedCredential.maximumTry.Exceeded
 * loginInHour.maximumNumber.exceeded
 * newPassword.isEqualTo.oldPassword
 * usernameOrPassword.incorrect
 * user.not.authenticated
 */
//@CrossOrigin
//@RestControllerAdvice
public class ApiAdviceHandler {
    protected final static Logger log = LoggerFactory.getLogger(ApiAdviceHandler.class);

    protected final MessageSource messageSource;

    public ApiAdviceHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            ApiException ex) {
        if (ex.getCause() != null && ex.getCause() instanceof NoSuchBeanDefinitionException)
            return handleNoSuchBeanDefinitionException((NoSuchBeanDefinitionException) ex.getCause());

        ApiError interpolation = ex.getApiError().interpolation(messageSource);

        log.error("Error occurred : {}", interpolation.getMessage(), ex);
        /*if (ex.getCause() != null)
            log.error("Error occurred : {}", interpolation.getMessage(), ex);
        else
            log.error("Error occurred : {}", interpolation.getMessage());*/

        return ResponseEntity.status(ex.getStatus()).body(interpolation);
    }

    @ExceptionHandler(NoSuchBeanDefinitionException.class)
    public ResponseEntity<ApiError> handleNoSuchBeanDefinitionException(
            NoSuchBeanDefinitionException ex) {
        log.error("No Bean error occurred : {}", ex.getMessage());
        return ResponseEntity.status(500).body(
                new ApiError("INTERNAL_ERROR").interpolation(messageSource));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            RuntimeException ex) {
        if (ex.getCause() != null && ex.getCause() instanceof NoSuchBeanDefinitionException)
            return handleNoSuchBeanDefinitionException((NoSuchBeanDefinitionException) ex.getCause());
        log.error("Error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(
                new ApiError("INTERNAL_ERROR").interpolation(messageSource));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Error occurred : {}", ex.getMessage(), ex);
        List<ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> validationErrors.add(
                        new ValidationError(e.getField(), e.getDefaultMessage())));

        if (validationErrors.isEmpty())
            return ResponseEntity.badRequest().body(
                    new ApiError("INPUT_NOT_VALID").interpolation(messageSource));
        else
            return ResponseEntity.badRequest().body(
                    new ApiError("INPUT_NOT_VALID").interpolation(messageSource));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ApiError> handleConversionFailedException(
            RuntimeException ex) {
        log.error("Error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ApiError("INPUT_NOT_VALID").interpolation(messageSource));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new ApiError("INPUT_NOT_VALID").interpolation(messageSource));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> authorizationDenied(
            AuthorizationDeniedException ex, WebRequest request) {
        log.error("Error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ApiError("ACCESS_DENIED").interpolation(messageSource));
    }


}
