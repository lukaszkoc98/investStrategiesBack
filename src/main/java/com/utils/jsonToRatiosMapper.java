package com.utils;

import com.models.Embedded;
import com.models.Performances;
import com.models.Ratio;
import com.models.Ratios;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class jsonToRatiosMapper {

    public static Ratios map(JSONObject jsonObject) throws JSONException {
        JSONObject jsonEmbedded = jsonObject.getJSONObject("_embedded");
        String metal = jsonObject.getString("metal");
        String currency = jsonObject.getString("currency");
        String weight_unit = jsonObject.getString("weight_unit");
        Embedded embedded = new Embedded(extractRatios(jsonEmbedded), extractPerformances(jsonEmbedded));
        return new Ratios(metal, currency, weight_unit, embedded);
    }

    public static Performances extractPerformances(JSONObject jsonEmbedded) throws JSONException {
        JSONObject performancesJson = jsonEmbedded.getJSONObject("performances");
        Performances performances = new Performances(performancesJson.getDouble("5D"),
                performancesJson.getDouble("1M"),
                performancesJson.getDouble("YTD"),
                performancesJson.getDouble("1Y"),
                performancesJson.getDouble("5Y"),
                performancesJson.getDouble("10Y"),
                performancesJson.getDouble("MAX"));
        return performances;
    }

    public static ArrayList<Ratio> extractRatios(JSONObject jsonEmbedded) throws JSONException {
        JSONArray jsonRatios = jsonEmbedded.getJSONArray("historical_spot_prices");
        ArrayList<Ratio> objectRatios = new ArrayList<>();
        for (int i = 0; i < jsonRatios.length(); i++) {
            JSONObject currentJsonRatio = jsonRatios.getJSONObject(i);
            Ratio currentRatio = new Ratio(LocalDate.parse(currentJsonRatio.getString("date")),
                    currentJsonRatio.getString("weight_unit"),
                    currentJsonRatio.getDouble("close"),
                    currentJsonRatio.getDouble("high"),
                    currentJsonRatio.getDouble("low"),
                    currentJsonRatio.getDouble("open"));
            objectRatios.add(currentRatio);
        }
        return objectRatios;
    }
}
