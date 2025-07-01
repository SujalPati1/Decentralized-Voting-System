package com.voting.blockvote.service;

import com.voting.blockvote.dto.AuthResponse;
import com.voting.blockvote.dto.LoginRequest;
import com.voting.blockvote.dto.RegisterRequest;
import com.voting.blockvote.model.Role;
import com.voting.blockvote.model.User;
import com.voting.blockvote.repository.UserRepository;
import com.voting.blockvote.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .fullname(request.getFullName())
                .voterId(request.getVoterId())
                .aadharOrPan(request.getAadharOrPan())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VOTER)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Passwords don't match");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }
}
