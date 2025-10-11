package com.flowershop.controller.api;

import com.flowershop.dto.AddToCartRequest;
import com.flowershop.dto.ApiResponse;
import com.flowershop.dto.CartDto;
import com.flowershop.service.CartService;
import com.flowershop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("User not authenticated!");
            throw new RuntimeException("Пользователь не аутентифицирован");
        }
        String username = authentication.getName();
        System.out.println("Username from authentication: " + username);

        try {
            var user = userService.findByUsername(username);
            System.out.println("Found user: " + user.getId() + " - " + user.getUsername());
            return user.getId();
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
            throw new RuntimeException("Ошибка при получении пользователя: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            CartDto cart = cartService.getCartForUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Корзина получена", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при получении корзины: " + e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            System.out.println("=== CART ADD REQUEST ===");
            System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
            System.out.println("Request: bouquetId=" + request.getBouquetId() + ", quantity=" + request.getQuantity());

            Long userId = getCurrentUserId(authentication);
            System.out.println("User ID: " + userId);

            CartDto cart = cartService.addToCart(userId, request);

            System.out.println("Item added successfully");
            return ResponseEntity.ok(ApiResponse.success("Товар добавлен в корзину", cart));
        } catch (Exception e) {
            System.out.println("Error adding to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при добавлении в корзину: " + e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartItemCount(Authentication authentication) {
        try {
            System.out.println("=== CART COUNT REQUEST ===");
            System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));

            Long userId = getCurrentUserId(authentication);
            System.out.println("User ID: " + userId);

            Integer count = cartService.getCartItemCount(userId);
            System.out.println("Cart count: " + count);

            return ResponseEntity.ok(ApiResponse.success("Количество товаров в корзине", count));
        } catch (Exception e) {
            System.out.println("Error getting cart count: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при получении количества: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{bouquetId}")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
            Authentication authentication,
            @PathVariable Long bouquetId,
            @RequestParam Integer quantity) {
        try {
            Long userId = getCurrentUserId(authentication);
            CartDto cart = cartService.updateCartItem(userId, bouquetId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Корзина обновлена", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при обновлении корзины: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{bouquetId}")
    public ResponseEntity<ApiResponse<CartDto>> removeFromCart(
            Authentication authentication,
            @PathVariable Long bouquetId) {
        try {
            Long userId = getCurrentUserId(authentication);
            CartDto cart = cartService.removeFromCart(userId, bouquetId);
            return ResponseEntity.ok(ApiResponse.success("Товар удален из корзины", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при удалении из корзины: " + e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Корзина очищена"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при очистке корзины: " + e.getMessage()));
        }
    }


}