package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.WaterResponse;
import com.meudiario.Diary.service.WaterIntakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/water")
@CrossOrigin(origins = "*")
public class WaterIntakeController {

    @Autowired
    private WaterIntakeService waterIntakeService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<WaterResponse> getToday(@PathVariable int userId) {
        return ResponseEntity.ok(waterIntakeService.getTodayCount(userId));
    }

    @PostMapping("/increment")
    public ResponseEntity<WaterResponse> increment(@RequestParam int userId) {
        return ResponseEntity.ok(waterIntakeService.incrementToday(userId));
    }

    @PostMapping("/decrement")
    public ResponseEntity<WaterResponse> decrement(@RequestParam int userId) {
        return ResponseEntity.ok(waterIntakeService.decrementToday(userId));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<WaterResponse>> getHistory(@PathVariable int userId,
                                                           @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(waterIntakeService.getHistory(userId, days));
    }
}
