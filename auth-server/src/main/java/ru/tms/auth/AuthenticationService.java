package ru.tms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.RegisterRequest;

import java.io.IOException;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest loginRequest);

    AuthenticationResponse register(RegisterRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
