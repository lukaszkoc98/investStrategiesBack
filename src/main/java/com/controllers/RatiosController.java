package com.controllers;

import com.beans.HistoricalDataBean;
import com.enums.Currency;
import com.enums.Metal;
import com.models.Ratios;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/ratios")
public class RatiosController {

    @Autowired
    private HistoricalDataBean historicalDataBean;

    @GetMapping
    @RequestMapping("/gold")
    public ResponseEntity<Ratios> getGold(@RequestParam String currency) throws JSONException, IOException {
        Ratios ratios = historicalDataBean.getRatios(Metal.GOLD, getSymbol(currency));
        return ResponseEntity.status(HttpStatus.OK).body(ratios);
    }

    @GetMapping
    @RequestMapping("/silver")
    public ResponseEntity<Ratios> getSilver(@RequestParam String currency) throws JSONException, IOException {
        Ratios ratios = historicalDataBean.getRatios(Metal.SILVER, getSymbol(currency));
        return ResponseEntity.status(HttpStatus.OK).body(ratios);
    }


    Currency getSymbol(String name) {
        for (Currency currency : Currency.values()) {
            if(currency.getSymbol().equals(name)) {
                return currency;
            }
        }
        return null;
    }

}
