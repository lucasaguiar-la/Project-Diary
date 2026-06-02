package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.NoteRequest;
import com.meudiario.Diary.dto.NoteUpdateRequest;
import com.meudiario.Diary.model.NotesForm;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.MoodTagRepository;
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

    @Autowired
    private MoodTagRepository moodTagRepository;

    public NotesForm saveNote(NoteRequest noteRequest) {

        User user = userRepository.findById(Math.toIntExact(noteRequest.getUserId()))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + noteRequest.getUserId()));

        NotesForm newNote = new NotesForm();

        newNote.setTitle(noteRequest.getTitle());
        newNote.setContent(noteRequest.getContent());
        newNote.setUser(user);

        if (noteRequest.getMoodIds() != null && !noteRequest.getMoodIds().isEmpty()) {
            newNote.setMoods(moodTagRepository.findAllById(noteRequest.getMoodIds()));
        }

        return noteRepository.save(newNote);
    }

    public List<NotesForm> getNotesByUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        return noteRepository.findByUser_Id(userId);
    }

    public NotesForm getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada com o id: " + id));
    }

    public NotesForm updateNote(Long id, NoteUpdateRequest request) {
        NotesForm note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota não encontrada com o id: " + id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Nota não encontrada com o id: " + id);
        }
        noteRepository.deleteById(id);
    }

}
