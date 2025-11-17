    package com.flowershop.service.impl;

    import com.flowershop.dto.AddToCartRequest;
    import com.flowershop.dto.CartDto;
    import com.flowershop.dto.CartItemDto;
    import com.flowershop.entity.*;
    import com.flowershop.repository.*;
    import com.flowershop.service.CartService;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.stream.Collectors;

    @Service
    @Transactional
    public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final BouquetRepository bouquetRepository;

        public CartServiceImpl(CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               UserRepository userRepository,
                               BouquetRepository bouquetRepository) {
            this.cartRepository = cartRepository;
            this.cartItemRepository = cartItemRepository;
            this.userRepository = userRepository;
            this.bouquetRepository = bouquetRepository;
        }

        @Override
        public Cart getOrCreateCartForUser(Long userId) {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
                        Cart cart = new Cart(user);
                        return cartRepository.save(cart);
                    });
        }

        @Override
        @Transactional(readOnly = true)
        public CartDto getCartForUser(Long userId) {
            System.out.println("=== GET CART FOR USER: " + userId + " ===");

            Cart cart = cartRepository.findByUserIdWithItems(userId)
                    .orElseGet(() -> getOrCreateCartForUser(userId));

            System.out.println("Cart found: " + cart.getId());
            System.out.println("Items count: " + (cart.getItems() != null ? cart.getItems().size() : "null"));

            CartDto dto = convertToDto(cart);
            System.out.println("DTO totalItems: " + dto.getTotalItems());

            return dto;
        }

        @Override
        public CartDto addToCart(Long userId, AddToCartRequest request) {
            Cart cart = getOrCreateCartForUser(userId);
            Bouquet bouquet = bouquetRepository.findById(request.getBouquetId())
                    .orElseThrow(() -> new RuntimeException("Букет не найден"));

            if (!bouquet.getInStock()) {
                throw new RuntimeException("Букет отсутствует в наличии");
            }

            if (request.getQuantity() <= 0) {
                throw new RuntimeException("Количество должно быть больше 0");
            }

            // Проверяем, есть ли уже такой букет в корзине
            CartItem existingItem = cartItemRepository.findByCartIdAndBouquetId(cart.getId(), bouquet.getId())
                    .orElse(null);

            if (existingItem != null) {
                // Обновляем количество
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            } else {
                // Создаем новый элемент
                CartItem newItem = new CartItem(cart, bouquet, request.getQuantity());
                cart.addItem(newItem);
            }

            cart.calculateTotal();
            cartRepository.save(cart);

            return convertToDto(cart);
        }

        @Override
        public CartDto updateCartItem(Long userId, Long bouquetId, Integer quantity) {
            if (quantity <= 0) {
                return removeFromCart(userId, bouquetId);
            }

            Cart cart = getOrCreateCartForUser(userId);
            CartItem item = cartItemRepository.findByCartIdAndBouquetId(cart.getId(), bouquetId)
                    .orElseThrow(() -> new RuntimeException("Элемент корзины не найден"));

            item.setQuantity(quantity);
            cart.calculateTotal();
            cartRepository.save(cart);

            return convertToDto(cart);
        }

        @Override
        public CartDto removeFromCart(Long userId, Long bouquetId) {
            Cart cart = getOrCreateCartForUser(userId);
            cart.removeItem(bouquetId);
            cartRepository.save(cart);

            cartItemRepository.deleteByCartIdAndBouquetId(cart.getId(), bouquetId);

            return convertToDto(cart);
        }

        @Override
        public void clearCart(Long userId) {
            Cart cart = getOrCreateCartForUser(userId);
            cart.clear();
            cartRepository.save(cart);
            cartItemRepository.deleteByCartId(cart.getId());
        }

        @Override
        @Transactional(readOnly = true)
        public Integer getCartItemCount(Long userId) {
            try {
                if (userId == null) {
                    return 0; // Для неавторизованных пользователей
                }
                Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
                if (cart == null || cart.getItems() == null) {
                    return 0;
                }
                return cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();
            } catch (Exception e) {
                return 0;
            }
        }



        private CartDto convertToDto(Cart cart) {
            CartDto dto = new CartDto();
            dto.setId(cart.getId());
            dto.setUserId(cart.getUser().getId());
            dto.setTotalAmount(cart.getTotalAmount());

            // Конвертируем элементы корзины
            dto.setItems(cart.getItems().stream()
                    .map(this::convertItemToDto)
                    .collect(Collectors.toList()));

            // Явно устанавливаем totalItems
            dto.setTotalItems(dto.getTotalItems());

            return dto;
        }

        private CartItemDto convertItemToDto(CartItem item) {
            CartItemDto dto = new CartItemDto();
            dto.setId(item.getId());
            dto.setBouquetId(item.getBouquet().getId());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            dto.setSubtotal(item.getSubtotal());

            // Дополнительная информация о букете
            Bouquet bouquet = item.getBouquet();
            dto.setBouquetName(bouquet.getName());
            dto.setBouquetImage(bouquet.getImageUrl());

            return dto;
        }
    }