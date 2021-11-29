package com.controllers;


import com.beans.PredictinosBean;
import com.enums.Metal;
import com.models.Prediction;
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
import java.text.ParseException;
import java.util.ArrayList;

@RestController
@RequestMapping("/predictions")
public class PredictionsController {
    @Autowired
    PredictinosBean predictinosBean;


    @GetMapping
    @RequestMapping("/gold")
    public ResponseEntity<ArrayList<Prediction>> getGoldPredictions() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(predictinosBean.getGoldPredictions());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping
    @RequestMapping("/silver")
    public ResponseEntity<ArrayList<Prediction>> getSilverPredictions() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(predictinosBean.getSilverPredictions());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
