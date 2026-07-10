package com.meudiario.Diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WaterResponse {
    private Long id;
    private LocalDate date;
    private int quantity;
}
