package com.flowershop.controller;

import com.flowershop.service.CartService;
import com.flowershop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartViewController {

    private final CartService cartService;
    private final UserService userService;

    public CartViewController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Пользователь не аутентифицирован");
        }
        String username = authentication.getName();
        return userService.findByUsername(username).getId();
    }

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        try {
            Long userId = getCurrentUserId(authentication);
            var cartDto = cartService.getCartForUser(userId);
            model.addAttribute("cart", cartDto);
            return "cart/view";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке корзины: " + e.getMessage());
            return "cart/view";
        }
    }
}