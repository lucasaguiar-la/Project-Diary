package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.CreateGroupRequest;
import com.meudiario.Diary.dto.GroupMemberResponse;
import com.meudiario.Diary.dto.GroupResponse;
import com.meudiario.Diary.dto.JoinGroupRequest;
import com.meudiario.Diary.model.Group;
import com.meudiario.Diary.model.GroupMember;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.GroupMemberRepository;
import com.meudiario.Diary.repository.GroupRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public List<GroupResponse> getGroupsForUser(int userId) {
        return groupMemberRepository.findByUser_Id(userId).stream()
                .map(m -> toResponse(m.getGroup()))
                .toList();
    }

    public GroupResponse getGroup(Long groupId, int userId) {
        assertMember(groupId, userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado com o id: " + groupId));
        return toResponse(group);
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + request.getUserId()));

        Group group = new Group();
        group.setName(request.getName());
        group.setOwner(user);
        group.setInviteCode(generateUniqueInviteCode());
        groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        groupMemberRepository.save(member);

        return toResponse(group);
    }

    public GroupResponse joinGroup(JoinGroupRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + request.getUserId()));
        Group group = groupRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado com o código de convite: " + request.getInviteCode()));

        if (!groupMemberRepository.existsByGroup_IdAndUser_Id(group.getId(), request.getUserId())) {
            GroupMember member = new GroupMember();
            member.setGroup(group);
            member.setUser(user);
            groupMemberRepository.save(member);
        }
        return toResponse(group);
    }

    public List<GroupMemberResponse> getMembers(Long groupId, int userId) {
        assertMember(groupId, userId);
        return groupMemberRepository.findMembersByGroupId(groupId);
    }

    private void assertMember(Long groupId, int userId) {
        if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
            throw new RuntimeException("Usuário não é membro do grupo com o id: " + groupId);
        }
    }

    private GroupResponse toResponse(Group group) {
        long count = groupMemberRepository.countByGroup_Id(group.getId());
        return new GroupResponse(group.getId(), group.getName(), group.getInviteCode(),
                group.getCreatedAt(), (int) count);
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8);
        } while (groupRepository.existsByInviteCode(code));
        return code;
    }
}
