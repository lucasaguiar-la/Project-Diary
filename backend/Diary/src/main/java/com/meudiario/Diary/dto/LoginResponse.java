package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private int userId;
    private String firstName;
    private String lastName;
}
