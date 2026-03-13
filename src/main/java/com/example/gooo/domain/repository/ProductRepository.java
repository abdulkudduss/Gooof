package com.example.gooo.domain.repository;

import com.example.gooo.domain.entity.Product;
import com.example.gooo.domain.projections.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    <T> Optional<T> findById(Long id, Class<T> type);
}
