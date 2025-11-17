package com.flowershop.service.impl;

import com.flowershop.dto.BouquetDto;
import com.flowershop.entity.Bouquet;
import com.flowershop.entity.Category;
import com.flowershop.repository.BouquetRepository;
import com.flowershop.repository.CategoryRepository;
import com.flowershop.service.AuditService;
import com.flowershop.service.BouquetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BouquetServiceImpl implements BouquetService {

    private final BouquetRepository bouquetRepository;
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    public BouquetServiceImpl(BouquetRepository bouquetRepository,
                              CategoryRepository categoryRepository,
                              AuditService auditService) {
        this.bouquetRepository = bouquetRepository;
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
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

        Bouquet savedBouquet = bouquetRepository.save(bouquet);

        // АУДИТ: Логируем создание букета
        String newData = String.format("{\"name\": \"%s\", \"price\": %.2f, \"category\": \"%s\", \"inStock\": %s}",
                savedBouquet.getName(), savedBouquet.getPrice(),
                savedBouquet.getCategory() != null ? savedBouquet.getCategory().getName() : "null",
                savedBouquet.getInStock());

        auditService.logAction("bouquets", savedBouquet.getId(), "CREATE", null, newData, getCurrentUsername());

        return savedBouquet;
    }

    @Override
    public Bouquet update(Long id, BouquetDto bouquetDto) {
        Bouquet existingBouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));

        Category category = categoryRepository.findById(bouquetDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Сохраняем старые данные для аудита
        String oldData = String.format("{\"name\": \"%s\", \"price\": %.2f, \"category\": \"%s\", \"inStock\": %s}",
                existingBouquet.getName(), existingBouquet.getPrice(),
                existingBouquet.getCategory() != null ? existingBouquet.getCategory().getName() : "null",
                existingBouquet.getInStock());

        existingBouquet.setName(bouquetDto.getName());
        existingBouquet.setDescription(bouquetDto.getDescription());
        existingBouquet.setPrice(bouquetDto.getPrice());
        existingBouquet.setImageUrl(bouquetDto.getImageUrl());
        existingBouquet.setCategory(category);
        existingBouquet.setInStock(bouquetDto.getInStock());
        existingBouquet.setStockQuantity(bouquetDto.getStockQuantity());

        Bouquet updatedBouquet = bouquetRepository.save(existingBouquet);

        // АУДИТ: Логируем обновление букета
        String newData = String.format("{\"name\": \"%s\", \"price\": %.2f, \"category\": \"%s\", \"inStock\": %s}",
                updatedBouquet.getName(), updatedBouquet.getPrice(),
                updatedBouquet.getCategory() != null ? updatedBouquet.getCategory().getName() : "null",
                updatedBouquet.getInStock());

        auditService.logAction("bouquets", updatedBouquet.getId(), "UPDATE", oldData, newData, getCurrentUsername());

        return updatedBouquet;
    }

    @Override
    public void delete(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));

        // АУДИТ: Логируем удаление букета
        String oldData = String.format("{\"name\": \"%s\", \"price\": %.2f, \"category\": \"%s\"}",
                bouquet.getName(), bouquet.getPrice(),
                bouquet.getCategory() != null ? bouquet.getCategory().getName() : "null");

        auditService.logAction("bouquets", bouquet.getId(), "DELETE", oldData, null, getCurrentUsername());

        bouquet.setDeleted(true);
        bouquetRepository.save(bouquet);
    }


    @Override
    public void restore(Long id) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Букет не найден"));

        // АУДИТ: Логируем восстановление букета
        String oldData = "{\"status\": \"deleted\"}";
        String newData = "{\"status\": \"active\"}";

        auditService.logAction("bouquets", bouquet.getId(), "UPDATE", oldData, newData, getCurrentUsername());

        bouquet.setDeleted(false);
        bouquetRepository.save(bouquet);
    }

    @Override
    @Transactional(readOnly = true)
    public Bouquet findById(Long id) {
        return bouquetRepository.findByIdWithCategory(id)
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
    @Transactional(readOnly = true)
    public Page<Bouquet> findAll(Pageable pageable) {
        return bouquetRepository.findAllWithCategory(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bouquet> findActiveBouquets(Pageable pageable) {
        return bouquetRepository.findActiveBouquetsWithCategory(pageable);
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

        // Сохраняем старые данные для аудита
        String oldData = String.format("{\"stockQuantity\": %d, \"inStock\": %s}",
                bouquet.getStockQuantity(), bouquet.getInStock());

        bouquet.setStockQuantity(quantity);
        bouquet.setInStock(quantity > 0);
        Bouquet updatedBouquet = bouquetRepository.save(bouquet);

        // АУДИТ: Логируем изменение запасов
        String newData = String.format("{\"stockQuantity\": %d, \"inStock\": %s}",
                updatedBouquet.getStockQuantity(), updatedBouquet.getInStock());

        auditService.logAction("bouquets", updatedBouquet.getId(), "UPDATE", oldData, newData, getCurrentUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bouquet> findByCategoryId(Long categoryId, Pageable pageable) {
        return bouquetRepository.findByCategoryIdWithCategory(categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bouquet> findBySearch(String search, Pageable pageable) {
        return bouquetRepository.searchWithCategory(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bouquet> findByCategoryIdAndSearch(Long categoryId, String search, Pageable pageable) {
        return bouquetRepository.findByCategoryIdAndSearchWithCategory(categoryId, search, pageable);
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
    @Override
    @Transactional(readOnly = true)
    public Page<Bouquet> findBySearchAndCategory(String search, Long categoryId, Pageable pageable) {
        return bouquetRepository.findBySearchAndCategory(search, categoryId, pageable);
    }

    /**
     * Получает имя текущего аутентифицированного пользователя
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return "system";
    }
}