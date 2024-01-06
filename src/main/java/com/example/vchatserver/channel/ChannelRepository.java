package com.example.vchatserver.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}
