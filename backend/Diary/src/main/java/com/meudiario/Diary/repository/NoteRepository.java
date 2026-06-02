package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.NotesForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NotesForm, Long> {

    List<NotesForm> findByUser_Id(int userId);

}
