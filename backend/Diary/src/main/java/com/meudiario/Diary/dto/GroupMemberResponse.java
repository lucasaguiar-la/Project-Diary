package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupMemberResponse {
    private int userId;
    private String firstName;
    private String lastName;
    private LocalDateTime joinedAt;
}
