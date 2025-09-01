package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.LoginRequest;
import com.meudiario.Diary.dto.RegisterRequest;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        // ATENÇÃO: Salvando senha como texto puro. Isso não é seguro para produção.
        // A senha deve ser "hasheada" com um algoritmo como BCrypt.
        newUser.setPassword(request.getPassword());
        return userRepository.save(newUser);
    }

    public Optional<User> login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // ATENÇÃO: Comparação de senha em texto puro. Isso não é seguro.
            if (user.getPassword().equals(request.getPassword())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }

    public User findUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}

