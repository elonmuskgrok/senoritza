package com.siva.api;

import com.siva.dto.LoginRequestDTO;
import com.siva.dto.RegisterRequestDTO;
import com.siva.dto.ApiResponse;
import com.siva.dto.AuthResponse;
import com.siva.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return new ResponseEntity<>(
                new ApiResponse("Registration successful", true, null),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
