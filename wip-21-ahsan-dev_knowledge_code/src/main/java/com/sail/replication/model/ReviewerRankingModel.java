package com.sail.replication.model;

import org.joda.time.DateTime;

public class ReviewerRankingModel {
    
    public String reviewerName;
    public Double score;
    public double devScore;
    public double reviewScore;
    public double lastTimeAccessFileScore;
    public DateTime recentAccessDate;

    
    public DateTime getRecentAccessDate() {
		return recentAccessDate;
	}


	public void setRecentAccessDate(DateTime recentAccessDate) {
		this.recentAccessDate = recentAccessDate;
	}


	public double getLastTimeAccessFileScore() {
		return lastTimeAccessFileScore;
	}


	public void setLastTimeAccessFileScore(double lastTimeAccessFileScore) {
		this.lastTimeAccessFileScore = lastTimeAccessFileScore;
	}


	public double getDevScore() {
        return devScore;
    }


    public void setDevScore(double devScore) {
        this.devScore = devScore;
    }


    public double getReviewScore() {
        return reviewScore;
    }


    public void setReviewScore(double reviewScore) {
        this.reviewScore = reviewScore;
    }


    public String getReviewerName() {
        return reviewerName;
    }


    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }


    public Double getScore() {
        return score;
    }


    public void setScore(Double score) {
        this.score = score;
    }


    public static void main(String[] args) {
        
    }
}
