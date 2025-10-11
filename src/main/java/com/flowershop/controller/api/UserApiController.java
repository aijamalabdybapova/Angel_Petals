package com.flowershop.controller.api;

import com.flowershop.dto.ApiResponse;
import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.entity.User;
import com.flowershop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Пользователи получены успешно", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Пользователь найден", user));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = userService.save(registrationDto);
        return ResponseEntity.ok(ApiResponse.success("Пользователь зарегистрирован успешно", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success("Пользователь обновлен успешно", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("Пользователь удален"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при удалении пользователя: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        try {
            userService.restoreUser(id);
            return ResponseEntity.ok(ApiResponse.success("Пользователь восстановлен"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при восстановлении пользователя: " + e.getMessage()));
        }
    }
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<User>>> getActiveUsers() {
        List<User> users = userService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success("Активные пользователи", users));
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<User>>> getDeletedUsers() {
        List<User> users = userService.findAllDeleted();
        return ResponseEntity.ok(ApiResponse.success("Удаленные пользователи", users));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success("Результаты поиска пользователей", users));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Пользователь найден", user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Пользователь найден", user));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(@PathVariable Long id,
                                                            @RequestParam String role) {
        try {
            System.out.println("Changing role for user " + id + " to: " + role);
            userService.changeUserRole(id, role);
            return ResponseEntity.ok(ApiResponse.success("Роль пользователя изменена"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при изменении роли: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Проверка имени пользователя", exists));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Проверка email", exists));
    }
}