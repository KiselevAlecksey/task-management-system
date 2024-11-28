package ru.tms.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.tms.user.model.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER(Set.of(USER_READ, USER_CREATE, USER_UPDATE), "USER"),
    ADMIN(Set.of(ADMIN_READ, ADMIN_CREATE, ADMIN_UPDATE, ADMIN_DELETE), "ADMIN"),
    GUEST(Set.of(), "GUEST"),
    USER_REGISTRATION(Set.of(USER_CREATE), "USER_CREATOR");

    private final Set<Permission> permissions;
    private final String roleName;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }

    public static Optional<Role> from(String roleName) {
        for (Role value : values()) {
            if (value.name().equals(roleName)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}

