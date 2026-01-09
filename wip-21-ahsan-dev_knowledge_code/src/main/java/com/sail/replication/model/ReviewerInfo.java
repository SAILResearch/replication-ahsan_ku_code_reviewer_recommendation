package com.sail.replication.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReviewerInfo {

    String projectName;
    String reviewerName;
    Set<String> prListss = new HashSet<String>();
    List<String> authorCommitList = new ArrayList<String>();

   
    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getReviewerName() {
        return reviewerName;
    }


    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }


    public Set<String> getPrListss() {
        return prListss;
    }


    public void setPrListss(Set<String> prListss) {
        if(prListss == null){
            this.prListss = new HashSet<String>();
        }else{
            this.prListss = prListss;
        }
        
    }


    public List<String> getAuthorCommitList() {
        return authorCommitList;
    }


    public void setAuthorCommitList(List<String> authorCommitList) {
        if(authorCommitList == null){
            this.authorCommitList = new ArrayList<String>();
        }else{
            this.authorCommitList = authorCommitList;
        }
        
    }


    public static void main(String[] args) {
        
    }
}
