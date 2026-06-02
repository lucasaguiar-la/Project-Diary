package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.MoodHistoryResponse;
import com.meudiario.Diary.model.MoodTag;
import com.meudiario.Diary.repository.MoodTagRepository;
import com.meudiario.Diary.repository.NoteRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodService {

    @Autowired
    private MoodTagRepository moodTagRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MoodTag> getAllMoodTags() {
        return moodTagRepository.findAll();
    }

    public List<MoodHistoryResponse> getMoodHistory(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        return noteRepository.findByUser_Id(userId).stream()
                .filter(note -> !note.getMoods().isEmpty())
                .map(note -> new MoodHistoryResponse(note.getId(), note.getTitle(), note.getCreatedAt(), note.getMoods()))
                .toList();
    }
}
