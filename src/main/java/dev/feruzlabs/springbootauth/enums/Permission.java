package dev.feruzlabs.springbootauth.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Permission {
    // User permissions
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),

    // Product permissions
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_DELETE("product:delete"),

    // Admin permissions
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_DELETE("admin:delete"),

    // System permissions
    SYSTEM_READ("system:read"),
    SYSTEM_WRITE("system:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}
