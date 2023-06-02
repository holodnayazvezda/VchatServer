package com.example.vchatserver.user;

import com.example.vchatserver.group.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

;

@Entity(name = "vchat_user")
public class User {
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @NonNull
    private String name;

    @Getter @Setter
    @Column(unique = true)
    @NonNull
    private String nickname;

    @Getter @Setter
    @NonNull
    private String password;

    @Getter @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @NonNull
    private List<Long> chatsIds = new ArrayList<>();

    @ElementCollection
    @Getter @Setter
    @NonNull
    private List<String> secretKeys = new ArrayList<>();

    @Getter @Setter
    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAEElEQVR4nGJ6VrQAEAAA//8EQgH7dTCZ8gAAAABJRU5ErkJggg==",
            description = "Base64-encoded avatar image thumbnail", required = true)
    @Column(name = "IMAGE_DATA", columnDefinition = "LONGTEXT", length = 100000)
    @Lob
    private String imageData;

    @Getter @Setter
    @NonNull
    private int typeOfImage;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User &&
                Objects.equals(((User) obj).getId(), this.getId());
    }
}
