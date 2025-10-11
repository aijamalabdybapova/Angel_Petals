package com.flowershop.repository;

import com.flowershop.entity.Bouquet;
import com.flowershop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BouquetRepository extends JpaRepository<Bouquet, Long> {

    List<Bouquet> findByInStockTrue();
    List<Bouquet> findByCategory(Category category);
    List<Bouquet> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT b FROM Bouquet b WHERE b.deleted = false")
    List<Bouquet> findAllActive();

    @Query("SELECT b FROM Bouquet b WHERE b.deleted = true")
    List<Bouquet> findAllDeleted();

    @Query("SELECT b FROM Bouquet b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Bouquet> searchBouquets(@Param("search") String search);

    @Query("SELECT b FROM Bouquet b WHERE b.inStock = true AND b.deleted = false")
    Page<Bouquet> findActiveBouquets(Pageable pageable);

    @Query("SELECT b FROM Bouquet b WHERE b.category.id = :categoryId AND b.inStock = true AND b.deleted = false")
    Page<Bouquet> findByCategoryIdAndInStock(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Bouquet b WHERE b.price BETWEEN :minPrice AND :maxPrice AND b.inStock = true AND b.deleted = false")
    Page<Bouquet> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    // ИСПРАВЛЕННЫЕ МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ
    @Query("SELECT b FROM Bouquet b WHERE b.category.id = :categoryId AND b.inStock = true AND b.deleted = false")
    Page<Bouquet> findByCategoryIdAndInStockTrueAndDeletedFalse(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Bouquet b WHERE (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.inStock = true AND b.deleted = false")
    Page<Bouquet> findByNameContainingIgnoreCaseAndInStockTrueAndDeletedFalse(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Bouquet b WHERE b.category.id = :categoryId AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.inStock = true AND b.deleted = false")
    Page<Bouquet> findByCategoryIdAndNameContainingIgnoreCaseAndInStockTrueAndDeletedFalse(
            @Param("categoryId") Long categoryId, @Param("search") String search, Pageable pageable);

    // Методы для админки
    Page<Bouquet> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);
    Page<Bouquet> findByDeletedFalse(Pageable pageable);
    Page<Bouquet> findByDeletedTrue(Pageable pageable);
    Page<Bouquet> findByInStockFalse(Pageable pageable);
}