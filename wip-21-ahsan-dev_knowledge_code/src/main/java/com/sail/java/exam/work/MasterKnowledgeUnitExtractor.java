package com.sail.java.exam.work;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sail.util.FileUtil;

public class MasterKnowledgeUnitExtractor {
    String devStudiedProjectListFile = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/data/studied_project_dev_knowledge.csv";
    ArrayList<String> studiedProjectList = FileUtil.readAnalyzedProjectName(devStudiedProjectListFile);
    int numberOfThreads = 5;

    public void runThread(){
        int start = studiedProjectList.size() - 1;		
		int end = -1;
		int projectNeedToAnalyze = studiedProjectList.size();
		
		int difference = (int)((projectNeedToAnalyze)/numberOfThreads);

        System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Systems ["+studiedProjectList.size()+"]");
		System.out.println("Difference ["+difference+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
		long startTime = System.currentTimeMillis();
        for(int i = 0 ; i < numberOfThreads ; i ++ ){
			if( i == numberOfThreads - 1){
				Runnable worker = new ChildKnowledgeUnitExtractor(start, end,i);
				System.out.println("*Thread ["+ i + "] Start Position ["+ start +"]" +" " + " End Position ["+(end)+"]");
				executor.execute(worker);	
			}else{
				Runnable worker = new ChildKnowledgeUnitExtractor(start, start - difference,i);
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

    public static void main(String[] args) {
        MasterKnowledgeUnitExtractor ob = new MasterKnowledgeUnitExtractor();
        for(String project: ob.studiedProjectList){
            System.out.println(project);
        }
    }
}
