package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NoteRequest {

    private String title;
    private String content;
    private Long userId;
    private List<Integer> moodIds;

}
