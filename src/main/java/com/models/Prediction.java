package com.models;

import java.time.LocalDate;

public class Prediction {
    private LocalDate date;
    private Double gold;
    private Double goldReturn;
    private Double goldFutureValue;
    private LocalDate futureDate;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getGold() {
        return gold;
    }

    public void setGold(Double gold) {
        this.gold = gold;
    }

    public Double getGoldReturn() {
        return goldReturn;
    }

    public void setGoldReturn(Double goldReturn) {
        this.goldReturn = goldReturn;
    }

    public Double getGoldFutureValue() {
        return goldFutureValue;
    }

    public void setGoldFutureValue(Double goldFutureValue) {
        this.goldFutureValue = goldFutureValue;
    }

    public LocalDate getFutureDate() {
        return futureDate;
    }

    public void setFutureDate(LocalDate futureDate) {
        this.futureDate = futureDate;
    }

    public Prediction(LocalDate date, Double gold, Double goldReturn, Double goldFutureValue, LocalDate futureDate) {
        this.date = date;
        this.gold = gold;
        this.goldReturn = goldReturn;
        this.goldFutureValue = goldFutureValue;
        this.futureDate = futureDate;
    }
}
