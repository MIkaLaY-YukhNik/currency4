package com.example.currency4.controller;

import com.example.currency4.cache.CacheConfig;
import com.example.currency4.dto.ConvertRequest;
import com.example.currency4.entity.ConversionHistory;
import com.example.currency4.entity.CurrencyRate;
import com.example.currency4.entity.User;
import com.example.currency4.model.CurrencyResponse;
import com.example.currency4.repository.ConversionHistoryRepository;
import com.example.currency4.repository.CurrencyRateRepository;
import com.example.currency4.repository.UserRepository;
import com.example.currency4.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final ConversionHistoryRepository conversionHistoryRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final UserRepository userRepository;
    private final Map<String, List<ConversionHistory>> cache;

    public CurrencyController(CurrencyService currencyService, ConversionHistoryRepository conversionHistoryRepository,
                              CurrencyRateRepository currencyRateRepository, UserRepository userRepository,
                              Map<String, List<ConversionHistory>> cache) {
        this.currencyService = currencyService;
        this.conversionHistoryRepository = conversionHistoryRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.userRepository = userRepository;
        this.cache = cache;
    }

    @PostMapping("/convert")
    @Transactional
    public ResponseEntity<Map<String, Object>> convert(@Valid @RequestBody ConvertRequest request) {
        CurrencyResponse rates = currencyService.fetchExchangeRates();
        double convertedAmount = currencyService.convertAmount(request.getFrom(), request.getTo(), request.getAmount(), rates);

        User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));

        ConversionHistory conversionHistory = new ConversionHistory(
                request.getFrom().toUpperCase(),
                request.getTo().toUpperCase(),
                request.getAmount(),
                convertedAmount,
                user
        );
        conversionHistory.setNotes("Automated conversion");
        conversionHistory.setStatus("COMPLETED");

        Optional<CurrencyRate> fromCurrencyRate = currencyRateRepository.findById(request.getFrom().toUpperCase());
        Optional<CurrencyRate> toCurrencyRate = currencyRateRepository.findById(request.getTo().toUpperCase());

        fromCurrencyRate.ifPresent(conversionHistory.getCurrencyRates()::add);
        toCurrencyRate.ifPresent(conversionHistory.getCurrencyRates()::add);

        conversionHistoryRepository.save(conversionHistory);

        Map<String, Object> result = new HashMap<>();
        result.put("fromCurrency", request.getFrom().toUpperCase());
        result.put("toCurrency", request.getTo().toUpperCase());
        result.put("convertedAmount", convertedAmount);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConversionHistory>> getConversionHistoryByCurrency(
            @RequestParam String currency) {
        String cacheKey = currency.toUpperCase();
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        List<ConversionHistory> history = conversionHistoryRepository.findByFromCurrency(cacheKey);
        cache.put(cacheKey, history);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/to-history")
    public ResponseEntity<List<ConversionHistory>> getConversionToHistoryByCurrency(
            @RequestParam String currency) {
        String cacheKey = currency.toUpperCase();
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        List<ConversionHistory> history = conversionHistoryRepository.findByToCurrency(cacheKey);
        cache.put(cacheKey, history);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/history-by-date")
    public ResponseEntity<List<ConversionHistory>> getConversionHistoryByDate(
            @RequestParam String date) {
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(date); // Ожидает формат "2025-05-11T10:00:00"
            String cacheKey = "date_" + parsedDate.toString();
            if (cache.containsKey(cacheKey)) {
                return ResponseEntity.ok(cache.get(cacheKey));
            }

            List<ConversionHistory> history = conversionHistoryRepository.findByDate(parsedDate);
            cache.put(cacheKey, history);

            return ResponseEntity.ok(history);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format (e.g., 2025-05-11T10:00:00)", e);
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<ConversionHistory> getConversionHistoryById(@PathVariable Long id) {
        Optional<ConversionHistory> history = conversionHistoryRepository.findById(id);
        return history.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<ConversionHistory>> getConversionHistoryByUserId(@PathVariable Long userId) {
        List<ConversionHistory> history = conversionHistoryRepository.findByUserIdNative(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/sorted")
    public ResponseEntity<List<ConversionHistory>> getSortedConversionHistory() {
        List<ConversionHistory> history = conversionHistoryRepository.findAll();
        Collections.sort(history, (h1, h2) -> h1.getConvertedAt().compareTo(h2.getConvertedAt()));
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/paged")
    public ResponseEntity<Page<ConversionHistory>> getPagedConversionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversionHistory> historyPage = conversionHistoryRepository.findAll(pageable);
        return ResponseEntity.ok(historyPage);
    }
}