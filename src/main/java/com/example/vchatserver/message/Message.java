package com.example.vchatserver.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "vchat_text_message")
public class Message {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @NonNull
    @Column(length = 20000)
    private String content;

    @Getter @Setter
    @NonNull
    private Long messageChatId;

    @Getter @Setter
    private ZonedDateTime creationDate;

    @Getter @Setter
    @NonNull
    private Long ownerId;

    @Getter @Setter
    @ElementCollection
    private List<Long> readersIds = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message &&
                Objects.equals(((Message) obj).getId(), this.getId());
    }
}
