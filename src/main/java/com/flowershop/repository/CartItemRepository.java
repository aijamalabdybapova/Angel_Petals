package com.flowershop.repository;

import com.flowershop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndBouquetId(Long cartId, Long bouquetId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.bouquet.id = :bouquetId")
    void deleteByCartIdAndBouquetId(@Param("cartId") Long cartId, @Param("bouquetId") Long bouquetId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    Long countByCartId(Long cartId);
}