package ru.tms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ru.tms.user.model.Permission.*;
import static ru.tms.user.model.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private static final String[] WHITE_LIST_URL = {"/v1/api/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/", "/**").permitAll()
                                .requestMatchers("/users/**").hasAnyAuthority(ADMIN.getRoleName(), USER_REGISTRATION.getRoleName())
                                .requestMatchers(GET, "/users/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(POST, "/users/**").hasAnyAuthority(ADMIN_CREATE.name(), USER_REGISTRATION.name())
                                .requestMatchers(PATCH, "/users/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, "/users/**").hasAnyAuthority(ADMIN_DELETE.name())
                                .requestMatchers("/users/tasks**").hasAnyAuthority(ADMIN.getRoleName(), USER.getRoleName())
                                .requestMatchers(PATCH, "/users/tasks**").hasAnyAuthority(ADMIN_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers("/admin/**").hasAuthority(ADMIN.getRoleName())
                                .requestMatchers(POST, "/admin/**").hasAnyAuthority(ADMIN_CREATE.name())
                                .requestMatchers(PATCH, "/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, "/admin/**").hasAnyAuthority(ADMIN_DELETE.name())
                                .requestMatchers("/tasks/**").hasAnyAuthority(ADMIN.getRoleName(), USER.getRoleName())
                                .requestMatchers(POST, "/tasks/**").hasAnyAuthority(ADMIN_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(GET, "/tasks/**").hasAnyAuthority(ADMIN_READ.name(), USER_READ.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
