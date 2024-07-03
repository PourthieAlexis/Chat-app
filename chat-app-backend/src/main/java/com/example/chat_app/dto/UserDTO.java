package com.example.chat_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "Username is required", groups = {Default.class})
    private String username;

    @NotBlank(message = "Password is required", groups = {Default.class})
    private String password;

    @NotBlank(message = "Email is required", groups = {RegistrationValidation.class})
    @Email(message = "Must be a valid email", groups = {RegistrationValidation.class})
    private String email;

    public interface RegistrationValidation {}
}
