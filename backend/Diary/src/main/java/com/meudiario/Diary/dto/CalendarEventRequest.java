package com.meudiario.Diary.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CalendarEventRequest {
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private int userId;
}
