package com.meudiario.Diary.controller;

import com.meudiario.Diary.dto.CreateGroupRequest;
import com.meudiario.Diary.dto.GroupMemberResponse;
import com.meudiario.Diary.dto.GroupResponse;
import com.meudiario.Diary.dto.JoinGroupRequest;
import com.meudiario.Diary.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponse>> getGroups(@PathVariable int userId) {
        return ResponseEntity.ok(groupService.getGroupsForUser(userId));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId, @RequestParam int userId) {
        return ResponseEntity.ok(groupService.getGroup(groupId, userId));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request) {
        return new ResponseEntity<>(groupService.createGroup(request), HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<GroupResponse> joinGroup(@RequestBody JoinGroupRequest request) {
        return ResponseEntity.ok(groupService.joinGroup(request));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getMembers(@PathVariable Long groupId, @RequestParam int userId) {
        return ResponseEntity.ok(groupService.getMembers(groupId, userId));
    }
}
