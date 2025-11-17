    package com.flowershop.controller.api;

    import com.flowershop.dto.ApiResponse;
    import com.flowershop.dto.BouquetDto;
    import com.flowershop.entity.Bouquet;
    import com.flowershop.service.BouquetService;
    import jakarta.validation.Valid;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.math.BigDecimal;
    import java.util.List;

    @RestController
    @RequestMapping("/api/bouquets")
    public class BouquetApiController {

        private final BouquetService bouquetService;

        public BouquetApiController(BouquetService bouquetService) {
            this.bouquetService = bouquetService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<Bouquet>>> getAllBouquets(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "name") String sortBy,
                @RequestParam(defaultValue = "asc") String sortDirection) {

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Bouquet> bouquets = bouquetService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success("Букеты получены успешно", bouquets));
        }

        @GetMapping("/active")
        public ResponseEntity<ApiResponse<Page<Bouquet>>> getActiveBouquets(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "12") int size) {

            Pageable pageable = PageRequest.of(page, size);
            Page<Bouquet> bouquets = bouquetService.findActiveBouquets(pageable);
            return ResponseEntity.ok(ApiResponse.success("Активные букеты получены успешно", bouquets));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<Bouquet>> getBouquetById(@PathVariable Long id) {
            Bouquet bouquet = bouquetService.findById(id);
            return ResponseEntity.ok(ApiResponse.success("Букет найден", bouquet));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<Bouquet>> createBouquet(@Valid @RequestBody BouquetDto bouquetDto) {
            Bouquet bouquet = bouquetService.save(bouquetDto);
            return ResponseEntity.ok(ApiResponse.success("Букет создан успешно", bouquet));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<Bouquet>> updateBouquet(@PathVariable Long id,
                                                                  @Valid @RequestBody BouquetDto bouquetDto) {
            Bouquet bouquet = bouquetService.update(id, bouquetDto);
            return ResponseEntity.ok(ApiResponse.success("Букет обновлен успешно", bouquet));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteBouquet(@PathVariable Long id) {
            bouquetService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Букет удален успешно"));
        }

        @PostMapping("/{id}/restore")
        public ResponseEntity<ApiResponse<Void>> restoreBouquet(@PathVariable Long id) {
            bouquetService.restore(id);
            return ResponseEntity.ok(ApiResponse.success("Букет восстановлен успешно"));
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<List<Bouquet>>> searchBouquets(@RequestParam String query) {
            List<Bouquet> bouquets = bouquetService.searchBouquets(query);
            return ResponseEntity.ok(ApiResponse.success("Результаты поиска", bouquets));
        }

        @GetMapping("/category/{categoryId}")
        public ResponseEntity<ApiResponse<List<Bouquet>>> getBouquetsByCategory(@PathVariable Long categoryId) {
            List<Bouquet> bouquets = bouquetService.findByCategory(categoryId);
            return ResponseEntity.ok(ApiResponse.success("Букеты по категории", bouquets));
        }

        @GetMapping("/price-range")
        public ResponseEntity<ApiResponse<List<Bouquet>>> getBouquetsByPriceRange(
                @RequestParam BigDecimal minPrice,
                @RequestParam BigDecimal maxPrice) {

            List<Bouquet> bouquets = bouquetService.findByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success("Букеты по диапазону цен", bouquets));
        }

        @GetMapping("/filter")
        public ResponseEntity<ApiResponse<Page<Bouquet>>> filterBouquets(
                @RequestParam(required = false) Long categoryId,
                @RequestParam(required = false) BigDecimal minPrice,
                @RequestParam(required = false) BigDecimal maxPrice,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "12") int size) {

            Pageable pageable = PageRequest.of(page, size);
            Page<Bouquet> bouquets;

            if (categoryId != null && minPrice != null && maxPrice != null) {
                // Combined filter - simplified implementation
                bouquets = bouquetService.findByCategoryIdAndInStock(categoryId, pageable);
            } else if (categoryId != null) {
                bouquets = bouquetService.findByCategoryIdAndInStock(categoryId, pageable);
            } else if (minPrice != null && maxPrice != null) {
                bouquets = bouquetService.findByPriceRange(minPrice, maxPrice, pageable);
            } else {
                bouquets = bouquetService.findActiveBouquets(pageable);
            }

            return ResponseEntity.ok(ApiResponse.success("Отфильтрованные букеты", bouquets));
        }
    }