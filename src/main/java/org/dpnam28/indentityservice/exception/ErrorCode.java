package org.dpnam28.indentityservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("Internal server error", 500),
    USERNAME_NOT_VALID("Username must be at least 5 characters", 400),
    PASSWORD_NOT_VALID("Password must be at least 5 characters", 400),
    USER_EXISTED("User already existed", 400),
    USER_NOT_FOUND("User not found", 404),
    PASSWORD_NOT_MATCH("Password not match", 400),
    USER_NOT_AUTHENTICATED("User not authenticated", 401),
    TOKEN_NOT_VALID("Token not valid", 400),
    INVALID_DATE("Date must be in format yyyy-MM-dd", 400),
    INVALID_JSON_FORMAT("Invalid json format", 400),
    USER_NOT_AUTHORIZED("User not authorized", 403),
    PERMISSION_NOT_FOUND("Permission not found", 404),
    PERMISSION_EXISTED("Permission already existed", 400),
    ROLE_NOT_FOUND("Role not found", 404),
    ROLE_EXISTED("Role already existed", 400),
    FIELD_NOT_VALID("Field not valid", 400),
    DATA_INTEGRITY_VIOLATION("Data integrity violation", 400),

    ;
    private final int code;
    private final String message;

    ErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

}
