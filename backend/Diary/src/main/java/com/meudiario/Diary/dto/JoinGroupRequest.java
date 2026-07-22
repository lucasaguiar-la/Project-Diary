package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinGroupRequest {
    private String inviteCode;
    private int userId;
}
