package com.example.gooo.controller;

import com.example.gooo.dto.ProductResponseDTO;
import com.example.gooo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Управление товарами")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Получить список товаров с пагинацией")
    @GetMapping
    public Page<ProductResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Request to fetch products page {} size {}", page, size);
        return productService.findAll(page, size);
    }

    @Operation(summary = "Получить информацию о товаре по ID")
    @GetMapping("/{id}")
    public ProductResponseDTO getOne(@PathVariable Long id) {
        log.info("Request to fetch product by id: {}", id);
        return productService.findById(id);
    }
}
