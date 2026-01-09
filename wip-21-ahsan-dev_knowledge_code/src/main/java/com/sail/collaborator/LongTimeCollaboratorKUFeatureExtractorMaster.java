package com.sail.collaborator;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sail.github.model.GitCommitModel;
import com.sail.java.exam.repository.history.ChildRepositoryReleaseLevelCommitAnalyzerII;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

public class LongTimeCollaboratorKUFeatureExtractorMaster {

    int numberOfThreads = 10;

    public void runTheWorkerAllProject(){
		
		ArrayList<String> projectList = FileUtil.readAnalyzedXinProjectName(ConstantUtil.xinProjectPath);
		long startTimeProjAnalysis = System.currentTimeMillis();
        int start = 0;		
		int end = projectList.size();
		int projectNeedToAnalyze = projectList.size();
		int difference = (int)((projectNeedToAnalyze)/numberOfThreads);
        System.out.println("Total Threads ["+numberOfThreads+"]");
        System.out.println("Total Number of commits ["+projectNeedToAnalyze+"]");
        System.out.println("Difference ["+difference+"]");
        
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    
        long startTime = System.currentTimeMillis();
        for(int i = 0 ; i < numberOfThreads ; i ++ ){
            if( i == numberOfThreads - 1){
                Runnable worker = new LongTimeCollaboraatorKUFeatureExtractorChild(start, end,i, projectList);
                System.out.println("*Thread ["+ i + "] Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
                executor.execute(worker);	
            }else{
                Runnable worker = new LongTimeCollaboraatorKUFeatureExtractorChild(start, start + difference,i, projectList);
                System.out.println("*Thread [" + i + "] Start Position ["+ start +"]" +" " + " End Position ["+(start + difference)+"]");
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
        LongTimeCollaboratorKUFeatureExtractorMaster ob = new LongTimeCollaboratorKUFeatureExtractorMaster();
        ob.runTheWorkerAllProject();
        System.out.println("Program finishes successfully");
    }
}

