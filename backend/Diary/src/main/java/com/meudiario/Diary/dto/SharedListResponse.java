package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SharedListResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
