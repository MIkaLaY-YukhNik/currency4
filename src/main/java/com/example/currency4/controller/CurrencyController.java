package com.example.currency4.controller;

import com.example.currency4.entity.ConversionHistory;
import com.example.currency4.model.CurrencyResponse;
import com.example.currency4.repository.ConversionHistoryRepository;
import com.example.currency4.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Currency Converter", description = "API for currency conversion and history")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final ConversionHistoryRepository conversionHistoryRepository;
    private final Map<String, List<ConversionHistory>> cache;

    public CurrencyController(CurrencyService currencyService, ConversionHistoryRepository conversionHistoryRepository, Map<String, List<ConversionHistory>> cache) {
        this.currencyService = currencyService;
        this.conversionHistoryRepository = conversionHistoryRepository;
        this.cache = cache;
    }

    @GetMapping("/convert")
    @Operation(summary = "Convert currency", description = "Converts an amount from one currency to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversion successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> convert(
            @Parameter(description = "Source currency code") @RequestParam String from,
            @Parameter(description = "Target currency code") @RequestParam String to,
            @Parameter(description = "Amount to convert") @RequestParam double amount) {
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("From currency must not be blank");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("To currency must not be blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        try {
            CurrencyResponse rates = currencyService.fetchExchangeRates();
            double convertedAmount = currencyService.convertAmount(from, to, amount, rates);

            ConversionHistory conversionHistory = new ConversionHistory(
                    from.toUpperCase(),
                    to.toUpperCase(),
                    amount,
                    convertedAmount
            );
            conversionHistoryRepository.save(conversionHistory);

            Map<String, Object> result = new HashMap<>();
            result.put("fromCurrency", from.toUpperCase());
            result.put("toCurrency", to.toUpperCase());
            result.put("convertedAmount", convertedAmount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Unable to process conversion: " + e.getMessage(), e);
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get conversion history by source currency", description = "Returns conversion history for a given source currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid currency"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ConversionHistory>> getConversionHistoryByCurrency(
            @Parameter(description = "Source currency code") @RequestParam String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must not be empty");
        }

        String cacheKey = currency.toUpperCase();
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        List<ConversionHistory> history = conversionHistoryRepository.findByFromCurrency(cacheKey);
        cache.put(cacheKey, history);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/to-history")
    @Operation(summary = "Get conversion history by target currency", description = "Returns conversion history for a given target currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid currency"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ConversionHistory>> getConversionToHistoryByCurrency(
            @Parameter(description = "Target currency code") @RequestParam String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must not be empty");
        }

        String cacheKey = currency.toUpperCase();
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        List<ConversionHistory> history = conversionHistoryRepository.findByToCurrency(cacheKey);
        cache.put(cacheKey, history);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/history-by-date")
    @Operation(summary = "Get conversion history by date", description = "Returns conversion history for a given date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ConversionHistory>> getConversionHistoryByDate(
            @Parameter(description = "Date in ISO format (e.g., 2023-10-01T10:00:00)") @RequestParam String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date must not be empty");
        }

        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format (e.g., 2023-10-01T10:00:00)", e);
        }

        String cacheKey = "date_" + parsedDate.toString();
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        List<ConversionHistory> history = conversionHistoryRepository.findByDate(parsedDate);
        cache.put(cacheKey, history);

        return ResponseEntity.ok(history);
    }
}