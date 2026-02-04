package com.example.dockerpoc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    List<PriceEntity> findBySymbol(String symbol);
}