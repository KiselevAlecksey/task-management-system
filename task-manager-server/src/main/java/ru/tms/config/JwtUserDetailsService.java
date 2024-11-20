package ru.tms.config;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tms.exception.InvalidJWTClaimsException;
import ru.tms.exception.UnsupportedRoleException;
import ru.tms.user.model.Role;
import ru.tms.user.model.User;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final JwtService jwtService;

    public JwtUserDetailsService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Claims claims = jwtService.validateToken(username);
            if (claims == null) {
                throw new UsernameNotFoundException("Invalid token");
            }

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (email == null || role == null) {
                throw new InvalidJWTClaimsException("Email or role cannot be null");
            }

            Role erole = Role.from(role)
                    .orElseThrow(() -> new UnsupportedRoleException("Не поддерживаемая роль: " + role));
            return new User(email, erole);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Failed to load user details", e);
        }
    }
}
