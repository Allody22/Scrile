package ru.scrile.org.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scrile.org.payload.request.AuthRequest;
import ru.scrile.org.payload.request.RegistrationRequest;
import ru.scrile.org.service.UserService;

import javax.validation.Valid;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        userService.register(registrationRequest.getName(), registrationRequest.getPassword(), registrationRequest.getAge());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody AuthRequest authRequest) {
        try {
            return ResponseEntity.ok(userService.authenticate(authRequest.getUsername(), authRequest.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed");
        }
    }

}
