package com.flowershop.service.impl;

import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.entity.User;
import com.flowershop.entity.UserRole;
import com.flowershop.exception.ResourceNotFoundException;
import com.flowershop.repository.UserRepository;
import com.flowershop.repository.UserRoleRepository;
import com.flowershop.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setAddress(registrationDto.getAddress());

        // Set default role
        UserRole userRole = userRoleRepository.findByName(UserRole.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
        user.setRoles(Set.of(userRole));

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllActive() {
        return userRepository.findAllActive();
    }

    @Override
    public List<User> findAllDeleted() {
        return userRepository.findAllDeleted();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> searchUsers(String search) {
        return userRepository.searchUsers(search);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void changeUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserRole.RoleName roleEnum = UserRole.RoleName.valueOf(roleName);
        UserRole role = userRoleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Неверное имя пользователя или пароль"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }
}