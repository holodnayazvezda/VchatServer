package com.example.vchatserver.group;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupDto {
    @Getter
    @Setter
    private String name;

    @Getter @Setter
    private long unreadMsgCount;

    @Getter @Setter
    private int typeOfImage;

    @Getter @Setter
    private List<Long> messagesIds = new ArrayList<>();

    @Getter @Setter
    private List<Long> membersIds = new ArrayList<>();

    @Getter @Setter
    private String imageData;
}
