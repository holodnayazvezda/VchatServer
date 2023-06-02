package com.example.vchatserver.channel;

import com.example.vchatserver.group.CreateGroupDto;
import lombok.Getter;
import lombok.Setter;

public class CreateChannelDto extends CreateGroupDto {
    @Getter
    @Setter
    private String nickname;
}
