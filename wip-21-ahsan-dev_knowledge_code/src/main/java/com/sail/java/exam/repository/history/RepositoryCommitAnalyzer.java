package com.sail.java.exam.repository.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sail.evaluatingevaluator.diff.GitDiff;
import com.sail.evaluatingevaluator.diff.GitDiffChangeDescriptor;
import com.sail.github.model.GitCommitModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.ShellUtil;

public class RepositoryCommitAnalyzer {
	GitDiff gitDiff = new GitDiff();
	String gitRepoTestPath = "/Users/ahsan/Documents/Queens_Phd/SAIL_Lab_Works/Java_Exam_Project/GitRepositoryTest/";

	public void extractingLineInformation() {
		String projectFullName = "apache_stratos";
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		String projectRepoName = projectFullName.split("_")[1];
		String projectRepoPath = gitRepoTestPath + projectRepoName + "/";

		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);

		for (int i = gitCommitList.size() - 1; i > 0; i--) {
			
			GitCommitModel currentCommit = gitCommitList.get(i);
			GitCommitModel previousCommit = gitCommitList.get(i - 1);
			//currentCommit.printCommitInfo();
			//previousCommit.printCommitInfo();

			System.out.println("Commit Id: " + currentCommit.getCommitId());
	
			if (i != gitCommitList.size() - 1) {
				String commandCheckout[] = {"git", "checkout", currentCommit.getCommitId()};
				String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
				System.out.println("Output: " + outputCheckout);
				
			}
			
			String commandGitStatus[] = { "git", "status" };
			String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
			System.out.println("Output Status: " + outputStatus);
			
			//System.out.println("prevComId: " + previousCommit.getCommitId());
			//System.out.println("CommiId: " + currentCommit.getCommitId());

			String file = "extensions/cep/modules/stratos-cep-extension/wso2cep-3.0.0/src/main/java/org/apache/stratos/cep/extension/FaultHandlingWindowProcessor.java";

			for (String changedFilePath : currentCommit.getChangedJavaFileList()) {
				String diffCommand[] = { "git", "diff", previousCommit.getCommitId(), currentCommit.getCommitId(),
						changedFilePath };
				String output = ShellUtil.runCommand(projectRepoPath, diffCommand);
				GitDiffChangeDescriptor gitDiffChangeDescriptor = new GitDiffChangeDescriptor(output);
				List<Integer> changedLineList = gitDiffChangeDescriptor.getAddedLines();
				List<Integer> deletedLineList = gitDiffChangeDescriptor.getDeletedLines();
				// System.out.println("File: " + changedFilePath);
				// System.out.println("Added Lines: " + changedLineList);
			}
		}
		String commandCheckoutHead [] = {"git", "checkout", "master"};
		ShellUtil.runCommand(projectRepoPath, commandCheckoutHead);
	}

	public static void main(String[] args) {
		RepositoryCommitAnalyzer ob = new RepositoryCommitAnalyzer();
		ob.extractingLineInformation();
		System.out.println("Program finishes successfully");
	}

}
