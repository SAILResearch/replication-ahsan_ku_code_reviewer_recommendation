package com.sail.java.exam.dev.profile.cluster;

import java.util.HashMap;
import java.util.Map;

public class DevProfileWithClusterModel {
    public String projectName;
    public String developerName;
    public int year;
    public boolean isCoreDev;
    public String clusterLebel;
    Map<String, Double> kuValues;
    public int totalJavaCommits;
    public int totalCommits;
    public int pcaSelectedElements;
    public String developerYearName;
    Map<String,Double> pcaValues;
    

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getDeveloperName() {
        return developerName;
    }
    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public boolean isCoreDev() {
        return isCoreDev;
    }
    public void setCoreDev(boolean isCoreDev) {
        this.isCoreDev = isCoreDev;
    }
    public String getClusterLebel() {
        return clusterLebel;
    }
    public void setClusterLebel(String clusterLebel) {
        this.clusterLebel = clusterLebel;
    }
    public Map<String, Double> getKuValues() {
        return kuValues;
    }
    public void setKuValues(Map<String, Double> kuValues) {
        this.kuValues = kuValues;
    }
    public int getTotalJavaCommits() {
        return totalJavaCommits;
    }
    public void setTotalJavaCommits(int totalJavaCommits) {
        this.totalJavaCommits = totalJavaCommits;
    }
    public int getTotalCommits() {
        return totalCommits;
    }
    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }
    public int getPcaSelectedElements() {
        return pcaSelectedElements;
    }
    public void setPcaSelectedElements(int pcaSelectedElements) {
        this.pcaSelectedElements = pcaSelectedElements;
    }
    public Map<String, Double> getPcaValues() {
        return pcaValues;
    }
    public void setPcaValues(Map<String, Double> pcaValues) {
        this.pcaValues = pcaValues;
    }
    public String getDeveloperYearName() {
        return developerYearName;
    }
    public void setDeveloperYearName(String developerYearName) {
        this.developerYearName = developerYearName;
    }

    
}
