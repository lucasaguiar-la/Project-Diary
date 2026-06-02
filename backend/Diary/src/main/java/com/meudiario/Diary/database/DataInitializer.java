package com.meudiario.Diary.database;

import com.meudiario.Diary.model.MoodTag;
import com.meudiario.Diary.repository.MoodTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MoodTagRepository moodTagRepository;

    @Override
    public void run(String... args) {
        if (moodTagRepository.count() > 0) return;

        moodTagRepository.saveAll(List.of(
            mood("feliz",   "😊"),
            mood("neutro",  "😐"),
            mood("triste",  "😢"),
            mood("ansioso", "😟"),
            mood("calmo",   "😌")
        ));
    }

    private MoodTag mood(String title, String emoji) {
        MoodTag tag = new MoodTag();
        tag.setTitle(title);
        tag.setEmoji(emoji);
        return tag;
    }
}
