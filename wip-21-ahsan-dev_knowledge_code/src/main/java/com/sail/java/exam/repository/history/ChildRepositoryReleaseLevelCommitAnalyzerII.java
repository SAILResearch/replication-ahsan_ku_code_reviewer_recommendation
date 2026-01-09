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

public class ChildRepositoryReleaseLevelCommitAnalyzerII implements Runnable {

	ArrayList<String> projectList;
	int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

	public String projectFullName;

	// String gitRepoTestPath =
	// "/scratch/ahsan/Java_Exam_Work/GitRepoExperimentMultiThread/";
	String gitRepoTestPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTest/";
	String projectRepoName;
	String projectRepoPath;
	ArrayList<GitCommitModel> gitCommitList;

	public void extractingLineInformation(int commitIndex) {

		final long startTimeForProject = System.currentTimeMillis();
		int finishProcessing = 0;

		String commandHeadCommit[] = { "git", "log", "-1", "--oneline", "--pretty='%H'" };
		String headerCommitId = ShellUtil.runCommand(projectRepoPath, commandHeadCommit).replace("'", "").trim();
		System.out.println("Header commit Id: " + headerCommitId + "  Total Commit: " + gitCommitList.size());

		final long startTime = System.currentTimeMillis();
		GitCommitModel currentCommit = gitCommitList.get(commitIndex);
		String commitId = currentCommit.getCommitId();

		// System.out.println("Commit Id: " + currentCommit.getCommitId());

		String commandCheckout[] = { "git", "checkout", currentCommit.getCommitId() };
		String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
		// System.out.println("Output: " + outputCheckout);

		JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
		String fileTag = projectRepoName + "-" + currentCommit.getCommitId() + ".csv";
		String outputFileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" + fileTag;
		
		try {
			ob.startReleaseLevelWithDependencyChangeAnalysis(commitId, projectRepoPath, projectRepoName,
					outputFileLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String commandGitStatus[] = { "git", "status" };
		String outputStatus = ShellUtil.runCommand(projectRepoPath, commandGitStatus);
		System.out.println("Output Status: " + outputStatus);

		final long endTimeForProject = System.currentTimeMillis();
		long durationSecond = (endTimeForProject - startTimeForProject) / 1000;
		System.out.println("[Finish Checkout]" + "[TH:" + this.threadNo + "]" + "[" + projectFullName + "]"
				+ " Total execution time: " + durationSecond + " seconds");

	}

	@Override
	public void run() {
		// System.out.println(">>>>>>> INSIDE " + this.threadNo);
		List<String> headSwitchCommand = Arrays.asList("master","main","develop");
		for(String headString : headSwitchCommand){
			String commandCheckoutHeader[] = { "git", "checkout", headString};
			try{
				String headerCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckoutHeader);
				break;
			}catch(AssertionError e){
				e.printStackTrace();
			}catch(Exception e){
				System.err.println("Headd switch command not work: " + headString);
			}
		}

		String directoryPath = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/";
		Path pathInfo = Paths.get(directoryPath);

		if (!Files.isDirectory(pathInfo)) {
			String commandDirMaking[] = { "mkdir",
					ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" };
			String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandDirMaking);
			System.out.println("TH [" + this.threadNo + "] Directory is created.");
		} else {
			System.out.println("TH [" + this.threadNo + "] Directory is already created.");
		}

		// TODO Auto-generated method stub
		for (int i = startPos; i > endPos; i--) {
			// String p = projectList.get(i);
			// System.out.println("Project: " + p);
			// String projectName = p.split("/")[1];
			// String projectFullName = p.replace("/", "_");
			try {
				extractingLineInformation(i);
				System.out.println("[TH:" + this.threadNo + "]" + "Project: " + projectFullName + "[Done] [" + i + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// String commandCheckout[] = { "git", "checkout", "master" };
		// String outputCheckout = ShellUtil.runCommand(projectRepoPath,
		// commandCheckout);
	}

	public ArrayList<GitCommitModel> wrapperCommitSelection(String projectName) {

		String pathNew = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/clean_result_studied_project/commit_merge_data/"
				+ projectName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitListNew = FileUtil.readCommitInformation(pathNew);
		ArrayList<GitCommitModel> selectedNewListNew = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitListNew);

		String pathOld = ConstantUtil.ROOT + "/Data/commit_merge_data/" + projectName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitListOld = FileUtil.readCommitInformation(pathOld);
		ArrayList<GitCommitModel> selectedListOld = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitListOld);

		Set<String> commitIdNew = new HashSet<String>();
		Set<String> commitIdOld = new HashSet<String>();
		Set<String> selectedCommitIdList = new HashSet<String>();

		for (GitCommitModel commit : selectedNewListNew) {
			commitIdNew.add(commit.getCommitId());
		}
		for (GitCommitModel commit : selectedListOld) {
			commitIdOld.add(commit.getCommitId());
		}

		for (String commit : commitIdNew) {
			if (!commitIdOld.contains(commit)) {
				selectedCommitIdList.add(commit);
			}
		}

		ArrayList<GitCommitModel> finalSelectedCommitList = new ArrayList<GitCommitModel>();
		for(GitCommitModel commit : selectedNewListNew){
			if(selectedCommitIdList.contains(commit.getCommitId())){
				finalSelectedCommitList.add(commit);
			}
		}
		return finalSelectedCommitList;
	}

	public ChildRepositoryReleaseLevelCommitAnalyzerII(int startPos, int endPos, int threadNo, String projectFullName,
			boolean selectedCommit) {
		//projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.xinProjectPath);
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
		this.projectFullName = projectFullName;
		this.projectRepoName = projectFullName;
		this.projectRepoPath = gitRepoTestPath + this.projectFullName + "_" + threadNo + "/";
		//String path = ConstantUtil.COMMIT_HISTORY_DIR + this.projectFullName + "_full_commit_data.csv";
		String path = ConstantUtil.XIN_COMMIT_HISTORY + this.projectFullName + "_full_commit_data.csv";

		// only selecting the selected commits
		if (selectedCommit) {
			ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
			//this.gitCommitList = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
			//this.gitCommitList = wrapperCommitSelection(projectFullName);
			this.gitCommitList =  GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);


		} else {
			this.gitCommitList = FileUtil.readCommitInformation(path);
		}

	}

	public ChildRepositoryReleaseLevelCommitAnalyzerII() {

	}

	public static void main(String[] args) {
		ChildRepositoryReleaseLevelCommitAnalyzerII ob = new ChildRepositoryReleaseLevelCommitAnalyzerII();
		ob.extractingLineInformation(0);
		System.out.println("Program finishes successfully");
	}

}
