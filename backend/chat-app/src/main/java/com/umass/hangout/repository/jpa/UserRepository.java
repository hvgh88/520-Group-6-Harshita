package com.umass.hangout.repository.jpa;

import com.umass.hangout.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groupIds WHERE u.id = :userId")
    Optional<User> findByIdWithGroups(@Param("userId") Long userId);
}