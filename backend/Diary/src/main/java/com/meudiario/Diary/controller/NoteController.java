package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.NoteRequest;
import com.meudiario.Diary.model.NotesForm;
import com.meudiario.Diary.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<NotesForm> createNote(@RequestBody NoteRequest noteRequest) {
        NotesForm savedNote = noteService.saveNote(noteRequest);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotesForm>> getNotesByUser(@PathVariable int userId) {
        return ResponseEntity.ok(noteService.getNotesByUser(userId));
    }

}
