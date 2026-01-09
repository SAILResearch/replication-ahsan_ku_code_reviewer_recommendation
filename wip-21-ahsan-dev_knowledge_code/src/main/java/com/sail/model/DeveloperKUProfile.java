package com.sail.model;

import java.util.HashMap;
import java.util.Map;

public class DeveloperKUProfile {

    public String developerName;
    public Map<String, Double> devKUprofileMap = new HashMap<String, Double>();
    public int yearOfFirstContrib;
    public int commitContributionYear;
    public int totalCommits;

    public boolean isDiff = false;
    public String diffYearString ;
    public int commitDiffs;
    public int yearExp;

    public int getCommitDiffs() {
        return commitDiffs;
    }

    public void setCommitDiffs(int commitDiffs) {
        this.commitDiffs = commitDiffs;
    }

    
    public int getYearExp() {
        return yearExp;
    }

    public void setYearExp(int yearExp) {
        this.yearExp = yearExp;
    }

    public boolean isDiff() {
        return isDiff;
    }

    public void setDiff(boolean isDiff) {
        this.isDiff = isDiff;
    }

    public String getDiffYearString() {
        return diffYearString;
    }

    public void setDiffYearString(String diffYearString) {
        this.diffYearString = diffYearString;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public int getCommitContributionYear() {
        return commitContributionYear;
    }

    public void setCommitContributionYear(int commitContributionYear) {
        this.commitContributionYear = commitContributionYear;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public Map<String, Double> getDevKUprofileMap() {
        return devKUprofileMap;
    }

    public void setDevKUprofileMap(Map<String, Double> devKUprofileMap) {
        this.devKUprofileMap = devKUprofileMap;
    }

    public int getYearOfFirstContrib() {
        return yearOfFirstContrib;
    }

    public void setYearOfFirstContrib(int yearOfFirstContrib) {
        this.yearOfFirstContrib = yearOfFirstContrib;
    }

    public static void main(String[] args) {

    }
}
