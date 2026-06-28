package com.siva.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Please provide a valid Name")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+){0,2}$", message = "Name must contain 1-3 words, alphabets only, separated by single spaces.")
    private String name;

    @NotBlank(message = "Please provide a valid Email")
    @Email(message = "Please provide a valid Email")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|in)$", message = "Email domain must end in .in or .com")
    private String email;

    @NotBlank(message = "Please provide a valid Password")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|]).{7,20}$", message = "Password must be 7-20 characters with at least one uppercase, lowercase, digit, and special character.")
    private String password;

    @NotBlank(message = "Please provide a valid Mobile Number")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile Number must be exactly 10 digits and start with 6, 7, 8, or 9")
    private String mobileNumber;

    @NotBlank(message = "Please provide a valid Address Line 1")
    private String addressLine1;

    @NotBlank(message = "Please provide a valid Address Line 2")
    private String addressLine2;

    @NotBlank(message = "Please provide a valid Area")
    private String area;

    @NotBlank(message = "Please provide a valid City")
    private String city;

    @NotBlank(message = "Please provide a valid State")
    private String state;

    @NotBlank(message = "Please provide a valid PIN Code")
    @Pattern(regexp = "^\\d{6}$", message = "PIN Code must be exactly 6 digits")
    private String pincode;
}
