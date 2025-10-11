package com.flowershop.service.impl;

import com.flowershop.entity.Category;
import com.flowershop.repository.CategoryRepository;
import com.flowershop.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void restore(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        category.setDeleted(false);
        categoryRepository.save(category);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }

    @Override
    public List<Category> findAllDeleted() {
        return categoryRepository.findAllDeleted();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> searchCategories(String search) {
        return categoryRepository.searchCategories(search);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}