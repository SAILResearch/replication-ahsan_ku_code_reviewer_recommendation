package com.sail.github.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.sail.util.DateUtil;

public class GitCommitModel {
	String projectName;
	String commitId;
	String authorName;
	String authorEmail;
	String committerName;
	String committerEmail;
	String commitCommitterDate;
	String commitAuthorDate;
	String commitMessage;
	String releaseTagName;
	List<String> changedJavaFileList = new ArrayList<String>();
	int noChangedFiles;
	private DateTime commitJodaDate;
	private DateTime commitAuthorJodaDate;
	boolean isCommitMerge = false;
	
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getReleaseTagName() {
		return releaseTagName;
	}

	public void setReleaseTagName(String releaseTagName) {
		this.releaseTagName = releaseTagName;
	}

	public DateTime getCommitAuthorJodaDate() {
		if(commitAuthorJodaDate == null) {
			commitAuthorJodaDate = DateUtil.gitHubDateFormatterWithZone.parseDateTime(getCommitAuthorDate());
		}
		return commitAuthorJodaDate;
	}
		
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getCommitterName() {
		return committerName;
	}

	public void setCommitterName(String committerName) {
		this.committerName = committerName;
	}

	public String getCommitterEmail() {
		return committerEmail;
	}

	public void setCommitterEmail(String committerEmail) {
		this.committerEmail = committerEmail;
	}

	public String getCommitCommitterDate() {
		return commitCommitterDate;
	}

	public void setCommitCommitterDate(String commitCommitterDate) {
		this.commitCommitterDate = commitCommitterDate;
	}

	public String getCommitAuthorDate() {
		return commitAuthorDate;
	}

	public void setCommitAuthorDate(String commitAuthorDate) {
		this.commitAuthorDate = commitAuthorDate;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public List<String> getChangedJavaFileList() {
		return changedJavaFileList;
	}


	public void setChangedJavaFileList(List<String> changedJavaFileList) {
		this.changedJavaFileList = changedJavaFileList;
	}

	public int getNoChangedFiles() {
		return noChangedFiles;
	}

	public void setNoChangedFiles(int noChangedFiles) {
		this.noChangedFiles = noChangedFiles;
	}

	public DateTime getCommitJodaDate() {
		if(commitJodaDate == null) {
			commitJodaDate = DateUtil.gitHubDateFormatterWithZone.parseDateTime(getCommitAuthorDate());
		}
		return commitJodaDate;
	}

	public void setCommitJodaDate(DateTime commitJodaDate) {
		this.commitJodaDate = commitJodaDate;
	}

	public boolean isCommitMerge() {
		return isCommitMerge;
	}

	public void setCommitMerge(boolean isCommitMerge) {
		this.isCommitMerge = isCommitMerge;
	}
	



	public void printCommitInfo() {
		System.out.println("Committer Id: " + this.getCommitId());
		System.out.println("Commit Git Date: " + this.getCommitAuthorDate());
		System.out.println("Commit Joda Date: " + this.getCommitAuthorJodaDate());
		System.out.println("Commit Message: " + this.getCommitMessage());
		System.out.println("Changed files: " + this.getChangedJavaFileList().size());
		System.out.println("Changed Java files: " + this.getChangedJavaFileList());
	}
}
