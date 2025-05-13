package com.example.currency4.service;

import com.example.currency4.entity.CurrencyRate;
import com.example.currency4.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyRateService {

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Cacheable(value = "currencyRateCache")
    public List<CurrencyRate> getAllCurrencyRates() {
        return currencyRateRepository.findAll();
    }

    @Cacheable(value = "currencyRateCache", key = "#currencyCode")
    public Optional<CurrencyRate> getCurrencyRateByCode(String currencyCode) {
        return currencyRateRepository.findById(currencyCode);
    }

    @CachePut(value = "currencyRateCache", key = "#result.currencyCode")
    public CurrencyRate createCurrencyRate(CurrencyRate currencyRate) {
        return currencyRateRepository.save(currencyRate);
    }

    @CachePut(value = "currencyRateCache", key = "#currencyCode")
    public CurrencyRate updateCurrencyRate(String currencyCode, CurrencyRate currencyRateDetails) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRate.setRate(currencyRateDetails.getRate());
        currencyRate.setLastUpdated(currencyRateDetails.getLastUpdated());
        currencyRate.setSource(currencyRateDetails.getSource());
        return currencyRateRepository.save(currencyRate);
    }

    @CacheEvict(value = "currencyRateCache", key = "#currencyCode")
    public void deleteCurrencyRate(String currencyCode) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRateRepository.delete(currencyRate);
    }
}