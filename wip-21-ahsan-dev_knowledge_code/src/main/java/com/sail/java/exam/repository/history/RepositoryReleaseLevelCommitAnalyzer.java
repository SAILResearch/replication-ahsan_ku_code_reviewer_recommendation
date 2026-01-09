package com.sail.java.exam.repository.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sail.evaluatingevaluator.diff.GitDiff;
import com.sail.evaluatingevaluator.diff.GitDiffChangeDescriptor;
import com.sail.github.model.GitCommitModel;
import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.ShellUtil;

public class RepositoryReleaseLevelCommitAnalyzer {
	GitDiff gitDiff = new GitDiff();
	String gitRepoTestPath = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/GitRepositoryTest/";

	public void extractingLineInformation() {
		String projectFullName = "apache_stratos";
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		String projectRepoName = projectFullName.split("_")[1];
		String projectRepoPath = gitRepoTestPath + projectRepoName + "/";

		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		
		final long startTimeForProject = System.currentTimeMillis();
		int finishProcessing = 0;
		for (int i = gitCommitList.size() - 1; i >= 0; i--) {
			++finishProcessing;
	
			final long startTime = System.currentTimeMillis();
			
			GitCommitModel currentCommit = gitCommitList.get(i);
			
			System.out.println("Commit Id: " + currentCommit.getCommitId());
			
			String commandCheckout[] = {"git", "checkout", currentCommit.getCommitId()};
			String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
			System.out.println("Output: " + outputCheckout);
			
			String commandGitStatus[] = { "git", "status" };
			String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
			System.out.println("Output Status: " + outputStatus);
		
			JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
			String fileTag = projectRepoName + "-" + currentCommit.getCommitId() +"-" + currentCommit.getCommitCommitterDate() + ".csv";
			String outputFileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" + fileTag ;
			
			try {
				ob.startReleaseLevelChangeAnalysis("",projectRepoPath,projectRepoName,outputFileLocation);
			}catch (Exception e) {
				e.printStackTrace();
			}
			//break;
			final long endTime = System.currentTimeMillis();
			long durationSecond = (endTime - startTime) / 1000;
			System.out.println("Finish: [" + finishProcessing + "/" + gitCommitList.size() + "]" + "["+ outputFileLocation +"]" + "  Execution time: " + durationSecond + " seconds");
		
		}
		final long endTimeForProject = System.currentTimeMillis();
		long durationSecond = (endTimeForProject - startTimeForProject) / 1000;
		System.out.println("Finish: ["+ projectFullName +"]" + " Total execution time: " + durationSecond + " seconds");
	
	}

	public static void main(String[] args) {
		RepositoryReleaseLevelCommitAnalyzer ob = new RepositoryReleaseLevelCommitAnalyzer();
		ob.extractingLineInformation();
		System.out.println("Program finishes successfully");
	}

}
