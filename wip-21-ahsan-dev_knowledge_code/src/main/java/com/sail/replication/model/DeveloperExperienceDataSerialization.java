package com.sail.replication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sail.model.ReviwerRecommendationDataLoader;


public class DeveloperExperienceDataSerialization implements Serializable{

    private static final long serialVersionUID = 1L;

    String projectName = "";

    List<String> trainingPRList = new ArrayList<String>();

    Map<String,Map<String, ArrayList<Map<String, Double>>>> developerPreviousDevelopmentExpertise =
    new HashMap<String, Map<String,ArrayList<Map<String,Double>>>>();

    Map<String,Map<String, ArrayList<Map<String, Double>>>> reviwerPreviousExpertiseWithPR =
    new HashMap<String, Map<String,ArrayList<Map<String,Double>>>>();

    Map<String,Map<String, Map<String, Double>>> developerExpertiseHistory = new HashMap<String, Map<String, Map<String,Double>>>();
    Map<String,Map<String, Map<String, Double>>> reviewerExpertiseHistory  = new HashMap<String, Map<String, Map<String,Double>>>();
    

    Map<String,Map<String, Double>> kuPresentInChangeFiles = new HashMap<String, Map<String, Double>>();

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public Map<String, Map<String, Map<String, Double>>> getDeveloperExpertiseHistory() {
        return developerExpertiseHistory;
    }
    public void setDeveloperExpertiseHistory(Map<String, Map<String, Map<String, Double>>> developerExpertiseHistory) {
        this.developerExpertiseHistory = developerExpertiseHistory;
    }
    public Map<String, Map<String, Map<String, Double>>> getReviewerExpertiseHistory() {
        return reviewerExpertiseHistory;
    }
    public void setReviewerExpertiseHistory(Map<String, Map<String, Map<String, Double>>> reviewerExpertiseHistory) {
        this.reviewerExpertiseHistory = reviewerExpertiseHistory;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public List<String> getTrainingPRList() {
        return trainingPRList;
    }
    public void setTrainingPRList(List<String> trainingPRList) {
        this.trainingPRList = trainingPRList;
    }
    public Map<String, Map<String, ArrayList<Map<String, Double>>>> getDeveloperPreviousDevelopmentExpertise() {
        return developerPreviousDevelopmentExpertise;
    }
    public void setDeveloperPreviousDevelopmentExpertise(
            Map<String, Map<String, ArrayList<Map<String, Double>>>> developerPreviousDevelopmentExpertise) {
        this.developerPreviousDevelopmentExpertise = developerPreviousDevelopmentExpertise;
    }
    public Map<String, Map<String, ArrayList<Map<String, Double>>>> getReviwerPreviousExpertiseWithPR() {
        return reviwerPreviousExpertiseWithPR;
    }
    public void setReviwerPreviousExpertiseWithPR(
            Map<String, Map<String, ArrayList<Map<String, Double>>>> reviwerPreviousExpertiseWithPR) {
        this.reviwerPreviousExpertiseWithPR = reviwerPreviousExpertiseWithPR;
    }
    public Map<String, Map<String, Double>> getKuPresentInChangeFiles() {
        return kuPresentInChangeFiles;
    }
    public void setKuPresentInChangeFiles(Map<String, Map<String, Double>> kuPresentInChangeFiles) {
        this.kuPresentInChangeFiles = kuPresentInChangeFiles;
    }
    public  DeveloperExperienceDataSerialization(){

    }
    public static void main(String[] args) {
        System.out.println("[Success] Developer Experience Serilization");
    }
}
