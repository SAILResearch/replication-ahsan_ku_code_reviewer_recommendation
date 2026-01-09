package com.sail.replication.model;

import java.util.List;
import java.util.Map;

import com.sail.util.DateUtil;
import com.sail.util.FileUtil;

import org.joda.time.DateTime;

public class PullRequestReviewCommentModel {
    public String projectName;
    public String gitRepoName;
    public String gitProjectName;
    public String prHtmlUrl;
    public String prNumber;
    public String commentBody;
    public String commentCreatedAt;
    public String commentCreatorGitLoginName;
    public DateTime commentCreatedJodaTime;

    public String commenterName;
    public String commenterUrl;
    public String CommenterLocation;

    public boolean isDiscussionComment;

        

    public boolean isDiscussionComment() {
        return isDiscussionComment;
    }

    public void setDiscussionComment(boolean isDiscussionComment) {
        this.isDiscussionComment = isDiscussionComment;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterUrl() {
        return commenterUrl;
    }

    public void setCommenterUrl(String commenterUrl) {
        this.commenterUrl = commenterUrl;
    }

    public String getCommenterLocation() {
        return CommenterLocation;
    }

    public void setCommenterLocation(String commenterLocation) {
        CommenterLocation = commenterLocation;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGitRepoName() {
        return gitRepoName;
    }

    public void setGitRepoName(String gitRepoName) {
        this.gitRepoName = gitRepoName;
    }

    public String getGitProjectName() {
        return gitProjectName;
    }

    public void setGitProjectName(String gitProjectName) {
        this.gitProjectName = gitProjectName;
    }

    public String getPrHtmlUrl() {
        return prHtmlUrl;
    }

    public void setPrHtmlUrl(String prHtmlUrl) {
        this.prHtmlUrl = prHtmlUrl;
    }

    public String getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(String commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
        if(commentCreatedAt.trim().length() > 0 && this.commentCreatedJodaTime == null){
            DateTime jodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(commentCreatedAt.replace("T", " "));
            this.setCommentCreatedJodaTime(jodaTime);
        }
    }

    public String getCommentCreatorGitLoginName() {
        return commentCreatorGitLoginName;
    }

    public void setCommentCreatorGitLoginName(String commentCreatorGitLoginName) {
        this.commentCreatorGitLoginName = commentCreatorGitLoginName;
    }

    public DateTime getCommentCreatedJodaTime() {
        return commentCreatedJodaTime;
    }

    public void setCommentCreatedJodaTime(DateTime commentCreatedJodaTime) {
        this.commentCreatedJodaTime = commentCreatedJodaTime;
    }

    public static void main(String[] args) {
        String filePath =  "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/apache_hbase_comments.csv";
        Map<String, List<PullRequestReviewCommentModel>> prCommentList =    FileUtil.readPullReviewComment(filePath);
        System.out.println("Total PR Comments: " + prCommentList.size());
        int total = 0;
        for (String key : prCommentList.keySet()){
            total++;
            if(total > 5) break;
            for(int i = 0 ; i < prCommentList.get(key).size() ; i ++ ){
                prCommentList.get(key).get(i).printInfo();
            }
            System.out.println("----------------------");
        }
        System.out.println("Program finishes successfully");
    }

    public void printInfo(){
        System.out.println("------- [PR COMMENT " + this.getPrNumber() + "] -------------");
        System.out.println("PR Number: " + this.getPrNumber());
        System.out.println("Commenter Login: " + this.getCommentCreatorGitLoginName());
        System.out.println("Commenter Creator Time: " + this.getCommentCreatedJodaTime());

    }
}
