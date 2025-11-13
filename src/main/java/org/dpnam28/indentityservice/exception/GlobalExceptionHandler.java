package org.dpnam28.indentityservice.exception;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    static final String FIELD_EXCEPTION = "min";

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException e) {
        log.error(e.toString());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error(e.toString());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
            var constraintViolation = e.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.error(attributes.toString());
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode, attributes));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {

        if (e.getMessage().contains("LocalDate")) {
            ErrorCode errorCode = ErrorCode.INVALID_DATE;
            return ResponseEntity.status(ErrorCode.INVALID_DATE.getCode()).body(apiResponse(errorCode));
        }
        ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorCode errorCode = ErrorCode.USER_NOT_AUTHORIZED;
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(value = InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        ErrorCode errorCode = ErrorCode.FIELD_NOT_VALID;
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    private ApiResponse<?> apiResponse(ErrorCode errorCode) {
        return ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
    private ApiResponse<?> apiResponse(ErrorCode errorCode, Map<String, Object> attributes) {
        return ApiResponse.builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes) ?
                        mapAttribute(errorCode.getMessage(), attributes) :
                        errorCode.getMessage())
                .build();
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = attributes.get(FIELD_EXCEPTION).toString();
        return message.replace("{" + FIELD_EXCEPTION + "}", minValue);
    }
}
