package com.example.vchatserver.name;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NameController {

    @GetMapping(value = "/v1.0/check_name", name = "Check name; returns special code")
    public @ResponseBody ResponseEntity<Integer> checkName(String name) {
        return ResponseEntity.ok(
                NameService.checkName(name)
        );
    }

}
