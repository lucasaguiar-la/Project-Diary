package com.meudiario.Diary.dto;

import com.meudiario.Diary.model.MoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MoodHistoryResponse {
    private Long noteId;
    private String noteTitle;
    private LocalDateTime createdAt;
    private List<MoodTag> moods;
}
