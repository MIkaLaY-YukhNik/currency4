package com.example.currency4.service;

import com.example.currency4.entity.CurrencyRate;
import com.example.currency4.model.CurrencyResponse;
import com.example.currency4.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CurrencyService {

    private static final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=";
    private final String apiKey;
    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyService(RestTemplate restTemplate, CurrencyRateRepository currencyRateRepository,
                           @Value("${open.exchange.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.currencyRateRepository = currencyRateRepository;
        this.apiKey = apiKey;
    }

    public CurrencyResponse fetchExchangeRates() {
        if (apiKey.isEmpty()) {
            throw new RuntimeException("API key for Open Exchange Rates is not provided");
        }

        String fullApiUrl = API_URL + apiKey;
        try {
            CurrencyResponse response = restTemplate.getForObject(fullApiUrl, CurrencyResponse.class);
            if (response == null || response.getRates() == null) {
                throw new RuntimeException("Unable to fetch exchange rates: empty response");
            }

            Map<String, Double> rates = response.getRates();
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                String currency = entry.getKey();
                Double rate = entry.getValue();
                CurrencyRate currencyRate = new CurrencyRate(currency, rate);
                currencyRateRepository.save(currencyRate);
            }

            return response;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch exchange rates: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while fetching exchange rates: " + e.getMessage());
        }
    }

    public double convertAmount(String from, String to, double amount, CurrencyResponse rates) {
        Map<String, Double> rateMap = rates.getRates();
        double fromRate = rateMap.getOrDefault(from.toUpperCase(), 1.0);
        double toRate = rateMap.getOrDefault(to.toUpperCase(), 1.0);
        return (amount / fromRate) * toRate;
    }
}