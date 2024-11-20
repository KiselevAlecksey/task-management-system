package ru.tms.auth;

import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class AuthControllerRefreshTest {

    @InjectMocks
    private AuthenticationController authController;

    @Mock
    private AuthenticationService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRefreshToken() throws IOException {
        authController.refreshToken(request, response);

        verify(authService, times(1)).refreshToken(request, response);
    }
}
