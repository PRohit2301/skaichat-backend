package com.skaichatbackend.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // get all conversations for a user — sorted by last message time
    @Query("SELECT c FROM Conversation c WHERE " +
            "c.participantOneId = :userId OR c.participantTwoId = :userId " +
            "ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findAllByUserId(@Param("userId") Long userId);

    // find conversation between two specific users
    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.participantOneId = :userA AND c.participantTwoId = :userB) OR " +
            "(c.participantOneId = :userB AND c.participantTwoId = :userA)")
    Optional<Conversation> findByParticipants(
            @Param("userA") Long userA,
            @Param("userB") Long userB
    );

    // delete all conversations involving a user — used in hard delete
    @Modifying
    @Transactional
    @Query("DELETE FROM Conversation c WHERE " +
            "c.participantOneId = :userId OR c.participantTwoId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
