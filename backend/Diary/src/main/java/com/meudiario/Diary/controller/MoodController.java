package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.MoodHistoryResponse;
import com.meudiario.Diary.model.MoodTag;
import com.meudiario.Diary.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@CrossOrigin(origins = "*")
public class MoodController {

    @Autowired
    private MoodService moodService;

    @GetMapping
    public ResponseEntity<List<MoodTag>> getAllMoodTags() {
        return ResponseEntity.ok(moodService.getAllMoodTags());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MoodHistoryResponse>> getMoodHistory(@PathVariable int userId) {
        return ResponseEntity.ok(moodService.getMoodHistory(userId));
    }
}
