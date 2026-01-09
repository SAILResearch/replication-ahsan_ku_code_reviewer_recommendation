package com.sail.java.exam.repository.history;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sail.github.model.GitCommitModel;
import com.sail.util.FileUtil;

public class MasterRepositoryStudiedProjectAnalyzerKlaCodePaper1 {

	int numberOfThreads = 3;
	
	public void runTheWorkerAllProject() {

		String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/Result/tosem-resubmit-Aug-2022/extracted_ku_studied_project_sep_20_2022/missing.csv";
		//String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/Data/Studied_Project_List_200_short.csv";
		
		//String STUDIED_PROJECT_FILE = "/scratch/ahsan/Java_Exam_Work/Data/Studied_Project_List_Final_Temp.csv";
		ArrayList<String> projectList = FileUtil.readAnalyzedProjectName(STUDIED_PROJECT_FILE);
		
		int start = projectList.size() - 1;
		int end = -1;
		int projectNeedToAnalyze = projectList.size();
		int difference = (int) ((projectNeedToAnalyze) / numberOfThreads);

		System.out.println("Total Threads [" + numberOfThreads + "]");
		System.out.println("Total Number of commits [" + projectNeedToAnalyze + "]");
		System.out.println("Difference [" + difference + "]");

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfThreads; i++) {
			if (i == numberOfThreads - 1) {
				Runnable worker = new ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1(start, end, i);
				System.out.println(
						"*Thread [" + i + "] Start Position [" + start + "]" + " " + " End Position [" + (end) + "]");
				executor.execute(worker);
			} else {
				Runnable worker = new ChildRepositoryStudiedProjectAnalyzerKlaCodePaper1(start, start - difference, i);
				System.out.println("*Thread [" + i + "] Start Position [" + start + "]" + " " + " End Position ["
						+ (start - difference) + "]");
				start -= difference;
				executor.execute(worker);
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time [" + totalTime / (1000) + "] seconds");
		System.out.println("Total Time [" + totalTime / (1000 * 60) + "] minutes");
		System.out.println("Finished all threads");
	}

	public static void main(String[] args) throws Exception {
		MasterRepositoryStudiedProjectAnalyzerKlaCodePaper1 ob = new MasterRepositoryStudiedProjectAnalyzerKlaCodePaper1();
		// ob.runTheWorker();
		// ob.copyGitRepository("apache_lucene");
		// ob.delteCopiedGitRepository("apache_lucene");
		ob.runTheWorkerAllProject();

		System.out.println("Program finishes successfully");
	}
}
