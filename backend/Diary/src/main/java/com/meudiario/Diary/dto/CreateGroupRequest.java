package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    private String name;
    private int userId;
}
