package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.SharedListHistoryResponse;
import com.meudiario.Diary.dto.SharedListItemRequest;
import com.meudiario.Diary.dto.SharedListItemResponse;
import com.meudiario.Diary.dto.SharedListRequest;
import com.meudiario.Diary.dto.SharedListResponse;
import com.meudiario.Diary.service.SharedListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared-lists")
@CrossOrigin(origins = "*")
public class SharedListController {

    @Autowired
    private SharedListService sharedListService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SharedListResponse>> getLists(@PathVariable Long groupId, @RequestParam int userId) {
        return ResponseEntity.ok(sharedListService.getLists(groupId, userId));
    }

    @PostMapping
    public ResponseEntity<SharedListResponse> createList(@RequestBody SharedListRequest request) {
        return new ResponseEntity<>(sharedListService.createList(request), HttpStatus.CREATED);
    }

    @GetMapping("/{listId}/items")
    public ResponseEntity<List<SharedListItemResponse>> getItems(@PathVariable Long listId, @RequestParam int userId) {
        return ResponseEntity.ok(sharedListService.getItemsWithStatus(listId, userId));
    }

    @PostMapping("/items")
    public ResponseEntity<SharedListItemResponse> createItem(@RequestBody SharedListItemRequest request) {
        return new ResponseEntity<>(sharedListService.createItem(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId, @RequestParam int userId) {
        sharedListService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items/{itemId}/complete")
    public ResponseEntity<Void> completeItem(@PathVariable Long itemId, @RequestParam int userId) {
        sharedListService.completeItem(itemId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}/complete/today")
    public ResponseEntity<Void> uncompleteItem(@PathVariable Long itemId, @RequestParam int userId) {
        sharedListService.uncompleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{listId}/history")
    public ResponseEntity<List<SharedListHistoryResponse>> getHistory(@PathVariable Long listId, @RequestParam int userId) {
        return ResponseEntity.ok(sharedListService.getHistory(listId, userId));
    }
}
