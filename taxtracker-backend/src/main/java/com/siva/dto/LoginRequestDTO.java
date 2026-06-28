package com.siva.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Please provide a valid Email")
    @Email(message = "Please provide a valid Email")
    private String email;

    @NotBlank(message = "Please provide a valid Password")
    private String password;
}
