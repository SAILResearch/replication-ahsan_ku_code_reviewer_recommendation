package com.sail.github.api.caller;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class MasterGitHubMetaDataExtractor {
	
	
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
				Runnable worker = new GitProjectMetaDataCollectorCommitLevel(start, end,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
				executor.execute(worker);	
			}else{
				Runnable worker = new GitProjectMetaDataCollectorCommitLevel(start, start+difference,i);
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
	public static void main(String[] args) {
		MasterGitHubMetaDataExtractor ob = new MasterGitHubMetaDataExtractor();
		ob.runTheWorker();
		System.out.println("Program finishes successfully");
	}
}
