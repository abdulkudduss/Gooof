package com.example.gooo.domain.repository;

import com.example.gooo.domain.entity.Carrier;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    // Ищет компанию по имени (например "CDEK")
    Optional<Carrier> findByName(String name);

    // Возвращает список только тех компаний, у которых active = true
    List<Carrier> findByActiveTrue();
}
