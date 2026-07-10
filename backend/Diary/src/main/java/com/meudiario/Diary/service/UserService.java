package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.ForgotPasswordRequest;
import com.meudiario.Diary.dto.LoginRequest;
import com.meudiario.Diary.dto.LoginResponse;
import com.meudiario.Diary.dto.MessageResponse;
import com.meudiario.Diary.dto.RegisterRequest;
import com.meudiario.Diary.dto.RegisterResponse;
import com.meudiario.Diary.dto.ResetPasswordRequest;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.UserRepository;
import com.meudiario.Diary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Value("${app.reset-token-expiration-minutes}")
    private long resetTokenExpirationMinutes;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(newUser);
        return new RegisterResponse(saved.getId(), saved.getFirstName(), saved.getLastName());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas.");
        }
        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getId(), user.getFirstName(), user.getLastName());
    }

    public User findUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(Instant.now().plus(resetTokenExpirationMinutes, ChronoUnit.MINUTES));
            userRepository.save(user);
            try {
                emailService.sendPasswordResetEmail(user.getEmail(), token);
            } catch (Exception e) {
                // Falha de envio não pode vazar diferença de comportamento entre e-mail existente/inexistente.
            }
        });
        return new MessageResponse("Se o e-mail informado estiver cadastrado, um link de redefinição foi enviado.");
    }

    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token inválido ou expirado.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Senha redefinida com sucesso.");
    }

}

