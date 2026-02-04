package com.example.dockerpoc;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "price_audit_log")
public class PriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;   // e.g. "BTC", "ETH"
    private Double price;
    private String currency; // e.g. "USD"
    private LocalDateTime fetchedAt;
}