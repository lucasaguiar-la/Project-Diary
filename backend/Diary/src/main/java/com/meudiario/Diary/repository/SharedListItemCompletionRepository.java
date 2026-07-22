package com.meudiario.Diary.repository;

import com.meudiario.Diary.dto.SharedListHistoryResponse;
import com.meudiario.Diary.model.SharedListItemCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SharedListItemCompletionRepository extends JpaRepository<SharedListItemCompletion, Long> {

    // O estado "concluido hoje" e do item, nao do usuario: numa lista compartilhada,
    // se qualquer membro marcou, o item fica concluido pra todo mundo.
    boolean existsByItem_IdAndCompletedDate(Long itemId, LocalDate date);

    Optional<SharedListItemCompletion> findByItem_IdAndCompletedDate(Long itemId, LocalDate date);

    List<SharedListItemCompletion> findByItem_Id(Long itemId);

    @Query("SELECT new com.meudiario.Diary.dto.SharedListHistoryResponse(" +
           "c.id, i.title, u.firstName, u.lastName, c.completedDate, c.completedAt) " +
           "FROM SharedListItemCompletion c " +
           "JOIN c.item i " +
           "JOIN c.user u " +
           "WHERE i.sharedList.id = :listId " +
           "ORDER BY c.completedAt DESC")
    List<SharedListHistoryResponse> findHistoryByListId(@Param("listId") Long listId);
}
