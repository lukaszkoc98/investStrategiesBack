package com.beans;

import com.enums.Currency;
import com.enums.Metal;
import com.models.Ratios;
import com.utils.jsonToRatiosMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Startup
public class HistoricalDataBean {

    HashMap<Pair<Metal, Currency>, Ratios> allRatios = new HashMap<>();


    public Ratios getRatios(Metal metal, Currency currency) throws JSONException, IOException {
        refreshRatiosIfOutdated(metal, currency);
        return this.allRatios.get(Pair.of(metal, currency));
    }


    private void refreshRatiosIfOutdated(Metal metal, Currency currency) throws IOException, JSONException {
        if (this.allRatios.get(Pair.of(metal, currency)) != null &&
                isRatiosActual(this.allRatios.get(Pair.of(metal, currency)))) {
            this.init();
        }
    }

    @PostConstruct
    public void init() throws IOException, JSONException {
        Pair<Metal, Currency> pair = Pair.of(Metal.GOLD, Currency.PLN);
//        this.allRatios.put(Pair.of(Metal.GOLD, Currency.PLN), getRatiosFromAPI(Metal.GOLD.getSymbol(), Currency.PLN.getSymbol()));
//        this.allRatios.put(Pair.of(Metal.GOLD, Currency.USD), getRatiosFromAPI(Metal.GOLD.getSymbol(), Currency.USD.getSymbol()));
        this.allRatios.put(Pair.of(Metal.GOLD, Currency.EUR), getRatiosFromAPI(Metal.GOLD.getSymbol(), Currency.EUR.getSymbol()));
//        this.allRatios.put(Pair.of(Metal.SILVER, Currency.PLN), getRatiosFromAPI(Metal.SILVER.getSymbol(), Currency.PLN.getSymbol()));
//        this.allRatios.put(Pair.of(Metal.SILVER, Currency.USD), getRatiosFromAPI(Metal.SILVER.getSymbol(), Currency.USD.getSymbol()));
        this.allRatios.put(Pair.of(Metal.SILVER, Currency.EUR), getRatiosFromAPI(Metal.SILVER.getSymbol(), Currency.EUR.getSymbol()));
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

}
