package org.dpnam28.indentityservice.enums;

import lombok.Getter;

@Getter
public enum Roles {
    ADMIN("Admin role"),
    USER("User role")
    ;

    private final String description;
    Roles(String description){
        this.description = description;
    }
}
