package com.sail.replication.model;

import java.util.HashSet;
import java.util.Set;

public class SofiaDeveloperModel {
    String developerName;
    Set<String> fileReviewedList = new HashSet<String>();
    Set<String> fileCommitList = new HashSet<String>();
    Set<String> fileReviewCommitList = new HashSet<String>();

    Set<String> commitReviewListPastYear = new HashSet<String>();
    Set<String> activityMonthLastYear = new HashSet<String>();

    public double turnoverRec;
    public double learnRec;
    public double retationRec;
    public double contributionRatio;
    public double consistencyRatio;
    
    public void printAllScoreValues(){
        System.out.println("Dev Name: " + this.getDeveloperName());
        System.out.println(String.format("LR [%f]", learnRec));
        System.out.println(String.format("RT [%f]", retationRec));
        System.out.println(String.format("Con [%f]", contributionRatio));
        System.out.println(String.format("CS [%f]", consistencyRatio));
        System.out.println(String.format("TR [%f]", turnoverRec));
        System.out.println("--------------------------------");
    }

    public double getTurnoverRec() {
        return turnoverRec;
    }
    public void setTurnoverRec(double turnoverRec) {
        this.turnoverRec = turnoverRec;
    }
    public double getLearnRec() {
        return learnRec;
    }
    public void setLearnRec(double learnRec) {
        this.learnRec = learnRec;
    }
    public double getRetationRec() {
        return retationRec;
    }
    public void setRetationRec(double retationRec) {
        this.retationRec = retationRec;
    }
    public double getContributionRatio() {
        return contributionRatio;
    }
    public void setContributionRatio(double contributionRatio) {
        this.contributionRatio = contributionRatio;
    }
    public double getConsistencyRatio() {
        return consistencyRatio;
    }
    public void setConsistencyRatio(double consistencyRatio) {
        this.consistencyRatio = consistencyRatio;
    }
    public String getDeveloperName() {
        return developerName;
    }
    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }
    public Set<String> getFileReviewedList() {
        return fileReviewedList;
    }
    public void setFileReviewedList(Set<String> fileReviewedList) {
        this.fileReviewedList = fileReviewedList;
    }
    public Set<String> getFileCommitList() {
        return fileCommitList;
    }
    public void setFileCommitList(Set<String> fileCommitList) {
        this.fileCommitList = fileCommitList;
    }
    public Set<String> getFileReviewCommitList() {
        return fileReviewCommitList;
    }
    public void setFileReviewCommitList(Set<String> fileReviewCommitList) {
        this.fileReviewCommitList = fileReviewCommitList;
    }
    public Set<String> getCommitReviewListPastYear() {
        return commitReviewListPastYear;
    }
    public void setCommitReviewListPastYear(Set<String> commitReviewListPastYear) {
        this.commitReviewListPastYear = commitReviewListPastYear;
    }
    public Set<String> getActivityMonthLastYear() {
        return activityMonthLastYear;
    }
    public void setActivityMonthLastYear(Set<String> activityMonthLastYear) {
        this.activityMonthLastYear = activityMonthLastYear;
    }


    

}
