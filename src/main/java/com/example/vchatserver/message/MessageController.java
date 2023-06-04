package com.example.vchatserver.message;

import com.example.vchatserver.auth.Auth;
import com.example.vchatserver.user.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    @PutMapping(value = "/v1.0/message/add_reader", name = "Add reader of the message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> addReader(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                messageService.addReader(Auth.getUser(authentication).getId(),
                        messageId)
        );
    }

    @PostMapping(value = "/v1.0/message/create", name = "Create new message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> create(Authentication authentication, @RequestBody CreateMessageDto dto) {
        return ResponseEntity.ok(
                messageService.create(
                        Auth.getUser(authentication).getId(),
                        dto
                )
        );
    }

    @GetMapping(value = "/v1.0/message/get", name = "Get message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> get(Long messageId) {
        return ResponseEntity.ok(
                messageService.get(messageId)
        );
    }

    @GetMapping(value = "/v1.0/message/get_readers", name = "Get readers of this message")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<User>> getReaders(Authentication authentication, Long messageId) {
        return ResponseEntity.ok(
                messageService.getReaders(Auth.getUser(authentication).getId(), messageId)
        );
    }

    @GetMapping(value = "/v1.0/message/get_positions_of_found_messages", name = "Search messages in the chat with transferred id and return their positions in list")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Integer>> getPositionsOfFoundMessages(Authentication authentication, Long groupId, String content) {
        return ResponseEntity.ok(
                messageService.getPositionsOfFoundMessages(Auth.getUser(authentication), groupId, content)
        );
    }

    @GetMapping(value = "/v1.0/message/get_last_message", name = "Get last message of the chat with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Message> getLastMessage(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                messageService.getLastMessage(Auth.getUser(authentication).getId(), chatId)
        );
    }

    @GetMapping(value = "/v1.0/message/get_messages_with_offset", name = "Get all messages of the chat with transferred id with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Message>> getMessagesWithOffset(Authentication authentication, Long chatId, int limit, int offset) {
        return ResponseEntity.ok(
                messageService.getMessagesWithOffset(Auth.getUser(authentication), chatId, limit, offset)
        );
    }

    @DeleteMapping(value = "/v1.0/message/delete", name = "Delete the message with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long messageId) {
        messageService.delete(Auth.getUser(authentication).getId(), messageId);
    }
}
