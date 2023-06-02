package com.example.vchatserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);


    @Query(value = "SELECT chats_ids FROM vchat_user_chats_ids uci " +
            "JOIN vchat_group g ON g.id = uci.chats_ids " +
            "LEFT JOIN (SELECT vchat_group_id, MAX(tm.creation_date) AS last_message_date " +
            "           FROM vchat_group_messages_ids gm " +
            "           JOIN vchat_text_message tm ON gm.messages_ids = tm.id " +
            "           WHERE gm.vchat_group_id IN (SELECT chats_ids FROM vchat_user_chats_ids WHERE vchat_user_id = ?1 )" +
            "           GROUP BY vchat_group_id) gmi ON gmi.vchat_group_id = g.id " +
            "WHERE uci.vchat_user_id = ?1 " +
            "GROUP BY g.id " +
            "ORDER BY " +
            "   CASE WHEN MAX(gmi.last_message_date) IS NULL THEN g.creation_date ELSE MAX(gmi.last_message_date) END " +
            "DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Long> getChatsIdsWithOffset(Long userId, int limit, int offset);

    @Query(value = "SELECT COUNT(*) FROM vchat_user_chats_ids WHERE vchat_user_id = ?1", nativeQuery = true)
    int getAmountOfChats(Long userId);

    @Query(value = "SELECT chats_ids " +
            "FROM vchat_user_chats_ids user_chats " +
            "JOIN vchat_group chats ON chats.id = user_chats.chats_ids " +
            "WHERE user_chats.VCHAT_USER_ID = :userId" +
            "AND UPPER(chats.name) LIKE UPPER(CONCAT('%', :nameOfChat, '%')) " +
            "ORDER BY chats.chats_ids DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Long> searchChatsWithOffset(Long userId, String nameOfChat, int limit, int offset);
}
