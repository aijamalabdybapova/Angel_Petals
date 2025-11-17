package com.flowershop.controller;

import com.flowershop.dto.BouquetDto;
import com.flowershop.entity.Bouquet;
import com.flowershop.service.BouquetService;
import com.flowershop.service.CategoryService;
import com.flowershop.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bouquets")
public class BouquetController {

    private final BouquetService bouquetService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    public BouquetController(BouquetService bouquetService,
                             CategoryService categoryService,
                             FileStorageService fileStorageService) {
        this.bouquetService = bouquetService;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
    }

    // ОСНОВНОЙ МЕТОД ФИЛЬТРАЦИИ И СОРТИРОВКИ
    @GetMapping
    public String listBouquets(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        System.out.println("Filter params - Category: " + categoryId + ", Search: " + search + ", Sort: " + sort + ", Direction: " + direction);

        // Создаем сортировку
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<Bouquet> bouquetsPage;

        // Фильтрация по категории и поиску
        if (categoryId != null && search != null && !search.trim().isEmpty()) {
            bouquetsPage = bouquetService.findByCategoryIdAndSearch(categoryId, search, pageable);
        } else if (categoryId != null) {
            bouquetsPage = bouquetService.findByCategoryId(categoryId, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            bouquetsPage = bouquetService.findBySearch(search, pageable);
        } else {
            bouquetsPage = bouquetService.findActiveBouquets(pageable);
        }

        model.addAttribute("bouquets", bouquetsPage);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        return "bouquet/list";
    }

    @GetMapping("/{id}")
    public String bouquetDetails(@PathVariable Long id, Model model) {
        Bouquet bouquet = bouquetService.findById(id);
        model.addAttribute("bouquet", bouquet);
        return "bouquet/details";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("bouquet", new BouquetDto());
        model.addAttribute("categories", categoryService.findAllActive());
        return "bouquet/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String createBouquet(@ModelAttribute("bouquet") @Valid BouquetDto bouquetDto,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllActive());
            return "bouquet/create";
        }

        try {
            // Handle image upload
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(imageFile);
                bouquetDto.setImageUrl(fileName);
            }

            bouquetService.save(bouquetDto);
            redirectAttributes.addFlashAttribute("successMessage", "Букет успешно создан");
            return "redirect:/bouquets";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("errorMessage", "Ошибка при создании букета: " + e.getMessage());
            return "bouquet/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Bouquet bouquet = bouquetService.findById(id);
        BouquetDto bouquetDto = new BouquetDto();
        bouquetDto.setId(bouquet.getId());
        bouquetDto.setName(bouquet.getName());
        bouquetDto.setDescription(bouquet.getDescription());
        bouquetDto.setPrice(bouquet.getPrice());
        bouquetDto.setImageUrl(bouquet.getImageUrl());
        bouquetDto.setCategoryId(bouquet.getCategory().getId());
        bouquetDto.setInStock(bouquet.getInStock());
        bouquetDto.setStockQuantity(bouquet.getStockQuantity());

        model.addAttribute("bouquet", bouquetDto);
        model.addAttribute("categories", categoryService.findAllActive());
        return "bouquet/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String updateBouquet(@PathVariable Long id,
                                @ModelAttribute("bouquet") @Valid BouquetDto bouquetDto,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllActive());
            return "bouquet/edit";
        }

        try {
            // Handle image upload
            if (!imageFile.isEmpty()) {
                // Delete old image if exists
                Bouquet existingBouquet = bouquetService.findById(id);
                if (existingBouquet.getImageUrl() != null) {
                    fileStorageService.deleteFile(existingBouquet.getImageUrl());
                }

                String fileName = fileStorageService.storeFile(imageFile);
                bouquetDto.setImageUrl(fileName);
            }

            bouquetService.update(id, bouquetDto);
            redirectAttributes.addFlashAttribute("successMessage", "Букет успешно обновлен");
            return "redirect:/bouquets/" + id;
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("errorMessage", "Ошибка при обновлении букета: " + e.getMessage());
            return "bouquet/edit";
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String deleteBouquet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bouquetService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Букет успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении букета: " + e.getMessage());
        }
        return "redirect:/bouquets";
    }

    @GetMapping("/manage")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String manageBouquets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Bouquet> bouquetsPage;

        // ПРАВИЛЬНАЯ ЛОГИКА ФИЛЬТРАЦИИ
        if (search != null && !search.trim().isEmpty()) {
            // Поиск + статус + категория
            if (status != null && category != null) {
                bouquetsPage = applyAllFilters(search, status, category, pageable);
            }
            // Поиск + статус
            else if (status != null) {
                bouquetsPage = applySearchAndStatus(search, status, pageable);
            }
            // Поиск + категория
            else if (category != null) {
                bouquetsPage = bouquetService.findBySearchAndCategory(search, category, pageable);
            }
            // Только поиск
            else {
                bouquetsPage = bouquetService.findBySearchIncludeDeleted(search, pageable);
            }
        }
        // Без поиска, только статус и/или категория
        else {
            if (status != null && category != null) {
                bouquetsPage = applyStatusAndCategory(status, category, pageable);
            }
            // Только статус
            else if (status != null) {
                bouquetsPage = applyStatusFilter(status, pageable);
            }
            // Только категория
            else if (category != null) {
                bouquetsPage = bouquetService.findByCategoryId(category, pageable);
            }
            // Без фильтров - все букеты
            else {
                bouquetsPage = bouquetService.findAll(pageable);
            }
        }

        model.addAttribute("bouquets", bouquetsPage);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("category", category);
        model.addAttribute("statusParam", status); // Для пагинации
        model.addAttribute("categoryParam", category); // Для пагинации

        return "bouquet/manage";
    }

    // Вспомогательные методы для комбинированных фильтров
    private Page<Bouquet> applyAllFilters(String search, String status, Long category, Pageable pageable) {
        Page<Bouquet> result = bouquetService.findBySearchAndCategory(search, category, pageable);

        // Конвертируем Streamable в List и создаем новый Page
        List<Bouquet> filteredContent = result.getContent().stream()
                .filter(b -> {
                    if ("active".equals(status)) return !b.getDeleted();
                    if ("inactive".equals(status)) return b.getDeleted();
                    if ("out_of_stock".equals(status)) return !b.getInStock();
                    return true;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, filteredContent.size());
    }

    private Page<Bouquet> applySearchAndStatus(String search, String status, Pageable pageable) {
        Page<Bouquet> result = bouquetService.findBySearchIncludeDeleted(search, pageable);

        List<Bouquet> filteredContent = result.getContent().stream()
                .filter(b -> {
                    if ("active".equals(status)) return !b.getDeleted();
                    if ("inactive".equals(status)) return b.getDeleted();
                    if ("out_of_stock".equals(status)) return !b.getInStock();
                    return true;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, filteredContent.size());
    }

    private Page<Bouquet> applyStatusAndCategory(String status, Long category, Pageable pageable) {
        Page<Bouquet> result = bouquetService.findByCategoryId(category, pageable);

        List<Bouquet> filteredContent = result.getContent().stream()
                .filter(b -> {
                    if ("active".equals(status)) return !b.getDeleted();
                    if ("inactive".equals(status)) return b.getDeleted();
                    if ("out_of_stock".equals(status)) return !b.getInStock();
                    return true;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filteredContent, pageable, filteredContent.size());
    }

    private Page<Bouquet> applyStatusFilter(String status, Pageable pageable) {
        switch (status) {
            case "active":
                return bouquetService.findByDeletedFalse(pageable);
            case "inactive":
                return bouquetService.findByDeletedTrue(pageable);
            case "out_of_stock":
                return bouquetService.findByInStockFalse(pageable);
            default:
                return bouquetService.findAll(pageable);
        }
    }
}