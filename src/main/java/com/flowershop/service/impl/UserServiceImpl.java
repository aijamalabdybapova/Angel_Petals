package com.flowershop.service.impl;

import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.entity.User;
import com.flowershop.entity.UserRole;
import com.flowershop.exception.ResourceNotFoundException;
import com.flowershop.repository.UserRepository;
import com.flowershop.repository.UserRoleRepository;
import com.flowershop.service.AuditService;
import com.flowershop.service.EncryptionService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    private final EncryptionService encryptionService;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.encryptionService = encryptionService;
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

        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        String newData = "{\"username\": \"" + savedUser.getUsername() +
                "\", \"email\": \"" + savedUser.getEmail() +
                "\", \"role\": \"ROLE_USER\"}";

        auditService.logAction("users", savedUser.getId(), "CREATE", null, newData, "system");

        return savedUser;
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

        // Улучшенное логирование с JSON
        String userInfo = "{\"username\": \"" + user.getUsername() +
                "\", \"firstName\": \"" + user.getFirstName() +
                "\", \"lastName\": \"" + user.getLastName() + "\"}";

        auditService.logAction("users", user.getId(), "DELETE", userInfo, null, "admin");

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Добавьте логирование восстановления с JSON
        String oldData = "{\"status\": \"deleted\"}";
        String newData = "{\"status\": \"active\"}";

        auditService.logAction("users", user.getId(), "UPDATE", oldData, newData, "admin");

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
    @Transactional
    public void changeUserRole(Long userId, String roleName) {
        try {
            System.out.println("=== CHANGING USER ROLE IN SERVICE ===");
            System.out.println("User ID: " + userId);
            System.out.println("New role: " + roleName);

            // Находим пользователя
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));

            System.out.println("Found user: " + user.getUsername());

            // Сохраняем старые роли для аудита
            String oldRoles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.joining(", "));

            // Преобразуем строку в enum
            UserRole.RoleName roleEnum;
            try {
                roleEnum = UserRole.RoleName.valueOf(roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Неверное название роли: " + roleName +
                        ". Допустимые значения: ROLE_USER, ROLE_ADMIN");
            }

            // Находим роль в базе данных
            UserRole newRole = userRoleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Роль не найдена в базе данных: " + roleName));

            System.out.println("Found role: " + newRole.getName());

            // Создаем изменяемый HashSet
            Set<UserRole> newRoles = new HashSet<>();
            newRoles.add(newRole);

            // Устанавливаем новую роль
            user.setRoles(newRoles);

            // Сохраняем пользователя
            User savedUser = userRepository.save(user);

            // ДОБАВЛЕНО: Логируем изменение роли с JSON данными
            String oldDataJson = "{\"roles\": \"" + oldRoles + "\"}";
            String newDataJson = "{\"roles\": \"" + roleName + "\"}";

            auditService.logAction("users", user.getId(), "UPDATE",
                    oldDataJson, newDataJson, "admin");

            System.out.println("Role changed successfully for user: " + savedUser.getUsername());

        } catch (Exception e) {
            System.out.println("Error changing user role: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
    @Override
    public Page<User> findBySearch(String search, Pageable pageable) {
        return userRepository.findBySearch(search, pageable);
    }

    @Override
    public Page<User> findByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public User saveWithEncryption(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(encryptionService.encrypt(registrationDto.getEmail()));
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(encryptionService.encrypt(registrationDto.getPhoneNumber()));
        user.setAddress(registrationDto.getAddress());

        // Остальная логика как в save()
        UserRole userRole = userRoleRepository.findByName(UserRole.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public User updateUserWithEncryption(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(encryptionService.encrypt(user.getPhoneNumber()));
        existingUser.setAddress(user.getAddress());
        existingUser.setEmail(encryptionService.encrypt(user.getEmail()));

        return userRepository.save(existingUser);
    }

    @Override
    public String getMaskedPhone(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        String decryptedPhone = encryptionService.decrypt(user.getPhoneNumber());
        return encryptionService.maskPhone(decryptedPhone);
    }

    @Override
    public String getMaskedEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        String decryptedEmail = encryptionService.decrypt(user.getEmail());
        return encryptionService.maskEmail(decryptedEmail);
    }

}