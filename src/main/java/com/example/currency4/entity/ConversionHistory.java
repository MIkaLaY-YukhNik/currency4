package com.example.currency4.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "conversion_history")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ConversionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double convertedAmount;

    @Column(name = "converted_at", nullable = false)
    private LocalDateTime convertedAt;

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversion_history_currency_rate",
            joinColumns = @JoinColumn(name = "conversion_history_id"),
            inverseJoinColumns = @JoinColumn(name = "currency_rate_code")
    )
    @JsonManagedReference
    private Set<CurrencyRate> currencyRates = new HashSet<>();

    public ConversionHistory() {
        this.convertedAt = LocalDateTime.now();
    }

    public ConversionHistory(String fromCurrency, String toCurrency, double amount, double convertedAmount, User user) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.convertedAt = LocalDateTime.now();
        this.status = "COMPLETED";
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public LocalDateTime getConvertedAt() {
        return convertedAt;
    }

    public void setConvertedAt(LocalDateTime convertedAt) {
        this.convertedAt = convertedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<CurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    public void setCurrencyRates(Set<CurrencyRate> currencyRates) {
        this.currencyRates = currencyRates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversionHistory that = (ConversionHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}