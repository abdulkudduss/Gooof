package com.example.gooo.service;

import com.example.gooo.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponseDTO> findAll(int page, int size);
    ProductResponseDTO findById(Long id);
}
