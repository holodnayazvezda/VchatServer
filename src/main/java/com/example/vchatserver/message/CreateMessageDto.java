package com.example.vchatserver.message;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.OneToMany;
import java.util.List;

public class CreateMessageDto {
    @Getter
    @Setter
    private String content;

    @Getter @Setter
    private Long messageChatId;
}
