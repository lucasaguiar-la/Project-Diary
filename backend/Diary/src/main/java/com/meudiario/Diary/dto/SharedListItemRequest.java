package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharedListItemRequest {
    private String title;
    private Long listId;
    private int userId;
}
