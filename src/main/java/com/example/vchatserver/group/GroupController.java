package com.example.vchatserver.group;

import com.example.vchatserver.auth.Auth;
import com.example.vchatserver.message.Message;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {
    @Autowired
    GroupService groupService;

    @PutMapping(value = "/v1.0/group/add_member", name = "Add member to group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> addMember(Authentication authentication, Long groupId) {
        return ResponseEntity.ok(
                groupService.addMember(Auth.getUser(authentication).getId(), groupId)
        );
    }

    @PutMapping(value = "/v1.0/group/remove_member", name = "Remove member from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMember(Authentication authentication, Long groupId) {
        return ResponseEntity.ok(
                groupService.removeMember(Auth.getUser(authentication).getId(), groupId)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_name", name = "Edit name of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editName(Authentication authentication, Long groupId, String newName) {
        return ResponseEntity.ok(
                groupService.editName(Auth.getUser(authentication).getId(), groupId, newName)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_type_of_image", name = "Edit type of image of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editTypeOfImage(Authentication authentication, Long groupId, Integer newTypeOfImage) {
        return ResponseEntity.ok(
                groupService.editTypeOfImage(Auth.getUser(authentication).getId(), groupId, newTypeOfImage)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_image", name = "Edit image of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editImage(Authentication authentication, Long groupId, String imageData) {
        return ResponseEntity.ok(
                groupService.editImage(Auth.getUser(authentication).getId(), groupId, imageData)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editAll(Authentication authentication, Long groupId, String newName, Integer newTypeOfImage, String newImageData) {
        return ResponseEntity.ok(
                groupService.editAll(Auth.getUser(authentication).getId(), groupId, newName, newTypeOfImage, newImageData)
        );
    }

    @PutMapping(value = "/v1.0/group/add_message", name = "Add message with transferred id to group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> addMessage(Authentication authentication, Long groupId, Long messageId) {
        return ResponseEntity.ok(
                groupService.addMessage(Auth.getUser(authentication).getId(), groupId, messageId)
        );
    }

    @PutMapping(value = "/v1.0/group/remove_message", name = "Remove message with transferred id from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMessage(Authentication authentication, Long groupId, Long messageId) {
        return ResponseEntity.ok(
                groupService.removeMessage(Auth.getUser(authentication).getId(), groupId, messageId)
        );
    }

    @PostMapping(value = "/v1.0/group/create", name = "Create a new group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> create(Authentication authentication, @RequestBody CreateGroupDto dto) {
        return ResponseEntity.ok(
                groupService.createGroup(Auth.getUser(authentication).getId(), dto)
        );
    }

    @GetMapping(value = "/v1.0/chat/get_chat", name = "Get chat by id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> getChat(Long chatId) {
        return ResponseEntity.ok(
                groupService.getChat(chatId)
        );
    }

    @GetMapping(value = "/v1.0/chat/search_chats_with_offset", name = "Search chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> searchChatsWithOffset(String chatName, int limit, int offset) {
        return ResponseEntity.ok(
                groupService.searchChatsWithOffset(chatName, limit, offset)
        );
    }

    @GetMapping(value = "/v1.0/group/get_chat_fot_user", name = "Get chat for user by id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> getChatForUser(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                groupService.getChatForUser(Auth.getUser(authentication).getId(), chatId)
        );
    }

    @DeleteMapping(value = "/v1.0/group/delete", name = "Delete group by transferred id")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long groupId) {
        groupService.delete(Auth.getUser(authentication).getId(), groupId);
    }
}
