package com.sail.collaborator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sail.github.model.GitCommitModel;

public abstract class DataModelInterface {
    public ArrayList<GitCommitModel> JavaFileChangeGitCommitList = null;
    public ArrayList<GitCommitModel> gitCommitList = null;
    public ArrayList<GitCommitModel> selectedGitCommits = null;
    public Map<String,ArrayList<String>> authorCommitList = new HashMap<String, ArrayList<String>>();
    public Map<String, ArrayList<String>> developerChangedJavaFilesCommits = new HashMap<String, ArrayList<String>>();
    public Map<String, GitCommitModel> gitCommitListMap = null;

    public void loadData(){

    }

    public ArrayList<GitCommitModel> getJavaFileChangeGitCommitList() {
        return JavaFileChangeGitCommitList;
    }

    public void setJavaFileChangeGitCommitList(ArrayList<GitCommitModel> javaFileChangeGitCommitList) {
        JavaFileChangeGitCommitList = javaFileChangeGitCommitList;
    }

    public ArrayList<GitCommitModel> getGitCommitList() {
        return gitCommitList;
    }

    public void setGitCommitList(ArrayList<GitCommitModel> gitCommitList) {
        this.gitCommitList = gitCommitList;
    }

    public ArrayList<GitCommitModel> getSelectedGitCommits() {
        return selectedGitCommits;
    }

    public void setSelectedGitCommits(ArrayList<GitCommitModel> selectedGitCommits) {
        this.selectedGitCommits = selectedGitCommits;
    }

    public Map<String, ArrayList<String>> getAuthorCommitList() {
        return authorCommitList;
    }

    public void setAuthorCommitList(Map<String, ArrayList<String>> authorCommitList) {
        this.authorCommitList = authorCommitList;
    }

    public Map<String, ArrayList<String>> getDeveloperChangedJavaFilesCommits() {
        return developerChangedJavaFilesCommits;
    }

    public void setDeveloperChangedJavaFilesCommits(Map<String, ArrayList<String>> developerChangedJavaFilesCommits) {
        this.developerChangedJavaFilesCommits = developerChangedJavaFilesCommits;
    }

    public Map<String, GitCommitModel> getGitCommitListMap() {
        return gitCommitListMap;
    }

    public void setGitCommitListMap(Map<String, GitCommitModel> gitCommitListMap) {
        this.gitCommitListMap = gitCommitListMap;
    }

    

}