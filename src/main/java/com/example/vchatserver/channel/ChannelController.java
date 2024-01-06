package com.example.vchatserver.channel;

import com.example.vchatserver.auth.Auth;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @PutMapping(value = "/v1.0/channel/add_member", name = "Add member to channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> addMember(Authentication authentication, Long channelId) {
        return ResponseEntity.ok(
                channelService.addMember(Auth.getUser(authentication).getId(), channelId)
        );
    }

    @PutMapping(value = "/v1.0/channel/remove_member", name = "Remove member from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMember(Authentication authentication, Long channelId) {
        return ResponseEntity.ok(
                channelService.removeMember(Auth.getUser(authentication).getId(), channelId)
        );
    }

    @PutMapping(value = "/v1.0/channel/edit_name", name = "Edit name of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editName(Authentication authentication, Long channelId, String newName) {
        return ResponseEntity.ok(
                channelService.editName(Auth.getUser(authentication).getId(), channelId, newName)
        );
    }

    @PutMapping(value = "/v1.0/channel/edit_nickname", name = "Edit nickname of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editNickname(Authentication authentication, Long channelId, String newName) {
        return ResponseEntity.ok(
                channelService.editNickname(Auth.getUser(authentication).getId(), channelId, newName)
        );
    }

    @PutMapping(value = "/v1.0/channel/edit_type_of_image", name = "Edit type of image of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editTypeOfImage(Authentication authentication, Long channelId, Integer newTypeOfImage) {
        return ResponseEntity.ok(
                channelService.editTypeOfImage(Auth.getUser(authentication).getId(), channelId, newTypeOfImage)
        );
    }

    @PutMapping(value = "/v1.0/channel/edit_image", name = "Edit image of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editImage(Authentication authentication, Long channelId, String imageData) {
        return ResponseEntity.ok(
                channelService.editImage(Auth.getUser(authentication).getId(), channelId, imageData)
        );
    }

    @PutMapping(value = "/v1.0/channel/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editAll(Authentication authentication, Long channelId, String newName, String newNickname, Integer newTypeOfImage, String newImageData) {
        return ResponseEntity.ok(
                channelService.editAll(Auth.getUser(authentication).getId(), channelId, newName, newNickname, newTypeOfImage, newImageData)
        );
    }

    @PutMapping(value = "/v1.0/channel/add_message", name = "Add message with transferred id to channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> addMessage(Authentication authentication, Long channelId, Long messageId) {
        return ResponseEntity.ok(
                channelService.addMessage(Auth.getUser(authentication).getId(), channelId, messageId)
        );
    }

    @PutMapping(value = "/v1.0/channel/remove_message", name = "Remove message with transferred id from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMessage(Authentication authentication, Long channelId, Long messageId) {
        return ResponseEntity.ok(
                channelService.removeMessage(Auth.getUser(authentication).getId(), channelId, messageId)
        );
    }

    @PostMapping(value = "/v1.0/channel/create", name = "Create a new channel")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> create(Authentication authentication, @RequestBody CreateChannelDto dto) {
        return ResponseEntity.ok(
                channelService.create(Auth.getUser(authentication).getId(), dto)
        );
    }

    @GetMapping(value = "/v1.0/channel/get_by_id", name = "Gte channel by transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> getById(Long channelId) {
        return ResponseEntity.ok(
                channelService.getById(channelId)
        );
    }

    @DeleteMapping(value = "/v1.0/channel/delete", name = "Delete channel by transferred id")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long channelId) {
        channelService.delete(Auth.getUser(authentication).getId(), channelId);
    }
}
