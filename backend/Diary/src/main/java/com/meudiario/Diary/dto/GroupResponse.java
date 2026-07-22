package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String inviteCode;
    private LocalDateTime createdAt;
    private int memberCount;
}
