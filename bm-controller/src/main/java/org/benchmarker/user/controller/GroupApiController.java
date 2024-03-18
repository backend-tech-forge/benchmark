package org.benchmarker.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.user.controller.dto.GroupAddDto;
import org.benchmarker.user.controller.dto.GroupInfo;
import org.benchmarker.user.controller.dto.GroupUpdateDto;
import org.benchmarker.user.model.enums.GroupRole;
import org.benchmarker.user.service.GroupService;
import org.benchmarker.user.service.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupApiController {

    private final GroupService groupService;
    private final UserContext userContext;

    @PostMapping("/group")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<GroupInfo> createGroup(@RequestBody GroupAddDto dto) {
        String userId = userContext.getCurrentUser().getId();
        GroupInfo group = groupService.createGroup(dto, userId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/groups/{group_id}/users/{user_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<GroupInfo> addUserToGroup(
        @PathVariable(name = "group_id") String group_id,
        @PathVariable(name = "user_id") String user_id) {
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            return ResponseEntity.ok(
                groupService.addUserToGroupAdmin(group_id, user_id, GroupRole.MEMBER));
        }
        return ResponseEntity.ok(
            groupService.addUserToGroup(group_id, userContext.getCurrentUser().getId(), user_id,
                GroupRole.MEMBER));
    }

    @DeleteMapping("/groups/{group_id}/users/{user_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<GroupInfo> deleteUserFromGroup(
        @PathVariable(name = "group_id") String group_id,
        @PathVariable(name = "user_id") String user_id) {
        log.info("deleteUserFromGroup");
        return ResponseEntity.ok(
            groupService.deleteUserFromGroup(group_id,
                userContext.getCurrentUser().getId(),
                user_id,
                userContext.getCurrentUser().getRole().isAdmin()));
    }

    @DeleteMapping("/groups/{group_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<String> deleteGroup(@PathVariable(name = "group_id") String group_id) {
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            groupService.deleteGroupAdmin(group_id);
            return ResponseEntity.ok("Group deleted successfully");
        }
        groupService.deleteGroup(group_id, userContext.getCurrentUser().getId());
        return ResponseEntity.ok("Group deleted successfully");
    }

    @GetMapping("/groups/{group_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<GroupInfo> getGroup(@PathVariable(name = "group_id") String group_id) {
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            return ResponseEntity.ok(groupService.getGroupInfoAdmin(group_id));
        }
        return ResponseEntity.ok(
            groupService.getGroupInfo(group_id, userContext.getCurrentUser().getId())
        );
    }

    @PatchMapping("/groups/{group_id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<GroupInfo> updateGroup(@PathVariable(name = "group_id") String group_id,
        @RequestBody GroupUpdateDto dto) {
        if (userContext.getCurrentUser().getRole().isAdmin()) {
            return ResponseEntity.ok(groupService.updateGroupAdmin(dto, group_id));
        } else {
            return ResponseEntity.ok(
                groupService.updateGroupUser(dto, group_id, userContext.getCurrentUser().getId()));
        }
    }

}
