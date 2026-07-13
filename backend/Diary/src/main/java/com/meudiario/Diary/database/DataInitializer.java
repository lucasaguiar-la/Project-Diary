package com.meudiario.Diary.database;

import com.meudiario.Diary.model.MoodTag;
import com.meudiario.Diary.repository.MoodTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MoodTagRepository moodTagRepository;

    @Override
    public void run(String... args) {
        ensureMood("feliz",    "😊");
        ensureMood("neutro",   "😐");
        ensureMood("triste",   "😢");
        ensureMood("ansioso",  "😟");
        ensureMood("calmo",    "😌");
        ensureMood("raiva",    "😠");
        ensureMood("amor",     "😍");
        ensureMood("surpresa", "😲");
    }

    private void ensureMood(String title, String emoji) {
        if (moodTagRepository.findByTitle(title).isEmpty()) {
            MoodTag tag = new MoodTag();
            tag.setTitle(title);
            tag.setEmoji(emoji);
            moodTagRepository.save(tag);
        }
    }
}
