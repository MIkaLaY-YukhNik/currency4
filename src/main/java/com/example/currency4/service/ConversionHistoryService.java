package com.example.currency4.service;

import com.example.currency4.entity.ConversionHistory;
import com.example.currency4.entity.CurrencyRate;
import com.example.currency4.repository.ConversionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ConversionHistoryService {

    @Autowired
    private ConversionHistoryRepository conversionHistoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Cacheable(value = "conversionHistoryCache")
    public List<ConversionHistory> getAllConversionHistories() {
        List<ConversionHistory> histories = conversionHistoryRepository.findAll();
        // Предварительная загрузка связанных сущностей в кэш
        histories.forEach(this::cacheRelatedEntities);
        return histories;
    }

    @Cacheable(value = "conversionHistoryCache", key = "#id")
    public Optional<ConversionHistory> getConversionHistoryById(Long id) {
        Optional<ConversionHistory> history = conversionHistoryRepository.findById(id);
        history.ifPresent(this::cacheRelatedEntities);
        return history;
    }

    @CachePut(value = "conversionHistoryCache", key = "#result.id")
    public ConversionHistory createConversionHistory(ConversionHistory conversionHistory) {
        ConversionHistory savedHistory = conversionHistoryRepository.save(conversionHistory);
        cacheRelatedEntities(savedHistory);
        return savedHistory;
    }

    @CachePut(value = "conversionHistoryCache", key = "#id")
    public ConversionHistory updateConversionHistory(Long id, ConversionHistory conversionHistoryDetails) {
        ConversionHistory conversionHistory = conversionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConversionHistory not found with id: " + id));
        conversionHistory.setFromCurrency(conversionHistoryDetails.getFromCurrency());
        conversionHistory.setToCurrency(conversionHistoryDetails.getToCurrency());
        conversionHistory.setAmount(conversionHistoryDetails.getAmount());
        conversionHistory.setConvertedAmount(conversionHistoryDetails.getConvertedAmount());
        conversionHistory.setConvertedAt(conversionHistoryDetails.getConvertedAt());
        conversionHistory.setNotes(conversionHistoryDetails.getNotes());
        conversionHistory.setStatus(conversionHistoryDetails.getStatus());
        conversionHistory.setUser(conversionHistoryDetails.getUser());
        conversionHistory.setCurrencyRates(conversionHistoryDetails.getCurrencyRates());
        ConversionHistory updatedHistory = conversionHistoryRepository.save(conversionHistory);
        cacheRelatedEntities(updatedHistory);
        return updatedHistory;
    }

    @CacheEvict(value = "conversionHistoryCache", key = "#id")
    public void deleteConversionHistory(Long id) {
        ConversionHistory conversionHistory = conversionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConversionHistory not found with id: " + id));
        // Очистка кэша связанных сущностей
        evictRelatedEntities(conversionHistory);
        conversionHistoryRepository.delete(conversionHistory);
    }

    private void cacheRelatedEntities(ConversionHistory conversionHistory) {
        // Кэшируем пользователя
        if (conversionHistory.getUser() != null) {
            userService.getUserById(conversionHistory.getUser().getId());
        }
        // Кэшируем связанные CurrencyRate
        if (conversionHistory.getCurrencyRates() != null) {
            conversionHistory.getCurrencyRates().forEach(rate ->
                    currencyRateService.getCurrencyRateByCode(rate.getCurrencyCode()));
        }
    }

    private void evictRelatedEntities(ConversionHistory conversionHistory) {
        // Очистка кэша пользователя
        if (conversionHistory.getUser() != null) {
            userService.deleteUser(conversionHistory.getUser().getId());
        }
        // Очистка кэша CurrencyRate
        if (conversionHistory.getCurrencyRates() != null) {
            conversionHistory.getCurrencyRates().forEach(rate ->
                    currencyRateService.deleteCurrencyRate(rate.getCurrencyCode()));
        }
    }
}