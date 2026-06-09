package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUser_Id(int userId);
}
