package com.example.gooo.domain.repository;

import com.example.gooo.domain.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
}
