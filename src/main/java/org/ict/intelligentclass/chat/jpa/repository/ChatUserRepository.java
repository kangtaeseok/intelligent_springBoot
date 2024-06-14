package org.ict.intelligentclass.chat.jpa.repository;

import org.ict.intelligentclass.chat.jpa.entity.ChatUserCompositeKey;
import org.ict.intelligentclass.chat.jpa.entity.ChatUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUserEntity, ChatUserCompositeKey> {

    @Query("SELECT c.chatUserCompositeKey.roomId FROM ChatUserEntity c WHERE c.chatUserCompositeKey.userId = :userId")
    List<Long> findRoomIdsByUserId(@Param("userId") String userId);
}