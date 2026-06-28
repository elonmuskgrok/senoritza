package com.siva.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String type; // e.g. "Bearer"
    private Long id;
    private String email;
    private String name;
}
