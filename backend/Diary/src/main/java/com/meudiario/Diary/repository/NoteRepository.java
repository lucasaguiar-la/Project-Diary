package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.NotesForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<NotesForm, Long> {

}
