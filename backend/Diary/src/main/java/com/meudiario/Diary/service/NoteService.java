package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.NoteRequest;
import com.meudiario.Diary.model.NotesForm;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.NoteRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public NotesForm saveNote(NoteRequest noteRequest) {

        User user = userRepository.findById(Math.toIntExact(noteRequest.getUserId()))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + noteRequest.getUserId()));

        NotesForm newNote = new NotesForm();

        newNote.setTitle(noteRequest.getTitle());
        newNote.setContent(noteRequest.getContent());
        newNote.setUser(user);

        return noteRepository.save(newNote);
    }

    public List<NotesForm> getNotesByUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        return noteRepository.findByUser_Id(userId);
    }

}
