package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.CalendarEventRequest;
import com.meudiario.Diary.dto.CalendarEventUpdateRequest;
import com.meudiario.Diary.model.CalendarEvent;
import com.meudiario.Diary.service.CalendarEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class CalendarEventController {

    @Autowired
    private CalendarEventService eventService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CalendarEvent>> getEvents(
            @PathVariable int userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(eventService.getEventsByMonth(userId, year, month));
    }

    @PostMapping
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEventRequest request) {
        return new ResponseEntity<>(eventService.createEvent(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarEvent> updateEvent(@PathVariable Long id, @RequestBody CalendarEventUpdateRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
