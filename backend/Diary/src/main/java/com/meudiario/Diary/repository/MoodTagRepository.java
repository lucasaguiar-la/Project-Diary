package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.MoodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoodTagRepository extends JpaRepository<MoodTag, Integer> {
    Optional<MoodTag> findByTitle(String title);
}
