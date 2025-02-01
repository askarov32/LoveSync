package com.love_sync.demo.service;

import com.love_sync.demo.model.User;
import com.love_sync.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .verified(false)
                .authToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        return user.getAuthToken();
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        user.setAuthToken(UUID.randomUUID().toString());
        userRepository.save(user);

        return user.getAuthToken();
    }

    public Optional<User> getUserByToken(String token) {
        return userRepository.findByAuthToken(token);
    }
}
