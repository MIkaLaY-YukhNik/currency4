package com.example.currency4.repository;

import com.example.currency4.entity.ConversionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {

    List<ConversionHistory> findByFromCurrency(String fromCurrency);

    List<ConversionHistory> findByToCurrency(String toCurrency);

    @Query("SELECT ch FROM ConversionHistory ch WHERE ch.convertedAt = :date")
    List<ConversionHistory> findByDate(@Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM conversion_history WHERE user_id = :userId", nativeQuery = true)
    List<ConversionHistory> findByUserIdNative(@Param("userId") Long userId);

    Page<ConversionHistory> findAll(Pageable pageable);
}