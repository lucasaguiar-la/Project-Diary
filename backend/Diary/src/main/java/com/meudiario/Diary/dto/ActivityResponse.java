package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private boolean completedToday;
}
