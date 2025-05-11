package com.example.currency4.repository;

import com.example.currency4.entity.ConversionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {

    @Query("SELECT c FROM ConversionHistory c WHERE c.fromCurrency = :currency")
    List<ConversionHistory> findByFromCurrency(String currency);

    @Query("SELECT c FROM ConversionHistory c WHERE c.toCurrency = :currency")
    List<ConversionHistory> findByToCurrency(String currency);

    @Query("SELECT c FROM ConversionHistory c WHERE c.convertedAt = :date")
    List<ConversionHistory> findByDate(LocalDateTime date);
}