package com.flowershop.controller;

import com.flowershop.entity.Bouquet;
import com.flowershop.service.BouquetService;
import com.flowershop.service.CategoryService;
import com.flowershop.util.ThymeleafUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final BouquetService bouquetService;
    private final CategoryService categoryService;
    private final ThymeleafUtil thymeleafUtil;

    public MainController(BouquetService bouquetService,
                          CategoryService categoryService,
                          ThymeleafUtil thymeleafUtil) {
        this.bouquetService = bouquetService;
        this.categoryService = categoryService;
        this.thymeleafUtil = thymeleafUtil;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bouquet> bouquets = bouquetService.findActiveBouquets(pageable);

        model.addAttribute("bouquets", bouquets);
        model.addAttribute("categories", categoryService.findAllActive());
        return "index";
    }

    // ИЗМЕНИТЕ ЭТОТ МЕТОД - используйте BouquetController для каталога
    @GetMapping("/catalog")
    public String redirectToBouquets() {
        return "redirect:/bouquets";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}