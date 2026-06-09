package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequest {
    private String title;
    private int userId;
}
