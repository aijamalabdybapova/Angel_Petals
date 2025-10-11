package com.flowershop.controller.api;

import com.flowershop.dto.ApiResponse;
import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.entity.User;
import com.flowershop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            // Check if username exists
            if (userService.existsByUsername(registrationDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Имя пользователя уже занято"));
            }

            // Check if email exists
            if (userService.existsByEmail(registrationDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email уже используется"));
            }

            // Check password confirmation
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Пароли не совпадают"));
            }

            User user = userService.save(registrationDto);
            return ResponseEntity.ok(ApiResponse.success("Пользователь зарегистрирован успешно", user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при регистрации: " + e.getMessage()));
        }
    }
}