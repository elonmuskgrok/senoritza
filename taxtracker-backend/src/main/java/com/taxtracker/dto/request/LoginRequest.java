package com.taxtracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Please provide a valid Email")
    @Email(message = "Please provide a valid Email")
    private String email;

    @NotBlank(message = "Please provide a valid Password")
    private String password;
}
