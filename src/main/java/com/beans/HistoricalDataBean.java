package com.beans;

import com.models.Ratios;
import com.utils.jsonToRatiosMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Startup
public class HistoricalDataBean {
    private static final String GOLD = "XAG";
    private static final String SILVER = "XAU";
    private static final String EURO = "EUR";
    private static final String US_DOLLAR = "USD";
    private static final String POLISH_ZLOTY = "PLN";

    private Ratios currentGoldPLNRatios = null;
    private Ratios currentGoldUSDRatios = null;
    private Ratios currentGoldEURRatios = null;
    private Ratios currentSilverPLNRatios = null;
    private Ratios currentSilverEURRatios = null;
    private Ratios currentSilverUSDRatios = null;

    public Ratios getGoldRatios(Properties whichRatios) {
        switch (whichRatios.get("Currency").toString()) {
            case "PLN":
                return this.currentGoldPLNRatios;
            case "USD":
                return this.currentGoldUSDRatios;
            case "EUR":
                return this.currentGoldEURRatios;
        }
        return null;
    }


    public Ratios getSilverRatios(Properties whichRatios) {
        switch (whichRatios.get("Currency").toString()) {
            case "PLN":
                return this.currentGoldPLNRatios;
            case "USD":
                return this.currentGoldUSDRatios;
            case "EUR":
                return this.currentGoldEURRatios;
        }
        return null;
    }

    @PostConstruct
    public void init() throws IOException, JSONException {
        this.currentGoldPLNRatios = getRatiosFromAPI(GOLD, POLISH_ZLOTY);
        this.currentGoldUSDRatios = getRatiosFromAPI(GOLD, US_DOLLAR);
        this.currentGoldEURRatios = getRatiosFromAPI(GOLD, EURO);
        this.currentSilverPLNRatios = getRatiosFromAPI(SILVER, POLISH_ZLOTY);
        this.currentSilverEURRatios = getRatiosFromAPI(SILVER, US_DOLLAR);
        this.currentSilverUSDRatios = getRatiosFromAPI(SILVER, EURO);
    }

    private Ratios getRatiosFromAPI(String metal, String currency) throws IOException, JSONException {
        URL url = new URL("https://goldbroker.com/api/historical-spot-prices?metal=" + metal + "&currency=" + currency + "&weight_unit=oz");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject jsonObject = new JSONObject(String.valueOf(content));
        return jsonToRatiosMapper.map(jsonObject);
    }

    private boolean isRatiosActual(Ratios ratios) {
        Date today = new Date();
        Date lastRatiosDate = ratios.getEmbedded().getHistorical_spot_prices().get(
                        ratios.getEmbedded().getHistorical_spot_prices().size() - 1)
                .getDate();
        return !lastRatiosDate.before(today);
    }

    public Ratios getCurrentGoldPLNRatios() {
        return currentGoldPLNRatios;
    }

    public Ratios getCurrentGoldUSDRatios() {
        return currentGoldUSDRatios;
    }

    public Ratios getCurrentGoldEURRatios() {
        return currentGoldEURRatios;
    }

    public Ratios getCurrentSilverPLNRatios() {
        return currentSilverPLNRatios;
    }

    public Ratios getCurrentSilverEURRatios() {
        return currentSilverEURRatios;
    }

    public Ratios getCurrentSilverUSDRatios() {
        return currentSilverUSDRatios;
    }

}
