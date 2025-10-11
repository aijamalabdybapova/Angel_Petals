package com.flowershop.service;

import com.flowershop.dto.BouquetDto;
import com.flowershop.entity.Bouquet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BouquetService {

    Bouquet save(BouquetDto bouquetDto);
    Bouquet update(Long id, BouquetDto bouquetDto);
    void delete(Long id);
    void restore(Long id);
    Bouquet findById(Long id);
    List<Bouquet> findAll();
    List<Bouquet> findAllActive();
    List<Bouquet> findAllDeleted();
    Page<Bouquet> findAll(Pageable pageable);
    Page<Bouquet> findActiveBouquets(Pageable pageable);
    List<Bouquet> findByCategory(Long categoryId);
    List<Bouquet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Bouquet> searchBouquets(String search);
    Page<Bouquet> findByCategoryIdAndInStock(Long categoryId, Pageable pageable);
    Page<Bouquet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    void updateStock(Long bouquetId, Integer quantity);

    // ★★★★ ДОБАВЬТЕ ЭТИ МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ ★★★★
    Page<Bouquet> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Bouquet> findBySearch(String search, Pageable pageable);
    Page<Bouquet> findByCategoryIdAndSearch(Long categoryId, String search, Pageable pageable);
    Page<Bouquet> findBySearchIncludeDeleted(String search, Pageable pageable);
    Page<Bouquet> findByDeletedFalse(Pageable pageable);
    Page<Bouquet> findByDeletedTrue(Pageable pageable);
    Page<Bouquet> findByInStockFalse(Pageable pageable);
}