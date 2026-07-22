package com.meudiario.Diary.repository;

import com.meudiario.Diary.dto.GroupMemberResponse;
import com.meudiario.Diary.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByGroup_IdAndUser_Id(Long groupId, int userId);

    List<GroupMember> findByUser_Id(int userId);

    long countByGroup_Id(Long groupId);

    @Query("SELECT new com.meudiario.Diary.dto.GroupMemberResponse(" +
           "u.id, u.firstName, u.lastName, m.joinedAt) " +
           "FROM GroupMember m JOIN m.user u " +
           "WHERE m.group.id = :groupId " +
           "ORDER BY m.joinedAt ASC")
    List<GroupMemberResponse> findMembersByGroupId(@Param("groupId") Long groupId);
}
