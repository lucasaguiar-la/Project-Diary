package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUser_IdAndEventDateBetweenOrderByEventDateAscEventTimeAsc(int userId, LocalDate start, LocalDate end);
}
