package com.skaichatbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // find user by phone number — used for login
    Optional<User> findByPhoneNumber(String phoneNumber);

    // check if phone number already exists — used for signup
    boolean existsByPhoneNumber(String phoneNumber);

    // search users by name or phone — used for contacts search
    List<User> findByUsernameContainingIgnoreCaseOrPhoneNumberContaining(
            String username, String phoneNumber);

}
