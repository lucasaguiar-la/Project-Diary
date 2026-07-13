package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.ActivityRequest;
import com.meudiario.Diary.dto.ActivityResponse;
import com.meudiario.Diary.model.Activity;
import com.meudiario.Diary.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivities(@PathVariable int userId) {
        return ResponseEntity.ok(activityService.getActivitiesByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody ActivityRequest request) {
        return new ResponseEntity<>(activityService.createActivity(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeActivity(@PathVariable Long id, @RequestParam int userId) {
        activityService.completeActivity(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/complete/today")
    public ResponseEntity<Void> uncompleteActivity(@PathVariable Long id, @RequestParam int userId) {
        activityService.uncompleteActivity(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/streak")
    public ResponseEntity<Map<String, Integer>> getStreak(@PathVariable int userId) {
        return ResponseEntity.ok(Map.of("streak", activityService.getStreak(userId)));
    }

    @GetMapping("/user/{userId}/completed-dates")
    public ResponseEntity<List<LocalDate>> getCompletedDates(
            @PathVariable int userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(activityService.getCompletedDatesByMonth(userId, year, month));
    }
}
