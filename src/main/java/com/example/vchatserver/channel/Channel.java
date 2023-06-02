package com.example.vchatserver.channel;

import com.example.vchatserver.group.Group;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity(name = "vchat_channel")
public class Channel extends Group {
    @Getter @Setter
    @NonNull
    @Column(unique = true)
    private String nickname;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Channel &&
                Objects.equals(((Channel) obj).getId(), this.getId());
    }
}
