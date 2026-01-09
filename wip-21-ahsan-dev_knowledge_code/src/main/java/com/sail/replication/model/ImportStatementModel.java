package com.sail.replication.model;

import java.util.List;

public class ImportStatementModel {
    public String projectName;
    public String prId;
    public String fileName;
    public String commitId;
    public List<String>importList;
    public String importString;
    
    
    
    public String getPrId() {
        return prId;
    }
    public void setPrId(String prId) {
        this.prId = prId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public List<String> getImportList() {
        return importList;
    }
    public void setImportList(List<String> importList) {
        this.importList = importList;
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
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getImportString() {
        return importString;
    }
    public void setImportString(String importString) {
        this.importString = importString;
    }

    
}
