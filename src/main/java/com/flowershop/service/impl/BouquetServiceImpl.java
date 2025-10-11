package com.flowershop.service.impl;

import com.flowershop.dto.BouquetDto;
import com.flowershop.entity.Bouquet;
import com.flowershop.entity.Category;
import com.flowershop.repository.BouquetRepository;
import com.flowershop.repository.CategoryRepository;
import com.flowershop.service.BouquetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BouquetServiceImpl implements BouquetService {

    private final BouquetRepository bouquetRepository;
    private final CategoryRepository categoryRepository;

    public BouquetServiceImpl(BouquetRepository bouquetRepository,
                              CategoryRepository categoryRepository) {
        this.bouquetRepository = bouquetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Bouquet save(BouquetDto bouquetDto) {
        Category category = categoryRepository.findById(bouquetDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        Bouquet bouquet = new Bouquet();
        bouquet.setName(bouquetDto.getName());
        bouquet.setDescription(bouquetDto.getDescription());
        bouquet.setPrice(bouquetDto.getPrice());
        bouquet.setImageUrl(bouquetDto.getImageUrl());
        bouquet.setCategory(category);
        bouquet.setInStock(bouquetDto.getInStock());
        bouquet.setStockQuantity(bouquetDto.getStockQuantity());

        return bouquetRepository.save(bouquet);
    }

    @Override
    public Bouquet update(Long id, BouquetDto bouquetDto) {
        Bouquet existingBouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));

        Category category = categoryRepository.findById(bouquetDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        existingBouquet.setName(bouquetDto.getName());
        existingBouquet.setDescription(bouquetDto.getDescription());
        existingBouquet.setPrice(bouquetDto.getPrice());
        existingBouquet.setImageUrl(bouquetDto.getImageUrl());
        existingBouquet.setCategory(category);
        existingBouquet.setInStock(bouquetDto.getInStock());
        existingBouquet.setStockQuantity(bouquetDto.getStockQuantity());

        return bouquetRepository.save(existingBouquet);
    }

    @Override
    public void delete(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));
        bouquet.setDeleted(true);
        bouquetRepository.save(bouquet);
    }

    @Override
    public void restore(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));
        bouquet.setDeleted(false);
        bouquetRepository.save(bouquet);
    }

    @Override
    public Bouquet findById(Long id) {
        return bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));
    }

    @Override
    public List<Bouquet> findAll() {
        return bouquetRepository.findAll();
    }

    @Override
    public List<Bouquet> findAllActive() {
        return bouquetRepository.findAllActive();
    }

    @Override
    public List<Bouquet> findAllDeleted() {
        return bouquetRepository.findAllDeleted();
    }

    @Override
    public Page<Bouquet> findAll(Pageable pageable) {
        return bouquetRepository.findAll(pageable);
    }

    @Override
    public Page<Bouquet> findActiveBouquets(Pageable pageable) {
        return bouquetRepository.findActiveBouquets(pageable);
    }

    @Override
    public List<Bouquet> findByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        return bouquetRepository.findByCategory(category);
    }

    @Override
    public List<Bouquet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bouquetRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Bouquet> searchBouquets(String search) {
        return bouquetRepository.searchBouquets(search);
    }

    @Override
    public Page<Bouquet> findByCategoryIdAndInStock(Long categoryId, Pageable pageable) {
        return bouquetRepository.findByCategoryIdAndInStock(categoryId, pageable);
    }

    @Override
    public Page<Bouquet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return bouquetRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public void updateStock(Long bouquetId, Integer quantity) {
        Bouquet bouquet = bouquetRepository.findById(bouquetId)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));

        bouquet.setStockQuantity(quantity);
        bouquet.setInStock(quantity > 0);
        bouquetRepository.save(bouquet);
    }

    // ★★★★ РЕАЛИЗУЙТЕ ЭТИ МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ ★★★★
    @Override
    public Page<Bouquet> findByCategoryId(Long categoryId, Pageable pageable) {
        return bouquetRepository.findByCategoryIdAndInStockTrueAndDeletedFalse(categoryId, pageable);
    }

    @Override
    public Page<Bouquet> findBySearch(String search, Pageable pageable) {
        return bouquetRepository.findByNameContainingIgnoreCaseAndInStockTrueAndDeletedFalse(search, pageable);
    }

    @Override
    public Page<Bouquet> findByCategoryIdAndSearch(Long categoryId, String search, Pageable pageable) {
        return bouquetRepository.findByCategoryIdAndNameContainingIgnoreCaseAndInStockTrueAndDeletedFalse(
                categoryId, search, pageable);
    }

    @Override
    public Page<Bouquet> findBySearchIncludeDeleted(String search, Pageable pageable) {
        return bouquetRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
    }

    @Override
    public Page<Bouquet> findByDeletedFalse(Pageable pageable) {
        return bouquetRepository.findByDeletedFalse(pageable);
    }

    @Override
    public Page<Bouquet> findByDeletedTrue(Pageable pageable) {
        return bouquetRepository.findByDeletedTrue(pageable);
    }

    @Override
    public Page<Bouquet> findByInStockFalse(Pageable pageable) {
        return bouquetRepository.findByInStockFalse(pageable);
    }
}