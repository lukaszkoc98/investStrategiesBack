package com.beans;

import com.models.Prediction;
import com.models.Ratios;
import com.prediction.Predictor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Startup
public class PredictinosBean {

    private ArrayList<Prediction> predictions = new ArrayList<>();

    public ArrayList<Prediction> getPredictions() throws IOException, ParseException {
        if (isPredictionActual(this.predictions)) {
            return this.predictions;
        }
        predict();
        return this.predictions;
    }

    @PostConstruct
    public void predict() throws IOException, ParseException {
        this.predictions = Predictor.predict();
    }


    private boolean isPredictionActual(ArrayList<Prediction> predictions) {
        int today = LocalDate.now().getDayOfYear();
        int lastPredictionDate = predictions.get(predictions.size() - 1).getFutureDate().getDayOfYear();
        return today == lastPredictionDate - 22;
    }

}
