package com.sail.github.api.caller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class HeaderFixer {

	List<String> commitColumnHeaders = Arrays.asList("Project_Name","Commit_Id", "Release_Tag", "Author_Name", "Author_Email", "Committer_Name",
			"Committer_Email", "Commit_Date", "Commit_Message", "No_Changed_Files", "Changed_Java_File_List",
			"Changed_Gradle_File_List", "Chagned_Pom_File_List");
	
	ArrayList<String> projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
	String outputLocation = "/scratch/ahsan/Java_Exam_Work/Result/Result_June_30/Commit_History/";
	String oldLocation = "/scratch/ahsan/Java_Exam_Work/Result/Result_Test/Commit_History/";
	
	
	//hsz_idea-gitignore-Commits
	
	public void fixingHeaderProblem(){
		
		for(int i = 0 ; i < projectList.size() ; i ++ ){
			String p = projectList.get(i);
			String projectName = p.replace("/", "_");
			String oldFilePath = oldLocation +  projectName + "-" + "Commits.csv";
			try{
				CsvWriter writer = new CsvWriter(outputLocation + projectName + "-" + "Commits.csv");
				for(String colName : commitColumnHeaders){
					writer.write(colName);
				}
				writer.endRecord();
				CsvReader reader = new CsvReader(oldFilePath);
				reader.setSafetySwitch(false);	
				reader.readHeaders();
				while(reader.readRecord()){
					String releaseTag = reader.get("Commit_Id");
					String commitId  = reader.get("Release_Tag");
					String authorName = reader.get("Author_Name");
					String authorEmail = reader.get("Author_Email");
					String comitterName = reader.get("Committer_Name");
					String committerEmail = reader.get("Committer_Email");
					String commitDate = reader.get("Commit_Date");
					String commitMessage = reader.get("Commit_Message");
					String noChangedJavaFiles = reader.get("No_Changed_Files");
					String changedJavaFiles = reader.get("Changed_Java_File_List");
					String changeGradleFileList = reader.get("Changed_Gradle_File_List");
					String changedPomFileList = reader.get("Chagned_Pom_File_List");
					
					
					writer.write(p);
					writer.write(commitId);
					writer.write(releaseTag);
					writer.write(authorName);
					writer.write(authorEmail);
					writer.write(comitterName);
					writer.write(committerEmail);
					writer.write(commitDate);
					writer.write(commitMessage);
					writer.write(noChangedJavaFiles);
					writer.write(changedJavaFiles);
					writer.write(changeGradleFileList);
					writer.write(changedPomFileList);
					writer.endRecord();
				}
				System.out.println("[Finish] " + i + " " + p);
				writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		HeaderFixer ob = new HeaderFixer();
		ob.fixingHeaderProblem();
		System.out.println("Program finishes successfully");
	}
}
