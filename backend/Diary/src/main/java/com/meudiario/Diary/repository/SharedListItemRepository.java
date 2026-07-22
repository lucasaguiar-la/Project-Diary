package com.meudiario.Diary.repository;

import com.meudiario.Diary.model.SharedListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedListItemRepository extends JpaRepository<SharedListItem, Long> {
    List<SharedListItem> findBySharedList_Id(Long listId);
}
