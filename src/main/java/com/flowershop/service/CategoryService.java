package com.flowershop.service;

import com.flowershop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Category save(Category category);
    Category update(Long id, Category category);
    void delete(Long id);
    void restore(Long id);
    Category findById(Long id);
    Category findByName(String name);
    List<Category> findAll();
    List<Category> findAllActive();
    List<Category> findAllDeleted();
    Page<Category> findAll(Pageable pageable);
    List<Category> searchCategories(String search);
    boolean existsByName(String name);
}