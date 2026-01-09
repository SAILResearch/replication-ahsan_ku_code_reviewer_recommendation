package com.sail.java.exam.repository.history;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sail.evaluatingevaluator.diff.GitDiff;
import com.sail.github.model.GitCommitModel;
import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.ShellUtil;

public class ChildRepositoryReleaseLevelCommitAnalyzer implements Runnable {

	ArrayList<String> projectList;
	int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

	GitDiff gitDiff = new GitDiff();
	String gitRepoTestPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTemp/GitReposistories/";

	public void extractingLineInformation(String projectFullName) {
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		String projectRepoName = projectFullName;
		String projectRepoPath = gitRepoTestPath + projectRepoName + "/";

		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);

		final long startTimeForProject = System.currentTimeMillis();
		int finishProcessing = 0;
		
		String commandHeadCommit[] = { "git", "log", "-1","--oneline","--pretty='%H'"};
		String headerCommitId = ShellUtil.runCommand(projectRepoPath, commandHeadCommit).replace("'", "").trim();
		
		String commandCheckoutHeader[] = { "git", "checkout", "master" };
		String headerCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckoutHeader);
		
		System.out.println("Header commit Id: " + headerCommitId + "  Total Commit: " + gitCommitList.size());
		
		String directoryPath = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/";
		Path pathInfo = Paths.get(directoryPath);
		if(!Files.isDirectory(pathInfo)){
			String commandDirMaking[] = { "mkdir", ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName  + "/" };
			String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandDirMaking);
			System.out.println("Directory is created.");
		}else{
			System.out.println("Directory is already created.");
		}
		
		for (int i = gitCommitList.size() - 1; i >= gitCommitList.size() - 10; i--) {
			++finishProcessing;

			final long startTime = System.currentTimeMillis();

			GitCommitModel currentCommit = gitCommitList.get(i);

			// System.out.println("Commit Id: " + currentCommit.getCommitId());

			String commandCheckout[] = { "git", "checkout", currentCommit.getCommitId() };
			String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
			// System.out.println("Output: " + outputCheckout);

			String commandGitStatus[] = { "git", "status" };
			String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
			// System.out.println("Output Status: " + outputStatus);

			JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
			String fileTag = projectRepoName + "-" + currentCommit.getCommitId() + "-" + currentCommit.getCommitCommitterDate()
					+ ".csv";
			String outputFileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" + fileTag;

			try {
				ob.startReleaseLevelChangeAnalysis("",projectRepoPath, projectRepoName, outputFileLocation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// break;
			final long endTime = System.currentTimeMillis();
			long durationSecond = (endTime - startTime) / 1000;
			System.out.println("Finish: [" + finishProcessing + "/" + gitCommitList.size() + "]" + "["
					+ outputFileLocation + "]" + "  Execution time: " + durationSecond + " seconds");

		}
		String commandCheckout[] = { "git", "checkout", "master" };
		String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
		String commandGitStatus[] = { "git", "status" };
		String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
		System.out.println("Output Status: " + outputStatus);
		
		final long endTimeForProject = System.currentTimeMillis();
		long durationSecond = (endTimeForProject - startTimeForProject) / 1000;
		System.out
				.println("[Finish Checkout]"+ "[TH:"+this.threadNo+"]" + "[" + projectFullName + "]" + " Total execution time: " + durationSecond + " seconds");

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = startPos; i < endPos; i++) {
			String p = projectList.get(i);
			// System.out.println("Project: " + p);
			//String projectName = p.split("/")[1];
			String projectFullName = p.replace("/", "_");
			try {
				extractingLineInformation(projectFullName);
				System.out.println("[TH:"+this.threadNo+"]"+"Project: " + projectFullName + "[Done]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public ChildRepositoryReleaseLevelCommitAnalyzer(int startPos, int endPos, int threadNo) {
		projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
	}

	public ChildRepositoryReleaseLevelCommitAnalyzer() {

	}

	public static void main(String[] args) {
		ChildRepositoryReleaseLevelCommitAnalyzer ob = new ChildRepositoryReleaseLevelCommitAnalyzer();
		ob.extractingLineInformation("elastic_elasticsearch");
		System.out.println("Program finishes successfully");
	}

}
