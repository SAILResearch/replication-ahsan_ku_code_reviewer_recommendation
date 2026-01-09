package com.sail.collaborator;



import java.util.ArrayList;
import java.util.Arrays;
import com.csvreader.CsvReader;
import com.sail.util.ShellUtil;


public class ProjectCloneHelperLTC {
	
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
	
	public void cloneGitRepository() {
		
		final long initialStartTime = System.currentTimeMillis();
		
		for(int i = 57 ; i < studiedProjectList.size() ; i ++ ){
			final long startTime = System.currentTimeMillis();
			System.out.println("Working.. " + studiedProjectList.get(i));
			String htmlUrl = studiedProjectList.get(i);
			String gitHubProjectUrl = htmlUrl + ".git";
            String projectName = htmlUrl.split("/")[3] + "-" + htmlUrl.split("/")[4];
			String outputDirectory = "/home/local/SAIL/ahsan/XIN_REPOSITORY/" + projectName.replace("/", "_").trim() + "/";
			String command [] = {"git","clone",gitHubProjectUrl,outputDirectory};
			ShellUtil.runCommand("/home/local/SAIL/ahsan/XIN_REPOSITORY/", command);
			
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
		ProjectCloneHelperLTC ob = new ProjectCloneHelperLTC();
		ob.readStudiedProjectData();
		ob.cloneGitRepository();
		System.out.println("Program finishes successfully");
	}
}



