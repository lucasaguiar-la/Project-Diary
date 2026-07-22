package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SharedListHistoryResponse {
    private Long completionId;
    private String itemTitle;
    private String firstName;
    private String lastName;
    private LocalDate completedDate;
    private LocalDateTime completedAt;
}
