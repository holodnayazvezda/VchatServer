package com.example.vchatserver.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CreateUserDto {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String nickname;

    @Getter
    @Setter
    private String password;

    @Getter @Setter
    private List<String> secretWords;

    @Getter @Setter
    private String imageData;

    @Getter @Setter
    private int typeOfImage;
}
