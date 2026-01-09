package com.sail.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimilarityModel {
    public ArrayList<String> rankedName = new ArrayList<String>();
    public Map<String, Double> rankedValue = new HashMap<String, Double>();

    public ArrayList<String> getRankedName() {
        return rankedName;
    }

    public void setRankedName(ArrayList<String> rankedName) {
        this.rankedName = rankedName;
    }

    public Map<String, Double> getRankedValue() {
        return rankedValue;
    }

    public void setRankedValue(Map<String, Double> rankedValue) {
        this.rankedValue = rankedValue;
    }

}
