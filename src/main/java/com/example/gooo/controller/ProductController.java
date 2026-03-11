package com.example.gooo.controller;

import com.example.gooo.dto.ProductResponseDTO;
import com.example.gooo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Управление товарами")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Получить список всех товаров")
    @GetMapping
    public List<ProductResponseDTO> getAll() {
        log.info("Request to fetch all products");
        List<ProductResponseDTO> result = productService.findAll();
        log.info("Fetched {} products", result.size());
        return result;
    }

    @Operation(summary = "Получить информацию о товаре по ID")
    @GetMapping("/{id}")
    public ProductResponseDTO getOne(@PathVariable Long id) {
        log.info("Request to fetch product by id: {}", id);
        return productService.findById(id);
    }
}
