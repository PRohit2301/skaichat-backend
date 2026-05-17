package com.skaichatbackend.message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    // fetch all messages in a conversation — sorted by time
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    // find unread messages in a conversation for a specific receiver
    List<Message> findByConversationIdAndReceiverIdAndIsReadFalse(
            Long conversationId, Long receiverId
    );

    // mark all messages as delivered in a conversation
    @Query("{'conversationId': ?0, 'receiverId': ?1, 'isDelivered': false}")
    List<Message> findUndeliveredMessages(Long conversationId, Long receiverId);

    // delete all messages involving a user — used in hard delete
    void deleteBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
