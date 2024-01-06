package com.example.vchatserver.message;

import lombok.Getter;
import lombok.Setter;

public class CreateMessageDto {
    @Getter
    @Setter
    private String content;

    @Getter @Setter
    private Long messageChatId;
}
