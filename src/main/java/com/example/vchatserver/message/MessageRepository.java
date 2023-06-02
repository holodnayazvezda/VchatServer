package com.example.vchatserver.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT gm.messages_ids FROM vchat_group_messages_ids gm " +
            "JOIN vchat_text_message tm ON gm.messages_ids = tm.id " +
            "WHERE gm.vchat_group_id = ?1 AND LOWER(tm.content) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "ORDER BY tm.creation_date DESC", nativeQuery = true)
    List<Long> findMessagesIds(Long groupId, String content);

    @Query(value = "SELECT tm.* FROM vchat_group_messages_ids gm " +
            "JOIN vchat_text_message tm ON gm.messages_ids = tm.id " +
            "WHERE gm.vchat_group_id = ?1 " +
            "ORDER BY tm.creation_date DESC " +
            "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Message> getMessagesWithOffset(Long groupId, int limit, int offset);
}
