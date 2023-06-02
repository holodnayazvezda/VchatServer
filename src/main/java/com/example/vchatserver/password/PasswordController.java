package com.example.vchatserver.password;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordController {

    @GetMapping(value = "/v1.0/check_password_conditions", name = "1")
    public @ResponseBody ResponseEntity<Integer> checkPasswordConditions(String password) {
        return ResponseEntity.ok(
                PasswordService.checkPasswordConditions(password)
        );
    }

    @GetMapping(value = "/v1.0/check_password_confirmation", name = "2")
    public @ResponseBody ResponseEntity<Integer> checkPasswordConfirmation(String password1, String password2) {
        return ResponseEntity.ok(
                PasswordService.checkPasswordConfirmation(password1, password2)
        );
    }

    @GetMapping(value = "/v1.0/check_password_all_conditions", name = "3")
    public @ResponseBody ResponseEntity<Integer> checkPasswordAllConditions(String password1, String password2) {
        return ResponseEntity.ok(
                PasswordService.checkPasswordAllConditions(password1, password2)
        );
    }
}
