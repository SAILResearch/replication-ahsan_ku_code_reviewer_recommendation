package com.sail.replication.model;

import java.util.List;
import java.util.Map;

import com.sail.util.DateUtil;
import com.sail.util.FileUtil;
import com.sail.util.TextUtil;

import org.joda.time.DateTime;

public class PullRequestModel {

    public String projectName;
    public String gitRepoName;
    public String gitProjName;
    public String prUrl;
    public String prNumber;
    public String prState;
    public String prMergeCommitId;
    public String prTitle;
    public String prBody;
    public String prCreatedAt;
    public String prClosedAt;
    public String prMergedAt;
    public String prUpdatedAt;
    public String prCreaterGitLoginName;
    


    public DateTime prCreatedJodaTime;
    public DateTime prClosedJodaTime;
    public DateTime prMergedJodaTime;
    public DateTime prUpdatedJodaTime;    

    

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




    public String getGitProjName() {
        return gitProjName;
    }




    public void setGitProjName(String gitProjName) {
        this.gitProjName = gitProjName;
    }




    public String getPrUrl() {
        return prUrl;
    }




    public void setPrUrl(String prUrl) {
        this.prUrl = prUrl;
    }




    public String getPrNumber() {
        return prNumber;
    }




    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }




    public String getPrMergeCommitId() {
        return prMergeCommitId;
    }




    public void setPrMergeCommitId(String prMergeCommitId) {
        this.prMergeCommitId = prMergeCommitId;
    }




    public String getPrTitle() {
        return prTitle;
    }




    public void setPrTitle(String prTitle) {
        this.prTitle = prTitle;
    }




    public String getPrBody() {
        return prBody;
    }




    public void setPrBody(String prBody) {
        this.prBody = prBody;
    }




    public String getPrCreatedAt() {
        return prCreatedAt;
    }




    public void setPrCreatedAt(String prCreatedAt) {
        this.prCreatedAt = prCreatedAt;
        if(prCreatedAt.trim().length() > 0 && this.prCreatedJodaTime == null){
            DateTime prCreatedJodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(prCreatedAt.replace("T", " "));
            this.setPrCreatedJodaTime(prCreatedJodaTime);
        }
    }




    public String getPrClosedAt() {
        return prClosedAt;
    }




    public void setPrClosedAt(String prClosedAt) {
        this.prClosedAt = prClosedAt;
        if(prClosedAt.trim().length() > 0 && this.prClosedJodaTime == null){
            DateTime jodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(prClosedAt.replace("T", " "));
            this.setPrClosedJodaTime(jodaTime);
        }else{
            this.setPrClosedJodaTime(null);
        }
    }




    public String getPrMergedAt() {
        return prMergedAt;
    }




    public void setPrMergedAt(String prMergedAt) {
        this.prMergedAt = prMergedAt;
        if(prMergedAt.trim().length() > 0 && this.prMergedJodaTime == null){
            DateTime jodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(prMergedAt.replace("T", " "));
            this.setPrMergedJodaTime(jodaTime);
        }
    }




    public String getPrUpdatedAt() {
        return prUpdatedAt;
    }




    public void setPrUpdatedAt(String prUpdatedAt) {
        this.prUpdatedAt = prUpdatedAt;
        if(prUpdatedAt.trim().length() > 0 && this.prUpdatedJodaTime == null){
            DateTime jodaTime = DateUtil.gitHubDateFormatterWithZone.parseDateTime(prUpdatedAt.replace("T", " "));
            this.setPrUpdatedJodaTime(jodaTime);
        }
    }




    public String getPrCreaterGitLoginName() {
        return prCreaterGitLoginName;
    }




    public void setPrCreaterGitLoginName(String prCreaterGitLoginName) {
        this.prCreaterGitLoginName = prCreaterGitLoginName;
    }



    public DateTime getPrCreatedJodaTime() {
        return prCreatedJodaTime;
    }




    public void setPrCreatedJodaTime(DateTime prCreatedJodaTime) {
        this.prCreatedJodaTime = prCreatedJodaTime;
    }




    public DateTime getPrClosedJodaTime() {
        return prClosedJodaTime;
    }




    public void setPrClosedJodaTime(DateTime prClosedJodaTime) {
        this.prClosedJodaTime = prClosedJodaTime;
    }




    public DateTime getPrMergedJodaTime() {
        return prMergedJodaTime;
    }




    public void setPrMergedJodaTime(DateTime prMergedJodaTime) {
        this.prMergedJodaTime = prMergedJodaTime;
    }

    


    public String getPrState() {
        return prState;
    }




    public void setPrState(String prState) {
        this.prState = prState;
    }

    


    public DateTime getPrUpdatedJodaTime() {
        return prUpdatedJodaTime;
    }




    public void setPrUpdatedJodaTime(DateTime prUpdatedJodaTime) {
        this.prUpdatedJodaTime = prUpdatedJodaTime;
    }


    public void printPRInfo(){
        System.out.println("------------- [PR " + this.prNumber + "]--------------");
        System.out.println("Project: " + this.projectName);
        System.out.println("PR Number: " + this.prNumber);
        System.out.println("PR Status: " + this.getPrState());
        System.out.println("PR Created At: " + this.prCreatedJodaTime);
        System.out.println("PR Closed At: " + this.prClosedJodaTime);
        System.out.println("PR Creator Login: " + this.prCreaterGitLoginName);
    }

    public static void main(String[] args) {
        PullRequestModel ob = new PullRequestModel();
        ob.setPrCreatedAt("2020-06-26T04:56:08Z");
        System.out.println(ob.getPrCreatedJodaTime());

        String filePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_apache_hbase.csv";
        Map<String, PullRequestModel> pullRequestList = FileUtil.readPullRequestData(filePath);
        System.out.println("Total Pull Request Data: " + pullRequestList.size());

        int total = 0;
        for (String key : pullRequestList.keySet()){
            total++;
            if (total > 10) break;
            pullRequestList.get(key).printPRInfo();
        }


        String changeFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/apache_hbase_files.csv";
        Map<String, List<String>> changedFileList = FileUtil.readPullRequestChangedFiles(changeFilePath);
        System.out.println("Total Pull Request: " + changedFileList.size());
        System.out.println("Total Pull 2 File Changes: " + changedFileList.get("2").size());
        //System.out.println("Pull 2 Changed Files: " + TextUtil.convertListToString(changedFileList.get("2"), "-"));
    }
}
