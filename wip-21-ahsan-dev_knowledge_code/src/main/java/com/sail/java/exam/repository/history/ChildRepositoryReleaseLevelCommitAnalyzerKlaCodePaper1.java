package com.sail.java.exam.repository.history;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.github.model.GitCommitModel;
import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

public class ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1 implements Runnable {

	public String knowledgeOutputPath = "/scratch/ahsan/Java_Exam_Work/Result/tosem-resubmit-Aug-2022/knowledge_unit_data_git/";
	public String klaSourceCodePath = "/scratch/ahsan/Java_Exam_Work/Kla_Project_Source_Code/";

	String gitRepoTestPath = "/scratch/ahsan/Java_Exam_Work/tosem-scripts/gitrepos/";

	ArrayList<String> projectList;
	int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

	ArrayList<GitCommitModel> gitCommitList;

	public void extractFromSourceCode() {
		for (int i = startPos; i > endPos; i--) {
			try {
				GitCommitModel currentCommit = gitCommitList.get(i);
				JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
				String fileTag = currentCommit.getProjectName() + "-" + currentCommit.getCommitId() + "-"
						+ currentCommit.getCommitAuthorDate() + ".csv";
				String outputFileLocation = knowledgeOutputPath + "/" + currentCommit.getProjectName() + "/"
						+ fileTag;
				String projectSourceCodePath = klaSourceCodePath + currentCommit.getProjectName().split("_")[1].trim()
						+ "-" + currentCommit.getReleaseTagName();
				String projectRepoName = currentCommit.getProjectName().split("_")[1].trim() + "-"
						+ currentCommit.getReleaseTagName();
				try {
					ob.startReleaseLevelWithDependencyChangeAnalysis(currentCommit.getCommitId(), projectSourceCodePath,
							projectRepoName,
							outputFileLocation);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("[TH:" + this.threadNo + "]" + "Project: " + fileTag + "[Done] [" + i + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void extractFromGitRepo(GitCommitModel currentCommit) {
		String projectRepoName = currentCommit.getProjectName();
		String projectRepoPath = gitRepoTestPath + projectRepoName + "/";

		final long startTime = System.currentTimeMillis();
		int finishProcessing = 0;

		String commandHeadCommit[] = { "git", "log", "-1", "--oneline", "--pretty='%H'" };
		String headerCommitId = ShellUtil.runCommand(projectRepoPath, commandHeadCommit).replace("'", "").trim();

		System.out.println("Header Commit ID: " + headerCommitId + " " + projectRepoName + "-"
				+ currentCommit.getReleaseTagName() + " " + gitCommitList.size());
		String commandCheckoutHeader[] = { "git", "checkout", headerCommitId };
		String outputCheckoutHeader = ShellUtil.runCommand(projectRepoPath, commandCheckoutHeader);

		System.out.println("Header commit Id: " + headerCommitId + "  Total Commit: " + gitCommitList.size());

		String commandCheckout[] = { "git", "checkout", currentCommit.getCommitId() };
		String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);

		JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
		String fileTag = currentCommit.getProjectName() + "-" + currentCommit.getCommitId() + "-"
				+ currentCommit.getCommitAuthorDate() + ".csv";
		String outputFileLocation = knowledgeOutputPath + "/" + currentCommit.getProjectName() + "/"
				+ fileTag;
		try {
			ob.startReleaseLevelWithDependencyChangeAnalysis(currentCommit.getCommitId(), projectRepoPath,
					projectRepoName,
					outputFileLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String commandCheckout1[] = { "git", "checkout", headerCommitId };
		String outputCheckout1 = ShellUtil.runCommand(projectRepoPath, commandCheckout1);
		String commandGitStatus[] = { "git", "status" };
		String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
		System.out.println("Output Status: " + outputStatus);
		

		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
		System.out.println("Finish: [" + finishProcessing + "/" + gitCommitList.size() + "]" + "["
				+ outputFileLocation + "]" + "  Execution time: " + durationSecond + " seconds");

	}

	@Override
	public void run() {

		for (int i = startPos; i > endPos; i--) {
			try {
				GitCommitModel currentCommit = gitCommitList.get(i);
				extractFromGitRepo(currentCommit);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[TH:" + this.threadNo + "]" + "[Done]");

	}

	public ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1(int startPos, int endPos, int threadNo) {

		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
		String commitDataPath = "/scratch/ahsan/Java_Exam_Work/Data/Kla_Projects_Commit_Data.csv";
		this.gitCommitList = FileUtil.readCommitInformationMapPerProjectKLAProject(commitDataPath);

	}

	public ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1() {

	}

	public static void main(String[] args) {
		ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1 ob = new ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1();
		// ob.extractingLineInformation(0);
		System.out.println("Program finishes successfully");
	}

}
