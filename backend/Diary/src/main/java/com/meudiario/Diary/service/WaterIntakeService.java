package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.WaterResponse;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.model.WaterIntake;
import com.meudiario.Diary.repository.UserRepository;
import com.meudiario.Diary.repository.WaterIntakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class WaterIntakeService {

    @Autowired
    private WaterIntakeRepository waterIntakeRepository;

    @Autowired
    private UserRepository userRepository;

    public WaterResponse getTodayCount(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        LocalDate today = LocalDate.now();
        return waterIntakeRepository.findByUser_IdAndDate(userId, today)
                .map(w -> new WaterResponse(w.getId(), w.getDate(), w.getQuantity()))
                .orElse(new WaterResponse(null, today, 0));
    }

    public WaterResponse incrementToday(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        LocalDate today = LocalDate.now();
        WaterIntake intake = waterIntakeRepository.findByUser_IdAndDate(userId, today)
                .orElseGet(() -> {
                    WaterIntake w = new WaterIntake();
                    w.setUser(user);
                    w.setDate(today);
                    w.setQuantity(0);
                    return w;
                });
        intake.setQuantity(intake.getQuantity() + 1);
        WaterIntake saved = waterIntakeRepository.save(intake);
        return new WaterResponse(saved.getId(), saved.getDate(), saved.getQuantity());
    }

    public WaterResponse decrementToday(int userId) {
        LocalDate today = LocalDate.now();
        WaterIntake intake = waterIntakeRepository.findByUser_IdAndDate(userId, today).orElse(null);
        if (intake == null || intake.getQuantity() <= 1) {
            if (intake != null) {
                waterIntakeRepository.delete(intake);
            }
            return new WaterResponse(null, today, 0);
        }
        intake.setQuantity(intake.getQuantity() - 1);
        WaterIntake saved = waterIntakeRepository.save(intake);
        return new WaterResponse(saved.getId(), saved.getDate(), saved.getQuantity());
    }

    public List<WaterResponse> getHistory(int userId, int days) {
        return waterIntakeRepository.findByUser_IdOrderByDateDesc(userId).stream()
                .limit(days)
                .sorted(Comparator.comparing(WaterIntake::getDate))
                .map(w -> new WaterResponse(w.getId(), w.getDate(), w.getQuantity()))
                .toList();
    }
}
