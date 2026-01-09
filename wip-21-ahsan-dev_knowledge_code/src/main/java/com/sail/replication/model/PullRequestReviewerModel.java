package com.sail.replication.model;

import com.sail.util.DateUtil;

import org.joda.time.DateTime;

public class PullRequestReviewerModel {

    public String projectName;
    public String gitProjectName;
    public String gitRepoName;
    public String prNumber;
    public String reviewerGitLoginName;
    public String reviewerType;
    public String reviewSubmissionDate;
    public DateTime reviewSubmissionJodaDate;

    public String reviewerName;
    public String reviewerUrl;
    public String reviewerLocatiion;

    
    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }


    public String getReviewerUrl() {
        return reviewerUrl;
    }


    public void setReviewerUrl(String reviewerUrl) {
        this.reviewerUrl = reviewerUrl;
    }


    public String getReviewerLocatiion() {
        return reviewerLocatiion;
    }


    public void setReviewerLocatiion(String reviewerLocatiion) {
        this.reviewerLocatiion = reviewerLocatiion;
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getGitProjectName() {
        return gitProjectName;
    }


    public void setGitProjectName(String gitProjectName) {
        this.gitProjectName = gitProjectName;
    }


    public String getGitRepoName() {
        return gitRepoName;
    }


    public void setGitRepoName(String gitRepoName) {
        this.gitRepoName = gitRepoName;
    }


    public String getPrNumber() {
        return prNumber;
    }


    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }


    public String getReviewerGitLoginName() {
        return reviewerGitLoginName;
    }


    public void setReviewerGitLoginName(String reviewerGitLoginName) {
        this.reviewerGitLoginName = reviewerGitLoginName;
    }


    public String getReviewerType() {
        return reviewerType;
    }


    public void setReviewerType(String reviewerType) {
        this.reviewerType = reviewerType;
    }


    public String getReviewSubmissionDate() {
        return reviewSubmissionDate;
    }


    public void setReviewSubmissionDate(String reviewSubmissionDate) {
        this.reviewSubmissionDate = reviewSubmissionDate;
        if(reviewSubmissionDate.trim().length() > 0 && this.reviewSubmissionJodaDate == null){
            DateTime jodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(reviewSubmissionDate.replace("T", " "));
            this.setReviewSubmissionJodaDate(jodaTime);
        }
    }


    public DateTime getReviewSubmissionJodaDate() {
        return reviewSubmissionJodaDate;
    }


    public void setReviewSubmissionJodaDate(DateTime reviewSubmissionJodaDate) {
        this.reviewSubmissionJodaDate = reviewSubmissionJodaDate;
    }


    public static void main(String[] args) {
        System.out.println("Program finishes successfully");
    }
}
