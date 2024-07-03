package com.example.chat_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.chat_app.dto.AuthResponseDTO;
import com.example.chat_app.dto.UserDTO;
import com.example.chat_app.exception.UserAlreadyExistsException;
import com.example.chat_app.model.User;
import com.example.chat_app.service.AuthService;
import com.example.chat_app.service.JwtService;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());

            User registeredUser = authService.register(user);

            return ResponseEntity.ok(registeredUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated(Default.class) @RequestBody UserDTO userDTO) {
        User authenticatedUser = authService.login(userDTO.getUsername(), userDTO.getPassword());
        
        if (authenticatedUser != null) {
            String token = this.jwtService.generateToken(authenticatedUser);
            return ResponseEntity.ok(new AuthResponseDTO(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
