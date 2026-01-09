package com.sail.replication.model;

public class ReviewAnalysisModel {

    public String projectName;
    public  int totalReviewer;
    public int totalPR;
    public double medianPR;
    public double meanPR;
    public double maxPR;
    public double minPR;
    public double q1;
    public double q3;
    public int totalJavaFileChaangedCommits;
    public int totalFullCommits;
    public int totalDevelopers;
    public int prReviewerAndDeveloper;

    

    public int getPrReviewerAndDeveloper() {
        return prReviewerAndDeveloper;
    }
    public void setPrReviewerAndDeveloper(int prReviewerAndDeveloper) {
        this.prReviewerAndDeveloper = prReviewerAndDeveloper;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public int getTotalReviewer() {
        return totalReviewer;
    }
    public void setTotalReviewer(int totalReviewer) {
        this.totalReviewer = totalReviewer;
    }
    public int getTotalPR() {
        return totalPR;
    }
    public void setTotalPR(int totalPR) {
        this.totalPR = totalPR;
    }
    public double getMedianPR() {
        return medianPR;
    }
    public void setMedianPR(double medianPR) {
        this.medianPR = medianPR;
    }
    public double getMeanPR() {
        return meanPR;
    }
    public void setMeanPR(double meanPR) {
        this.meanPR = meanPR;
    }
    public double getMaxPR() {
        return maxPR;
    }
    public void setMaxPR(double maxPR) {
        this.maxPR = maxPR;
    }
    public double getMinPR() {
        return minPR;
    }
    public void setMinPR(double minPR) {
        this.minPR = minPR;
    }
    public double getQ1() {
        return q1;
    }
    public void setQ1(double q1) {
        this.q1 = q1;
    }
    public double getQ3() {
        return q3;
    }
    public void setQ3(double q3) {
        this.q3 = q3;
    }
    public int getTotalJavaFileChaangedCommits() {
        return totalJavaFileChaangedCommits;
    }
    public void setTotalJavaFileChaangedCommits(int totalJavaFileChaangedCommits) {
        this.totalJavaFileChaangedCommits = totalJavaFileChaangedCommits;
    }
    public int getTotalFullCommits() {
        return totalFullCommits;
    }
    public void setTotalFullCommits(int totalFullCommits) {
        this.totalFullCommits = totalFullCommits;
    }
    public int getTotalDevelopers() {
        return totalDevelopers;
    }
    public void setTotalDevelopers(int totalDevelopers) {
        this.totalDevelopers = totalDevelopers;
    }
}
