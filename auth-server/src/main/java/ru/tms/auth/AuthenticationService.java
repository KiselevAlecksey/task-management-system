package ru.tms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.auth.dto.ChangePasswordRequest;

import java.io.IOException;
import java.security.Principal;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest loginRequest);

    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    AuthenticationResponse register(RegisterRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
