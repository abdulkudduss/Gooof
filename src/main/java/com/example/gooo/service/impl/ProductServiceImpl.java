package com.example.gooo.service.impl;

import com.example.gooo.domain.projections.ProductView;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.dto.ProductResponseDTO;
import com.example.gooo.exception.ResourceNotFoundException;
import com.example.gooo.mapper.ProductMapper;
import com.example.gooo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponseDTO> findAll(int page, int size) {
        log.info("Fetching products page {} size {} from repository", page, size);
        return productRepository.findAll(PageRequest.of(page, size))
                .map(productMapper::toDto);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        log.info("Fetching product by id: {}", id);
        return productRepository.findById(id, ProductView.class)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));
    }
}
