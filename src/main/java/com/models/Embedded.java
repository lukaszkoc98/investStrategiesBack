package com.models;

import java.util.ArrayList;

public class Embedded {
    private ArrayList<Ratio> historical_spot_prices;
    private Performances performances;

    public Embedded(ArrayList<Ratio> historical_spot_prices, Performances performances) {
        this.historical_spot_prices = historical_spot_prices;
        this.performances = performances;
    }

    public ArrayList<Ratio> getHistorical_spot_prices() {
        return historical_spot_prices;
    }

    public void setHistorical_spot_prices(ArrayList<Ratio> historical_spot_prices) {
        this.historical_spot_prices = historical_spot_prices;
    }

    public Performances getPerformances() {
        return performances;
    }

    public void setPerformances(Performances performances) {
        this.performances = performances;
    }
}
