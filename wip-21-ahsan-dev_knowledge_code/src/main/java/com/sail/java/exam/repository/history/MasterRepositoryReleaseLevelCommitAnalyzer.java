package com.sail.java.exam.repository.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.NumericDataAnalysisModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class MasterRepositoryReleaseLevelCommitAnalyzer {
	
	int numberOfThreads = 15;
	
	public void runTheWorker(){
		ArrayList<String> projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		int start = 0;		
		int end = projectList.size();
		int projectNeedToAnalyze = end - start;
		
		int difference = (int)((projectNeedToAnalyze)/numberOfThreads);
		
		System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Systems ["+projectList.size()+"]");
		System.out.println("Difference ["+difference+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
		long startTime = System.currentTimeMillis();
		for(int i = 1 ; i <= numberOfThreads ; i ++ ){
			
			if( i == numberOfThreads){
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzer(start, end,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
				executor.execute(worker);	
			}else{
				Runnable worker = new ChildRepositoryReleaseLevelCommitAnalyzer(start, start+difference,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(start+difference)+"]");
				start+= difference;
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

	public static void main(String[] args) throws Exception {
		MasterRepositoryReleaseLevelCommitAnalyzer ob = new MasterRepositoryReleaseLevelCommitAnalyzer();
		ob.runTheWorker();
		
		System.out.println("Program finishes successfully");
	}
}
