package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.LoginRequest;
import com.example.service_marketplace.Dto.RegisterRequest;
import com.example.service_marketplace.Entity.Role;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest dto) {
        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .active(dto.getRole() != Role.PROVIDER) // PROVIDER -> false, others -> true
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // ⬅ Only block PROVIDERS who are inactive
        if (user.getRole() == Role.PROVIDER && !user.isActive()) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", "Your provider account is not active yet. Please wait for admin approval."
            ));
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }



}
