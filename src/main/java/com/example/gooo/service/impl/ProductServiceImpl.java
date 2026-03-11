package com.example.gooo.service.impl;

import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.dto.ProductResponseDTO;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.mapper.ProductMapper;
import com.example.gooo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDTO> findAll() {
        log.info("Fetching all products from repository");
        List<ProductResponseDTO> result = productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.info("Mapped {} products to DTOs", result.size());
        return result;
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        log.info("Fetching product by id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));
    }
}
