package dev.feruzlabs.springbootauth.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.feruzlabs.springbootauth.enums.Permission.*;


@Getter
public enum Role {
    USER(Set.of(USER_READ, PRODUCT_READ)),
    MANAGER(Set.of(USER_READ, USER_WRITE, PRODUCT_READ, PRODUCT_WRITE)),
    ADMIN(Set.of(USER_READ, USER_WRITE, USER_DELETE, PRODUCT_READ, PRODUCT_WRITE, PRODUCT_DELETE, ADMIN_READ, ADMIN_WRITE)),
    SUPER_ADMIN(Set.of(USER_READ, USER_WRITE, USER_DELETE, PRODUCT_READ, PRODUCT_WRITE, PRODUCT_DELETE,
            ADMIN_READ, ADMIN_WRITE, ADMIN_DELETE, SYSTEM_READ, SYSTEM_WRITE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getSimpleGrantedAuthority() {
        var authorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }


}
