package com.flowershop.service;

import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User save(UserRegistrationDto registrationDto);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    void restoreUser(Long id);
    User findById(Long id);
    List<User> findAll();
    List<User> findAllActive();
    List<User> findAllDeleted();
    Page<User> findAll(Pageable pageable);
    List<User> searchUsers(String search);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void changeUserRole(Long userId, String roleName);
    Page<User> findBySearch(String search, Pageable pageable);
    Page<User> findByRole(String role, Pageable pageable);

    User saveWithEncryption(UserRegistrationDto registrationDto);
    User updateUserWithEncryption(Long id, User user);
    String getMaskedPhone(Long userId);
    String getMaskedEmail(Long userId);
}