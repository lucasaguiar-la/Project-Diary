package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharedListRequest {
    private String name;
    private Long groupId;
    private int userId;
}
