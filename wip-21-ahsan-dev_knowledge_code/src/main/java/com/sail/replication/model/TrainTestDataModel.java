package com.sail.replication.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class TrainTestDataModel {
    public List<String> trainintPullRequestList = new ArrayList<String>();
    public List<String> testingPullRequestList = new ArrayList<String>();
    public List<String> fullPullRequestList = new ArrayList<String>();


public List<String> getFullPullRequestList() {
        return fullPullRequestList;
    }


    public void setFullPullRequestList(List<String> fullPullRequestList) {
        this.fullPullRequestList = fullPullRequestList;
    }


public List<String> getTrainintPullRequestList() {
        return trainintPullRequestList;
    }


    public void setTrainintPullRequestList(List<String> trainintPullRequestList) {
        this.trainintPullRequestList = trainintPullRequestList;
    }


    public List<String> getTestingPullRequestList() {
        return testingPullRequestList;
    }


    public void setTestingPullRequestList(List<String> testingPullRequestList) {
        this.testingPullRequestList = testingPullRequestList;
    }


public static void main(String[] args) {
       System.out.println("Program finishes successfully");
   } 
}
