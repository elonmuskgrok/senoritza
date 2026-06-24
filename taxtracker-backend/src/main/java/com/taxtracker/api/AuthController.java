package com.taxtracker.api;

import com.taxtracker.dto.request.LoginRequest;
import com.taxtracker.dto.request.RegisterRequest;
import com.taxtracker.dto.response.ApiResponse;
import com.taxtracker.dto.response.AuthResponse;
import com.taxtracker.service.AuthService;
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
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return new ResponseEntity<>(
                new ApiResponse("Registration successful", true, null),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
