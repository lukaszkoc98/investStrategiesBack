package com.models;

public class Ratios {
    private String metal;
    private String currency;
    private String weight_unit;
    private Embedded embedded;


    public Ratios(String metal, String currency, String weight_unit, Embedded embedded) {
        this.metal = metal;
        this.currency = currency;
        this.weight_unit = weight_unit;
        this.embedded = embedded;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public Embedded getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }
}
