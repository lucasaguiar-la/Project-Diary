package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.CalendarEventRequest;
import com.meudiario.Diary.dto.CalendarEventUpdateRequest;
import com.meudiario.Diary.model.CalendarEvent;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.CalendarEventRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarEventService {

    @Autowired
    private CalendarEventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CalendarEvent> getEventsByMonth(int userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return eventRepository.findByUser_IdAndEventDateBetweenOrderByEventDateAscEventTimeAsc(userId, start, end);
    }

    public CalendarEvent createEvent(CalendarEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + request.getUserId()));
        CalendarEvent event = new CalendarEvent();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setUser(user);
        return eventRepository.save(event);
    }

    public CalendarEvent updateEvent(Long id, CalendarEventUpdateRequest request) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o id: " + id));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Evento não encontrado com o id: " + id);
        }
        eventRepository.deleteById(id);
    }
}
