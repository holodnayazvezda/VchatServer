package com.example.vchatserver.nickname;

import com.example.vchatserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NicknameController {

    @Autowired
    NicknameService nicknameService;

    @GetMapping(value = "/v1.0/check_nickname_for_user", name = "Check nickname for user; returns special code")
    public @ResponseBody ResponseEntity<Integer> checkNicknameForUser(String nickname) {
        return ResponseEntity.ok(
                nicknameService.checkNicknameForUser(nickname)
        );
    }

    @GetMapping(value = "/v1.0/check_nickname_for_channel", name = "Check nickname for channel; returns special code")
    public @ResponseBody ResponseEntity<Integer> checkNicknameForChannel(String nickname) {
        return ResponseEntity.ok(
                nicknameService.checkNicknameForChannel(nickname)
        );
    }

}
