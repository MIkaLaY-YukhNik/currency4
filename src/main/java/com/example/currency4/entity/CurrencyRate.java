package com.example.currency4.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "currency_rate")
public class CurrencyRate {

    @Id
    private String currencyCode;

    private double rate;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "source", length = 100)
    private String source;

    @ManyToMany(mappedBy = "currencyRates", fetch = FetchType.LAZY)
    private Set<ConversionHistory> conversionHistories = new HashSet<>();

    public CurrencyRate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public CurrencyRate(String currencyCode, double rate) {
        this.currencyCode = currencyCode;
        this.rate = rate;
        this.lastUpdated = LocalDateTime.now();
        this.source = "OpenExchangeRates";
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Set<ConversionHistory> getConversionHistories() {
        return conversionHistories;
    }

    public void setConversionHistories(Set<ConversionHistory> conversionHistories) {
        this.conversionHistories = conversionHistories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode);
    }
}