package com.skaichatbackend.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // find request between two specific users
    Optional<FriendRequest> findBySenderIdAndReceiverId(
            Long senderId, Long receiverId
    );

    // get all incoming pending requests for a user
    List<FriendRequest> findByReceiverIdAndStatus(
            Long receiverId, String status
    );

    // get all sent pending requests by a user
    List<FriendRequest> findBySenderIdAndStatus(
            Long senderId, String status
    );

    // get all accepted contacts for a user
    @Query("SELECT f FROM FriendRequest f WHERE " +
            "(f.senderId = :userId OR f.receiverId = :userId) " +
            "AND f.status = 'accepted'")
    List<FriendRequest> findAcceptedContactsByUserId(@Param("userId") Long userId);

    // check if two users are already friends
    @Query("SELECT COUNT(f) > 0 FROM FriendRequest f WHERE " +
            "((f.senderId = :userA AND f.receiverId = :userB) OR " +
            "(f.senderId = :userB AND f.receiverId = :userA)) " +
            "AND f.status = 'accepted'")
    boolean areAlreadyFriends(@Param("userA") Long userA, @Param("userB") Long userB);

    // delete all requests involving a user — used in hard delete
    @Modifying
    @Transactional
    @Query("DELETE FROM FriendRequest f WHERE " +
            "f.senderId = :userId OR f.receiverId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
