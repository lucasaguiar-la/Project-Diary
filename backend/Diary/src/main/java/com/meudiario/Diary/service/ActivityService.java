package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.ActivityRequest;
import com.meudiario.Diary.dto.ActivityResponse;
import com.meudiario.Diary.model.Activity;
import com.meudiario.Diary.model.ActivityCompletion;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.ActivityCompletionRepository;
import com.meudiario.Diary.repository.ActivityRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityCompletionRepository completionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ActivityResponse> getActivitiesByUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        LocalDate today = LocalDate.now();
        return activityRepository.findByUser_Id(userId).stream()
                .map(a -> new ActivityResponse(
                        a.getId(),
                        a.getTitle(),
                        a.getCreatedAt(),
                        completionRepository.existsByActivity_IdAndUser_IdAndCompletedDate(a.getId(), userId, today)
                ))
                .toList();
    }

    public Activity createActivity(ActivityRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + request.getUserId()));
        Activity activity = new Activity();
        activity.setTitle(request.getTitle());
        activity.setUser(user);
        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new RuntimeException("Atividade não encontrada com o id: " + id);
        }
        activityRepository.deleteById(id);
    }

    public void completeActivity(Long activityId, int userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com o id: " + activityId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        LocalDate today = LocalDate.now();
        if (!completionRepository.existsByActivity_IdAndUser_IdAndCompletedDate(activityId, userId, today)) {
            ActivityCompletion completion = new ActivityCompletion();
            completion.setActivity(activity);
            completion.setUser(user);
            completion.setCompletedDate(today);
            completionRepository.save(completion);
        }
    }

    public void uncompleteActivity(Long activityId, int userId) {
        LocalDate today = LocalDate.now();
        completionRepository.findByActivity_IdAndUser_IdAndCompletedDate(activityId, userId, today)
                .ifPresent(completionRepository::delete);
    }

    public int getStreak(int userId) {
        List<LocalDate> dates = completionRepository.findDistinctCompletedDatesByUserId(userId);
        if (dates.isEmpty()) return 0;

        LocalDate mostRecent = dates.get(0);
        LocalDate today = LocalDate.now();

        if (mostRecent.isBefore(today.minusDays(1))) return 0;

        int streak = 0;
        LocalDate expected = mostRecent;
        for (LocalDate date : dates) {
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
