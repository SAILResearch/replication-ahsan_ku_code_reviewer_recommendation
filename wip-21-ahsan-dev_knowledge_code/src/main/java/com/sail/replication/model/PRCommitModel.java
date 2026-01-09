package com.sail.replication.model;

public class PRCommitModel {
    public String projectName;
    public String prNumber;
    public String commitId;

    public PRCommitModel(String projectName, String prNumber, String commitId){
        this.prNumber = prNumber;
        this.commitId = commitId;
        this.projectName = projectName;
    }

    
    public String getPrNumber() {
        return prNumber;
    }
    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }
    public String getCommitId() {
        return commitId;
    }
    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }


    public String getProjectName() {
        return projectName;
    }

    
}
