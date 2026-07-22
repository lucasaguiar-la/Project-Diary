package com.meudiario.Diary.service;

import com.meudiario.Diary.dto.SharedListHistoryResponse;
import com.meudiario.Diary.dto.SharedListItemRequest;
import com.meudiario.Diary.dto.SharedListItemResponse;
import com.meudiario.Diary.dto.SharedListRequest;
import com.meudiario.Diary.dto.SharedListResponse;
import com.meudiario.Diary.model.Group;
import com.meudiario.Diary.model.SharedList;
import com.meudiario.Diary.model.SharedListItem;
import com.meudiario.Diary.model.SharedListItemCompletion;
import com.meudiario.Diary.model.User;
import com.meudiario.Diary.repository.GroupMemberRepository;
import com.meudiario.Diary.repository.GroupRepository;
import com.meudiario.Diary.repository.SharedListItemCompletionRepository;
import com.meudiario.Diary.repository.SharedListItemRepository;
import com.meudiario.Diary.repository.SharedListRepository;
import com.meudiario.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SharedListService {

    @Autowired
    private SharedListRepository sharedListRepository;

    @Autowired
    private SharedListItemRepository itemRepository;

    @Autowired
    private SharedListItemCompletionRepository completionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public List<SharedListResponse> getLists(Long groupId, int userId) {
        assertMember(groupId, userId);
        return sharedListRepository.findByGroup_Id(groupId).stream()
                .map(l -> new SharedListResponse(l.getId(), l.getName(), l.getCreatedAt()))
                .toList();
    }

    public SharedListResponse createList(SharedListRequest request) {
        assertMember(request.getGroupId(), request.getUserId());
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado com o id: " + request.getGroupId()));
        SharedList list = new SharedList();
        list.setName(request.getName());
        list.setGroup(group);
        sharedListRepository.save(list);
        return new SharedListResponse(list.getId(), list.getName(), list.getCreatedAt());
    }

    public List<SharedListItemResponse> getItemsWithStatus(Long listId, int userId) {
        SharedList list = getListOrThrow(listId);
        assertMember(list.getGroup().getId(), userId);
        LocalDate today = LocalDate.now();
        return itemRepository.findBySharedList_Id(listId).stream()
                .map(i -> new SharedListItemResponse(
                        i.getId(),
                        i.getTitle(),
                        i.getCreatedAt(),
                        completionRepository.existsByItem_IdAndCompletedDate(i.getId(), today)))
                .toList();
    }

    public SharedListItemResponse createItem(SharedListItemRequest request) {
        SharedList list = getListOrThrow(request.getListId());
        assertMember(list.getGroup().getId(), request.getUserId());
        SharedListItem item = new SharedListItem();
        item.setTitle(request.getTitle());
        item.setSharedList(list);
        itemRepository.save(item);
        return new SharedListItemResponse(item.getId(), item.getTitle(), item.getCreatedAt(), false);
    }

    public void deleteItem(Long itemId, int userId) {
        SharedListItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com o id: " + itemId));
        assertMember(item.getSharedList().getGroup().getId(), userId);
        completionRepository.deleteAll(completionRepository.findByItem_Id(itemId));
        itemRepository.delete(item);
    }

    public void completeItem(Long itemId, int userId) {
        SharedListItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com o id: " + itemId));
        assertMember(item.getSharedList().getGroup().getId(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + userId));
        LocalDate today = LocalDate.now();
        if (!completionRepository.existsByItem_IdAndCompletedDate(itemId, today)) {
            SharedListItemCompletion completion = new SharedListItemCompletion();
            completion.setItem(item);
            completion.setUser(user);
            completion.setCompletedDate(today);
            completionRepository.save(completion);
        }
    }

    public void uncompleteItem(Long itemId, int userId) {
        SharedListItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com o id: " + itemId));
        assertMember(item.getSharedList().getGroup().getId(), userId);
        completionRepository.findByItem_IdAndCompletedDate(itemId, LocalDate.now())
                .ifPresent(completionRepository::delete);
    }

    public List<SharedListHistoryResponse> getHistory(Long listId, int userId) {
        SharedList list = getListOrThrow(listId);
        assertMember(list.getGroup().getId(), userId);
        return completionRepository.findHistoryByListId(listId);
    }

    private SharedList getListOrThrow(Long listId) {
        return sharedListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista não encontrada com o id: " + listId));
    }

    private void assertMember(Long groupId, int userId) {
        if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
            throw new RuntimeException("Usuário não é membro do grupo com o id: " + groupId);
        }
    }
}
