package org.dpnam28.indentityservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
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
        try {
            errorCode = ErrorCode.valueOf(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse(errorCode));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        log.error(ex.getMessage());
        if (ex.getMessage().contains("LocalDate")) {
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

    private ApiResponse<?> apiResponse(ErrorCode errorCode){
        return  ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}
