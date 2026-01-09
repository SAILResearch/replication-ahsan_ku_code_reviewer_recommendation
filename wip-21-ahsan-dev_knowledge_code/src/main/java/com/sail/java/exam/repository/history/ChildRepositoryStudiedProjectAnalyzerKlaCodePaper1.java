package com.sail.java.exam.repository.history;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

public class ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1 implements Runnable {

	String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/Result/tosem-resubmit-Aug-2022/extracted_ku_studied_project_sep_20_2022/missing.csv";
	// String STUDIED_PROJECT_FILE =
	// "/scratch/ahsan/Java_Exam_Work/Data/Studied_Project_List_200_short.csv";
	
	//public String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/Data/Studied_Project_List_Final_Temp.csv";

	public String knowledgeOutputPath = "/scratch/ahsan/Java_Exam_Work/Result/tosem-resubmit-Aug-2022/extracted_ku_studied_project_sep_20_2022/";

	String gitRepoTestPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTemp/GitReposistories/";

	ArrayList<String> projectList;
	int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

	public void extractFromGitRepo(String projectName) {
		String projectRepoPath = gitRepoTestPath + projectName + "/";

		final long startTime = System.currentTimeMillis();
		int finishProcessing = 0;

		String commandHeadCommit[] = { "git", "log", "-1", "--oneline", "--pretty='%H'" };
		String headerCommitId = ShellUtil.runCommand(projectRepoPath, commandHeadCommit).replace("'", "").trim();

		/*
		 * System.out.println("Header Commit ID: " + headerCommitId + " " +
		 * projectName);
		 * String commandCheckoutHeader[] = { "git", "checkout", headerCommitId };
		 * String outputCheckoutHeader = ShellUtil.runCommand(projectRepoPath,
		 * commandCheckoutHeader);
		 * System.out.println(outputCheckoutHeader);
		 */

		JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
		String fileTag = projectName + ".csv";
		String outputFileLocation = knowledgeOutputPath + fileTag;
		System.out.println("Working: " + projectName);
		try {
			ob.startReleaseLevelWithDependencyChangeAnalysis(headerCommitId, projectRepoPath,
					projectName,
					outputFileLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * String commandCheckout1[] = { "git", "checkout", headerCommitId };
		 * String outputCheckout1 = ShellUtil.runCommand(projectRepoPath,
		 * commandCheckout1);
		 * String commandGitStatus[] = { "git", "status" };
		 * String outputStatus = ShellUtil.runCommand(projectRepoPath,
		 * commandGitStatus);
		 * System.out.println("Output Status: " + outputStatus);
		 */

		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;
		System.out.println("Finish: [" + projectName + "]" + "["
				+ outputFileLocation + "]" + "  Execution time: " + durationSecond + " seconds");

	}

	@Override
	public void run() {

		for (int i = startPos; i > endPos; i--) {
			try {
				String projectName = this.projectList.get(i);
				extractFromGitRepo(projectName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[TH:" + this.threadNo + "]" + "[Done]");

	}

	public ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1(int startPos, int endPos, int threadNo) {

		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
		this.projectList = FileUtil.readAnalyzedProjectName(STUDIED_PROJECT_FILE);

	}

	public ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1() {

	}

	public void findMissingProjects() {
		this.projectList = FileUtil.readAnalyzedProjectName(STUDIED_PROJECT_FILE);
		List<String> missingProjects = new ArrayList<String>();
		for (String projectName : this.projectList) {
			String fileTag = projectName + ".csv";
			String outputFileLocation = knowledgeOutputPath + fileTag;
			try {
				CsvReader reader = new CsvReader(outputFileLocation);
			} catch (Exception e) {
				missingProjects.add(projectName);
				e.printStackTrace();
			}
		}
		CsvWriter writer = new CsvWriter(knowledgeOutputPath + "missing.csv");
		try {

			writer.write("project_name");
			writer.endRecord();
			for (String projectName : missingProjects) {
				writer.write(projectName);
				writer.endRecord();
			}
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void workOnMissing(){
		ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1 ob = new ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1();
		
		List<String> projectList = Arrays.asList("HubSpot_Singularity",
		"denimgroup_threadfix",
		"orhanobut_wasp",
		"embulk_embulk",
		"payara_Payara",
		"kotcrab_vis-ui",
		"google_guava",
		"itdelatrisu_opsu",
		"Stratio_flume-ingestion",
		"davidmoten_rxjava-jdbc",
		"Floens_Clover",
		"xaverW_MediathekView",
		"apache_struts",
		"xiaopansky_Spear",
		"j-easy_easy-batch",
		"RuedigerMoeller_kontraktor",
		"QuantumBadger_RedReader",
		"OpenNMS_newts",
		"immutables_immutables",
		"4pr0n_ripme",
		"kotcrab_VisSceneEditor"
		);
		for(String projectName : projectList){
			System.out.println("Working: " + projectName);
			ob.extractFromGitRepo(projectName);
		}
	}
	public static void main(String[] args) {
		ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1 ob = new ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1();
		// ob.extractingLineInformation(0);
		ob.findMissingProjects();
		
		
		System.out.println("Program finishes successfully");
	}

}
