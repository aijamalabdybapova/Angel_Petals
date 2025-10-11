package com.flowershop.service;

import com.flowershop.dto.AddToCartRequest;
import com.flowershop.dto.CartDto;
import com.flowershop.entity.Cart;
import org.springframework.transaction.annotation.Transactional;

public interface CartService {

    @Transactional(readOnly = true)
    Integer getCartItemCount(Long userId);

    Cart getOrCreateCartForUser(Long userId);

    @Transactional(readOnly = true)
    CartDto getCartForUser(Long userId);

    CartDto addToCart(Long userId, AddToCartRequest request);

    CartDto updateCartItem(Long userId, Long bouquetId, Integer quantity);

    CartDto removeFromCart(Long userId, Long bouquetId);

    void clearCart(Long userId);
}