package com.flowershop.controller;

import com.flowershop.dto.UserRegistrationDto;
import com.flowershop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto registrationDto,
                                      BindingResult result) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        // Check if username exists
        if (userService.existsByUsername(registrationDto.getUsername())) {
            result.rejectValue("username", "error.user", "Имя пользователя уже занято");
            return "auth/register";
        }

        // Check if email exists
        if (userService.existsByEmail(registrationDto.getEmail())) {
            result.rejectValue("email", "error.user", "Email уже используется");
            return "auth/register";
        }

        // Check password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
            return "auth/register";
        }

        try {
            userService.save(registrationDto);
            return "redirect:/register?success";
        } catch (Exception e) {
            result.reject("error.user", "Ошибка при регистрации: " + e.getMessage());
            return "auth/register";
        }
    }
}