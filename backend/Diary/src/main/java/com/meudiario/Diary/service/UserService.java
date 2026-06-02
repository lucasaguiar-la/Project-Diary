package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.LoginRequest;
import com.meudiario.Diary.dto.LoginResponse;
import com.meudiario.Diary.dto.RegisterRequest;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.UserRepository;
import com.meudiario.Diary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(newUser);
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

}

