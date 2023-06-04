package com.example.vchatserver.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "vchat_group")
public class Group {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @NonNull
    @Column(length = 30)
    private String name;

    @Getter @Setter
    private long unreadMsgCount;

    @Getter @Setter
    private int type;

    @Getter @Setter
    private int typeOfImage;

    @Getter @Setter
    @NonNull
    private Long ownerId;

    @Getter @Setter
    private ZonedDateTime creationDate;

    @Getter @Setter
    @ElementCollection
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Long> messagesIds = new ArrayList<>();

    @Getter @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Long> membersIds = new ArrayList<>();

    @Getter @Setter
    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAEElEQVR4nGJ6VrQAEAAA//8EQgH7dTCZ8gAAAABJRU5ErkJggg==",
            description = "Base64-encoded avatar image thumbnail", required = true)
    @Column(name = "IMAGE_DATA", columnDefinition = "LONGTEXT", nullable = false, length = 10000000)
    private String imageData;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Group &&
                Objects.equals(((Group) obj).getId(), this.getId());
    }
}
