package com.sail.java.exam.repository.history;

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

public class MasterRepositoryReleaseLevelCommitAnalyzerII {
	
	int numberOfThreads = 5;
	String COMMIT_HISTORY_DIR = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/";
	
	String xin_Java_path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/java_xin_project.csv";
	ArrayList<String> studiedProjectList = new ArrayList<String>();
	
	public void readStudiedProjectData() throws Exception{
		CsvReader reader = new CsvReader(xin_Java_path);
		reader.readHeaders();
		while(reader.readRecord()){
			String htmlUrl = reader.get("html_url");
			studiedProjectList.add(htmlUrl);
		}
	}


	public ArrayList<GitCommitModel> wrapperCommitSelection(String projectName) {

		String pathNew = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/"
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

		//System.out.println("SELECTED COMMIT SIZE: " + selectedCommitIdList.size());
		ArrayList<GitCommitModel> finalSelectedCommitList = new ArrayList<GitCommitModel>();
		for(GitCommitModel commit : selectedNewListNew){
			if(selectedCommitIdList.contains(commit.getCommitId())){
				finalSelectedCommitList.add(commit);
			}
		}
		return finalSelectedCommitList;
	}

	public void runTheWorker(){
		String projectFullName = "elastic_elasticsearch";
		//ArrayList<String> projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		ArrayList<String> projectList = FileUtil.readAnalyzedXinProjectName(ConstantUtil.xinProjectPath);
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		//ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		ArrayList<GitCommitModel> selectedGitCommits = wrapperCommitSelection(projectFullName);
		//52544
		
		int start = selectedGitCommits.size() - 1;		
		int end = -1;
		int projectNeedToAnalyze = selectedGitCommits.size();
		
		int difference = (int)((projectNeedToAnalyze)/numberOfThreads);
		
		System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Systems ["+projectList.size()+"]");
		System.out.println("Difference ["+difference+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
		long startTime = System.currentTimeMillis();
		for(int i = 0 ; i < numberOfThreads ; i ++ ){
			if( i == numberOfThreads - 1){
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerII(start, end,i, projectFullName, true);
				System.out.println("*Thread ["+ i + "] Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
				executor.execute(worker);	
			}else{
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerII(start, start - difference,i, projectFullName, true);
				System.out.println("*Thread [" + i + "] Start Position ["+ start +"]" +" " + " End Position ["+(start - difference)+"]");
				start-= difference;
				executor.execute(worker);				
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
        }
        long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time ["+totalTime/(1000)+"] seconds");	
		System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
        System.out.println("Finished all threads");
	}	

	
	public void createDirectory(String projectName) {
		String mainDirectoryPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTest/";
		for(int i = 0 ; i < 5 ; i ++ ) {
			String directoryPath = mainDirectoryPath + projectName + "_" + i;
			Path pathInfo = Paths.get(directoryPath);
			if(!Files.isDirectory(pathInfo)){
				String commandDirMaking[] = {"mkdir", directoryPath};
				String outputCheckout = ShellUtil.runCommand(commandDirMaking);
				System.out.println("Directory is created.");
			}else{
				System.out.println("Directory is already created.");
			}
		}
		System.out.println("Directory creation done.");
	}
	
	public void copyGitRepository(String projectName) {
		//createDirectory(projectName);
		String mainDirectoryPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTest/";
		//String gitRepoPath = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/repo_data/" + projectName + "/";
		String gitRepoPath = "/home/local/SAIL/ahsan/XIN_REPOSITORY/" + projectName + "/";

		for(int i = 0 ; i < 5 ; i ++ ) {
			String directoryPath = mainDirectoryPath + projectName + "_" + i;
			Path pathInfo = Paths.get(directoryPath);
			
			if(Files.isDirectory(pathInfo)){
				String commandDirMaking[] = { "rm","-r","-f", directoryPath };
				String outputCheckout = ShellUtil.runCommand(mainDirectoryPath, commandDirMaking);
			}
			
			String commandDirMaking[] = { "cp","-r", gitRepoPath, directoryPath };
			String outputCheckout = ShellUtil.runCommand(mainDirectoryPath, commandDirMaking);
			
		}
		System.out.println("Copy Project Done..");
	}
	
	public void delteCopiedGitRepository(String projectName) {
		String mainDirectoryPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTest/";
		for(int i = 0 ; i < 5 ; i ++ ) {
			String directoryPath = mainDirectoryPath + projectName + "_" + i;
			Path pathInfo = Paths.get(directoryPath);
			String commandDirMaking[] = { "rm","-r","-f", directoryPath };
			String outputCheckout = ShellUtil.runCommand(commandDirMaking);
		}
		System.out.println("Project Delete Done..");
	}
	
	public void runTheWorkerAllProject(){
		
		//ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		ArrayList<String> projectList = FileUtil.readAnalyzedXinProjectName(ConstantUtil.xinProjectPath);
		

		long startTimeProjAnalysis = System.currentTimeMillis();
		
		//for(int k = 0 ; k < 1 ; k ++) {
		for(int k = 30 ; k < projectList.size(); k ++) {
			String projectFullName = projectList.get(k);
			/*if (!projectFullName.contains("elastic_elasticsearch")){
				continue;
			}*/
			
			
			String path = COMMIT_HISTORY_DIR + projectFullName + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
			//ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
			
			//ArrayList<GitCommitModel> selectedGitCommits = wrapperCommitSelection(projectFullName);
			

			ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
	
			System.out.println("Project: " + projectFullName + " " + selectedGitCommits.size());

			
			System.out.println("Working... " + projectFullName);
			copyGitRepository(projectFullName);
			

			int start = selectedGitCommits.size() - 1;		
			int end = -1;
			int projectNeedToAnalyze = selectedGitCommits.size();
			int difference = (int)((projectNeedToAnalyze)/numberOfThreads);
			
			System.out.println("Total Threads ["+numberOfThreads+"]");
			System.out.println("Total Number of commits ["+projectNeedToAnalyze+"]");
			System.out.println("Difference ["+difference+"]");
			
			ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
			long startTime = System.currentTimeMillis();
			for(int i = 0 ; i < numberOfThreads ; i ++ ){
				if( i == numberOfThreads - 1){
					Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerII(start, end,i, projectFullName, true);
					System.out.println("*Thread ["+ i + "] Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
					executor.execute(worker);	
				}else{
					Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzerII(start, start - difference,i, projectFullName, true);
					System.out.println("*Thread [" + i + "] Start Position ["+ start +"]" +" " + " End Position ["+(start - difference)+"]");
					start-= difference;
					executor.execute(worker);				
				}
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
	        }
	        long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Total Time ["+totalTime/(1000)+"] seconds");	
			System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
	        System.out.println("Finished all threads");
	        System.out.println("[Done Project] " + projectFullName);
	        delteCopiedGitRepository(projectFullName);
			
		}
		
		long endTimeProjAnalysis   = System.currentTimeMillis();
		long totalTime = endTimeProjAnalysis - startTimeProjAnalysis;
		System.out.println("Total Time ["+totalTime/(1000)+"] seconds");	
		System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
        System.out.println("Finished all threads");

	}	
	
	public static void main(String[] args) throws Exception {
		MasterRepositoryReleaseLevelCommitAnalyzerII ob = new MasterRepositoryReleaseLevelCommitAnalyzerII();
		//ob.runTheWorker();
		//ob.copyGitRepository("apache_lucene");
		//ob.delteCopiedGitRepository("apache_lucene");
		ob.runTheWorkerAllProject();
		
		System.out.println("Program finishes successfully");
	}
}
