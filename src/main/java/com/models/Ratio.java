package com.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Ratio {
    private Date date;
    private String weight_unit;
    private Double close;
    private Double high;
    private Double low;
    private Double open;

    public Ratio(Date date, String weight_unit, Double close, Double high, Double low, Double open) {
        this.date = date;
        this.weight_unit = weight_unit;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
    }

    public Ratio(LocalDate date, String weight_unit, Double close, Double high, Double low, Double open) {
        this.date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());;
        this.weight_unit = weight_unit;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }


}

