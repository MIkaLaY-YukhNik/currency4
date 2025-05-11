package com.example.currency4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ConvertRequest {

    @NotBlank(message = "From currency must not be blank")
    private String from;

    @NotBlank(message = "To currency must not be blank")
    private String to;

    @Positive(message = "Amount must be positive")
    private double amount;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}