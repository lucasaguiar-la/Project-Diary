package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.ActivityCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityCompletionRepository extends JpaRepository<ActivityCompletion, Long> {

    boolean existsByActivity_IdAndUser_IdAndCompletedDate(Long activityId, int userId, LocalDate date);

    Optional<ActivityCompletion> findByActivity_IdAndUser_IdAndCompletedDate(Long activityId, int userId, LocalDate date);

    @Query("SELECT DISTINCT ac.completedDate FROM ActivityCompletion ac WHERE ac.user.id = :userId ORDER BY ac.completedDate DESC")
    List<LocalDate> findDistinctCompletedDatesByUserId(@Param("userId") int userId);
}
