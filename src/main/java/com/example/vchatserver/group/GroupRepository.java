package com.example.vchatserver.group;

import com.example.vchatserver.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query(value = "SELECT * FROM vchat_group WHERE (type = 1 AND LOWER(name)" +
            " LIKE LOWER(CONCAT('%', :chatName, '%'))) OR (type = 2 AND LOWER(name) " +
            "LIKE LOWER(CONCAT('%', :chatName, '%'))) OR (type = 2 AND LOWER(nickname) " +
            "LIKE LOWER(CONCAT('%', :chatName, '%'))) ORDER BY type, id LIMIT :limit OFFSET " +
            ":offset", nativeQuery = true)
    List<Group> searchChatsWithOffset(String chatName, int limit, int offset);
}
