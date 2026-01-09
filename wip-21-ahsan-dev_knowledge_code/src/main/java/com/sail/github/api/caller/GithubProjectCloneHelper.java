package com.sail.github.api.caller;

import java.util.ArrayList;
import java.util.Arrays;

import com.csvreader.CsvReader;
import com.sail.util.ShellUtil;

public class GithubProjectCloneHelper {
	
	ArrayList<String> studiedProjectList = new ArrayList<String>();
	
	public void readStudiedProjectData() throws Exception{
		CsvReader reader = new CsvReader("/home/local/SAIL/ahsan/Documents/Pinky/Documents/Java_Exam_Work/Data/Studied_Project_List_200.csv");
		reader.readHeaders();
		while(reader.readRecord()){
			String repoisotryName = reader.get("repository");
			studiedProjectList.add(repoisotryName);
		}
	}
	
	public void cloneGitRepository() {
		
		final long initialStartTime = System.currentTimeMillis();
		
		for(int i = 165 ; i < studiedProjectList.size() ; i ++ ){
			final long startTime = System.currentTimeMillis();
			System.out.println("Working.. " + studiedProjectList.get(i));
			String projectName = studiedProjectList.get(i);
			String gitHubProjectUrl = "https://github.com/" + projectName + ".git";
			String outputDirectory = "/scratch/ahsan/Java_Exam_Work/GitRepositories/" + projectName.replace("/", "_").trim() + "/";
			String command [] = {"git","clone",gitHubProjectUrl,outputDirectory};
			ShellUtil.runCommand("/scratch/ahsan/Java_Exam_Work/GitRepositories/", command);
			
			final long endTime = System.currentTimeMillis();
			long durationSecond = (endTime - startTime) / 1000;
			
			System.out.println("Finish ["+i+"] ["+projectName+"] Time: [" + durationSecond + "] seconds");
		}
		
		final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - initialStartTime) / 1000;
		
		System.out.println("[Finish All] Total Time: [" + durationSecond + "] seconds");
		
		
		//runShellCommand(command);
	}

	public static void main(String[] args) throws Exception {
		GithubProjectCloneHelper ob = new GithubProjectCloneHelper();
		ob.readStudiedProjectData();
		ob.cloneGitRepository();
		System.out.println("Program finishes successfully");
	}
}
