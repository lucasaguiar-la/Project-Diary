package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.ForgotPasswordRequest;
import com.meudiario.Diary.dto.LoginRequest;
import com.meudiario.Diary.dto.LoginResponse;
import com.meudiario.Diary.dto.MessageResponse;
import com.meudiario.Diary.dto.RegisterRequest;
import com.meudiario.Diary.dto.RegisterResponse;
import com.meudiario.Diary.dto.ResetPasswordRequest;
import com.meudiario.Diary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(userService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }

}
