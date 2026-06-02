package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.MoodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodTagRepository extends JpaRepository<MoodTag, Integer> {
}
