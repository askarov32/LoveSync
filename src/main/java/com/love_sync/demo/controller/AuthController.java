package com.love_sync.demo.controller;

import com.love_sync.demo.model.User;
import com.love_sync.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String token = authService.registerUser(request.get("email"), request.get("password"), request.get("name"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String token = authService.loginUser(request.get("email"), request.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Optional<User> user = authService.getUserByToken(token);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(Map.of(
                "email", user.get().getEmail(),
                "name", user.get().getName()
        ));
    }
}
