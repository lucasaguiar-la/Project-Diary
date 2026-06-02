package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private int userId;
    private String firstName;
    private String lastName;
}
