package com.taxtracker.service;

import com.taxtracker.dto.request.LoginRequest;
import com.taxtracker.dto.request.RegisterRequest;
import com.taxtracker.dto.response.AuthResponse;
import com.taxtracker.entity.User;
import com.taxtracker.repository.UserRepository;
import com.taxtracker.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setName("Test User");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> authService.register(request));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("User already exists", ex.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockJwtToken");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockJwtToken");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("User not found", ex.getMessage());
    }
}
