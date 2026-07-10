package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    Optional<WaterIntake> findByUser_IdAndDate(int userId, LocalDate date);

    List<WaterIntake> findByUser_IdOrderByDateDesc(int userId);
}
