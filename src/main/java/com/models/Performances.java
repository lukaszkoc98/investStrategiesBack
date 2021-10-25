package com.models;

public class Performances {
    private Double fiveDays;
    private Double oneMonth;
    private Double yearToDate;
    private Double oneYear;
    private Double fiveYears;
    private Double tenYears;
    private Double max;

    public Performances(Double fiveDays, Double oneMonth, Double yearToDate, Double oneYear, Double fiveYears, Double tenYears, Double max) {
        this.fiveDays = fiveDays;
        this.oneMonth = oneMonth;
        this.yearToDate = yearToDate;
        this.oneYear = oneYear;
        this.fiveYears = fiveYears;
        this.tenYears = tenYears;
        this.max = max;
    }

    public Double getFiveDays() {
        return fiveDays;
    }

    public void setFiveDays(Double fiveDays) {
        this.fiveDays = fiveDays;
    }

    public Double getOneMonth() {
        return oneMonth;
    }

    public void setOneMonth(Double oneMonth) {
        this.oneMonth = oneMonth;
    }

    public Double getYearToDate() {
        return yearToDate;
    }

    public void setYearToDate(Double yearToDate) {
        this.yearToDate = yearToDate;
    }

    public Double getOneYear() {
        return oneYear;
    }

    public void setOneYear(Double oneYear) {
        this.oneYear = oneYear;
    }

    public Double getFiveYears() {
        return fiveYears;
    }

    public void setFiveYears(Double fiveYears) {
        this.fiveYears = fiveYears;
    }

    public Double getTenYears() {
        return tenYears;
    }

    public void setTenYears(Double tenYears) {
        this.tenYears = tenYears;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
