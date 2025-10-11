package com.flowershop.repository;

import com.flowershop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.deleted = true")
    List<User> findAllDeleted();

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> searchUsers(@Param("search") String search);
}