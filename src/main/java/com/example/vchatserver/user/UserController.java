package com.example.vchatserver.user;

import com.example.vchatserver.auth.Auth;
import com.example.vchatserver.group.Group;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PutMapping(value = "/v1.0/user/change_name", name = "Change user's name")
    @SecurityRequirement(name = "basicAuth")
    public void changeName(Authentication authentication, String newName) {
        userService.changeName(Auth.getUser(authentication).getId(), newName);
    }

    @PutMapping(value = "/v1.0/user/change_nickname", name = "Change user's nickname")
    @SecurityRequirement(name = "basicAuth")
    public void changeNickname(Authentication authentication, String newNickname) {
        userService.changeNickname(Auth.getUser(authentication).getId(), newNickname);
    }

    @PutMapping(value = "/v1.0/user/change_password", name = "Change user's password")
    @SecurityRequirement(name = "basicAuth")
    public void changePassword(Authentication authentication, String password) {
        userService.changePassword(Auth.getUser(authentication).getId(), password);
    }

    @PutMapping(value = "/v1.0/user/change_password_by_secret_keys", name = "Change password by secret keys")
    public void changePasswordBySecretKeys(String userNickname,
                                           int a, String a_value,
                                           int b, String b_value,
                                           int c, String c_value,
                                           String newPassword) {
        userService.changePassword(userNickname,
                a, a_value,
                b, b_value,
                c, c_value,
                newPassword);
    }

    @PutMapping(value = "/v1.0/user/change_secret_keys", name = "Change user's secret keys")
    @SecurityRequirement(name = "basicAuth")
    public void changeSecretKeys(Authentication authentication, @RequestParam List<String> secretKeys) {
        userService.changeSecretKeys(Auth.getUser(authentication).getId(),
                secretKeys);
    }

    @PutMapping(value = "/v1.0/user/change_image", name = "Change user's image")
    @SecurityRequirement(name = "basicAuth")
    public void changeImage(Authentication authentication, String newImageData) {
        userService.changeImage(Auth.getUser(authentication).getId(),
                newImageData);
    }

    @PutMapping(value = "/v1.0/user/set_type_of_image", name = "Set type of user's image")
    @SecurityRequirement(name = "basicAuth")
    public void setTypeOfImage(Authentication authentication, int newTypeOfImage) {
        userService.changeTypeOfImage(Auth.getUser(authentication).getId(), newTypeOfImage);
    }

    @PutMapping(value = "/v1.0/user/add_chat", name = "Add chat with transferred id to list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> addChat(Authentication authentication, Long newChatId) {
        return ResponseEntity.ok(
                userService.addChat(Auth.getUser(authentication).getId(), newChatId)
        );
    }

    @PutMapping(value = "/v1.0/user/remove_chat", name = "Remove chat with transferred id from list of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> removeChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.removeChat(Auth.getUser(authentication).getId(), chatId)
        );
    }

    @PostMapping(value = "/v1.0/user/create", name = "Create a new user")
    public ResponseEntity<User> create(@RequestBody CreateUserDto dto) {
        return ResponseEntity.ok(
                userService.create(dto)
        );
    }

    @GetMapping(value = "/v1.0/user/get", name = "Get user")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<User> get(Authentication authentication) {
        return ResponseEntity.ok(
                userService.get(Auth.getUser(authentication).getNickname())
        );
    }

    @GetMapping(value = "/v1.0/user/get_base_info", name = "Get base info of the user with transferred id")
    public ResponseEntity<User> getBaseInfo(Long userId) {
        return ResponseEntity.ok(
                userService.getBaseInfo(userId)
        );
    }

    @GetMapping(value = "/v1.0/user/exists", name = "Check if user with transferred id exists")
    public ResponseEntity<Boolean> exists(String userNickname) {
        return ResponseEntity.ok(
                userService.exists(userNickname)
        );
    }

    @GetMapping(value = "/v1.0/user/get_amount_chats", name = "Get amount of user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Integer> getAmountOfChats(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getAmountOfChats(Auth.getUser(authentication).getId())
        );
    }

    @GetMapping(value = "/v1.0/user/get_chats", name = "Get user's chats")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> getChats(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getChats(Auth.getUser(authentication).getId())
        );
    }

    @GetMapping(value = "/1.0/user/get_chats_with_offset", name = "Get user;'s chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> getChatsWithOffset(Authentication authentication, int limit, int offset) {
        return ResponseEntity.ok(
                userService.getChatsWithOffset(Auth.getUser(authentication).getId(), limit, offset)
        );
    }

    @GetMapping(value = "/v1.0/user/can_write", name = "Check if user can write to chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canWrite(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canWrite(Auth.getUser(authentication).getId(), chatId)
        );
    }

    @GetMapping(value = "/v1.0/user/can_edit_chat", name = "Check if user can edit chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canEditChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canEditChat(Auth.getUser(authentication).getId(), chatId)
        );
    }

    @GetMapping(value = "/v1.0/user/can_delete_message", name = "Check if user can delete message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteMessage(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                userService.canDeleteMessage(Auth.getUser(authentication).getId(), messageId)
        );
    }

    @GetMapping(value = "/v1.0/user/can_delete_chat", name = "Check if user can delete chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Boolean> canDeleteChat(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                userService.canDeleteChat(Auth.getUser(authentication), chatId)
        );
    }

    @GetMapping(value = "/v1.0/user/check_password", name = "Check user's password")
    public ResponseEntity<Boolean> checkPassword(String userNickname, String verifiablePassword) {
        return ResponseEntity.ok(
                userService.checkPassword(userNickname, verifiablePassword)
        );
    }

    @GetMapping(value = "/v1.0/user/check_secret_keys", name = "Check user's secret keys")
    public ResponseEntity<Boolean> checkSecretKeys(String userNickname,
                                                   int a, String a_value,
                                                   int b, String b_value,
                                                   int c, String c_value) {
        return ResponseEntity.ok(
                userService.checkSecretKeys(userNickname,
                        a, a_value,
                        b, b_value,
                        c, c_value)
        );
    }

    @DeleteMapping(value = "/v1.0/user/delete", name = "delete user")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication) {
        userService.delete(Auth.getUser(authentication).getId());
    }
}