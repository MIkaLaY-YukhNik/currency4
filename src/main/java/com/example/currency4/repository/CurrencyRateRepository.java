package com.example.currency4.repository;

import com.example.currency4.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, String> {
}