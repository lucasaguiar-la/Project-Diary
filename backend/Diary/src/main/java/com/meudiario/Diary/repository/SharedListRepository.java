package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.SharedList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedListRepository extends JpaRepository<SharedList, Long> {
    List<SharedList> findByGroup_Id(Long groupId);
}
