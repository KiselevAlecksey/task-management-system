package ru.tms.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tms.auditing.ApplicationAuditAware;
import ru.tms.user.UserRepository;
import ru.tms.user.model.Role;
import ru.tms.user.model.User;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig implements BeanPostProcessor {

    private final JwtService jwtService;

    @Bean
    public UserDetailsService userDetailsService() {
        return jwt -> {
            String username = jwtService.extractUsername(jwt);

            Claims claims = jwtService.extractAllClaims(jwt);

            if (claims == null) {
                throw new UsernameNotFoundException("Invalid token");
            }

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (email == null || role == null) {
                throw new UsernameNotFoundException("Invalid JWT claims");
            }

            Role erole = Role.from(role)
                    .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемая роль: " + role));
            return new User(username, erole);
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

