package com.modelinvest;

public class RecommendationScoreModel {
    public String reviewerName;
    public double score;
    public boolean isTrueReviewer;

    public RecommendationScoreModel(String reviewerName, double score, boolean isTrueReviewer){
        this.setReviewerName(reviewerName);
        this.setScore(score);
        this.setTrueReviewer(isTrueReviewer);
    }

    public String getReviewerName() {
        return reviewerName;
    }
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public boolean isTrueReviewer() {
        return isTrueReviewer;
    }
    public void setTrueReviewer(boolean isTrueReviewer) {
        this.isTrueReviewer = isTrueReviewer;
    }

    
}
