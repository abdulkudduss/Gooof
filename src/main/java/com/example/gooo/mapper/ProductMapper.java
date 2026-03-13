package com.example.gooo.mapper;

import com.example.gooo.domain.entity.Product;
import com.example.gooo.domain.projections.ProductView;
import com.example.gooo.dto.ProductResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(source = "currentPrice", target = "price")
    ProductResponseDTO toDto(Product product);

    ProductResponseDTO toDto(ProductView productView);
}
