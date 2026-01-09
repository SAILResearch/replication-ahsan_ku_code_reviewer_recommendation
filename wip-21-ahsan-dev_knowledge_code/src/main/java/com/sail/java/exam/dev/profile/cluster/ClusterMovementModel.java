package com.sail.java.exam.dev.profile.cluster;

public class ClusterMovementModel {
    String projectName;
    String developerName;
    String fromLabel;
    String toLabel;
    int fromYear;
    int toYear;
    
    int fromClusterSize;
    int toClusterSize;

    int totalCommits;
    
    DevProfileWithClusterModel fromPoint;
    DevProfileWithClusterModel toPoint;

    

    public DevProfileWithClusterModel getFromPoint() {
		return fromPoint;
	}


	public void setFromPoint(DevProfileWithClusterModel fromPoint) {
		this.fromPoint = fromPoint;
	}


	public DevProfileWithClusterModel getToPoint() {
		return toPoint;
	}


	public void setToPoint(DevProfileWithClusterModel toPoint) {
		this.toPoint = toPoint;
	}


	public int getTotalCommits() {
		return totalCommits;
	}


	public void setTotalCommits(int totalCommits) {
		this.totalCommits = totalCommits;
	}



	public String getProjectName() {
        return projectName;
    }



    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }



    public String getDeveloperName() {
        return developerName;
    }



    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }



    public String getFromLabel() {
        return fromLabel;
    }



    public void setFromLabel(String fromLabel) {
        this.fromLabel = fromLabel;
    }



    public String getToLabel() {
        return toLabel;
    }



    public void setToLabel(String toLabel) {
        this.toLabel = toLabel;
    }



    public int getFromYear() {
        return fromYear;
    }



    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }



    public int getToYear() {
        return toYear;
    }



    public void setToYear(int toYear) {
        this.toYear = toYear;
    }



    public int getFromClusterSize() {
        return fromClusterSize;
    }



    public void setFromClusterSize(int fromClusterSize) {
        this.fromClusterSize = fromClusterSize;
    }



    public int getToClusterSize() {
        return toClusterSize;
    }



    public void setToClusterSize(int toClusterSize) {
        this.toClusterSize = toClusterSize;
    }



    public static void main(String[] args) {
        
    }
}
