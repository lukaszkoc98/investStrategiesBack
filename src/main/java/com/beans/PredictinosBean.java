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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Startup
public class PredictinosBean {

    private ArrayList<Prediction> predictionsGold = new ArrayList<>();
    private ArrayList<Prediction> predictionsSilver = new ArrayList<>();

    public ArrayList<Prediction> getGoldPredictions() throws IOException, ParseException {
        if (isPredictionActual(this.predictionsGold)) {
            return this.predictionsGold;
        }
        predict("Gold");
        return this.predictionsGold;
    }

    public ArrayList<Prediction> getSilverPredictions() throws IOException, ParseException {
        if (isPredictionActual(this.predictionsSilver)) {
            return this.predictionsSilver;
        }
        predict("Silver");
        return this.predictionsSilver;
    }

    @PostConstruct
    public void init() throws IOException, ParseException {
        predict("Gold");
        predict("Silver");
    }

    public void predict(String metal) throws IOException, ParseException {
        if ("Gold".equals(metal)) {
            this.predictionsGold = Predictor.predict(metal);
        } else {
            this.predictionsSilver = Predictor.predict(metal);
        }
    }


    private boolean isPredictionActual(ArrayList<Prediction> predictions) {
        int today = LocalDate.now().getDayOfYear();
        int lastPredictionDate = predictions.get(predictions.size() - 1).getFutureDate().getDayOfYear();
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY
                || LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY) {
            return today <= lastPredictionDate - 20;
        }
        return today <= lastPredictionDate - 22;
    }

}
