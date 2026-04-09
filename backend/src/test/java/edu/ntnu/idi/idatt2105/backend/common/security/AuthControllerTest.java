package edu.ntnu.idi.idatt2105.backend.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerReturns201Created() {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest(
                "staff@example.com",
                "Password123",
                "Test",
                "User",
                "123456789",
                "12345678",
                null);

        AuthDtos.LoginResponse loginResponse = new AuthDtos.LoginResponse(
                "access-token",
                "refresh-token",
                "staff@example.com",
                "Test User",
                10L,
                "STAFF");

        when(authService.register(request)).thenReturn(loginResponse);

        ResponseEntity<ApiResponse<AuthDtos.LoginResponse>> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User registered successfully", response.getBody().getMessage());
        assertEquals("staff@example.com", response.getBody().getData().email());
        verify(authService).register(request);
    }
}

