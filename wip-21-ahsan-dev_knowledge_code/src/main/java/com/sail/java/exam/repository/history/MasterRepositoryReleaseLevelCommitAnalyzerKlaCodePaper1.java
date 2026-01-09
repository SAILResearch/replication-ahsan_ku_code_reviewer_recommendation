package com.sail.java.exam.repository.history;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.core.runtime.Assert;
import org.joda.time.Months;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.NumericDataAnalysisModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

public class MasterRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1 {

	int numberOfThreads = 5;
	
	public void runTheWorkerAllProject() {

		String commitDataPath = "/scratch/ahsan/Java_Exam_Work/Data/Kla_Projects_Commit_Data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformationMapPerProjectKLAProject(commitDataPath);

		int start = gitCommitList.size() - 1;
		int end = -1;
		int projectNeedToAnalyze = gitCommitList.size();
		int difference = (int) ((projectNeedToAnalyze) / numberOfThreads);

		System.out.println("Total Threads [" + numberOfThreads + "]");
		System.out.println("Total Number of commits [" + projectNeedToAnalyze + "]");
		System.out.println("Difference [" + difference + "]");

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfThreads; i++) {
			if (i == numberOfThreads - 1) {
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1(start, end, i);
				System.out.println(
						"*Thread [" + i + "] Start Position [" + start + "]" + " " + " End Position [" + (end) + "]");
				executor.execute(worker);
			} else {
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1(start, start - difference, i);
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
		MasterRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1 ob = new MasterRepositoryReleaseLevelCommitAnalyzerKlaCodePaper1();
		// ob.runTheWorker();
		// ob.copyGitRepository("apache_lucene");
		// ob.delteCopiedGitRepository("apache_lucene");
		ob.runTheWorkerAllProject();

		System.out.println("Program finishes successfully");
	}
}
