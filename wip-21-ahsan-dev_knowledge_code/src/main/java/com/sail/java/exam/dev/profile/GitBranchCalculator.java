package com.sail.java.exam.dev.profile;

import java.util.ArrayList;

import com.csvreader.CsvWriter;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.ShellUtil;

public class GitBranchCalculator {

	String gitRepoTestPath = "/scratch/ahsan/Java_Exam_Work/GitRepositoryTemp/GitReposistories/";
	
	public void calculateGitBranch() throws Exception {
		ArrayList<String> projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		CsvWriter writer = new CsvWriter("/scratch/ahsan/Java_Exam_Work/Data/proj_branch_commit_meta.csv");
		writer.write("Project_Name");
		writer.write("No_Branches");
		writer.write("No_Commits");
		writer.endRecord();
		int complete = 0;
		for(String projectName : projectList) {
			complete = complete + 1;
			projectName = projectName.replace("/", "_");
			String projectRepoPath = gitRepoTestPath + projectName + "/";
			//String command[] = { "git", "branch", "-a","|","wc","-l"};
			String command[] = { "git", "branch", "-a"};
			String commandOutput = ShellUtil.runCommand(projectRepoPath, command);
			
			String commandCommit[] = { "git", "rev-list", "--count","HEAD"};
			String commandOutputCommit = ShellUtil.runCommand(projectRepoPath, commandCommit);
			
			int branches = commandOutput.split("\n").length;
			writer.write(projectName);
			writer.write(Integer.toString(branches));
			writer.write(commandOutputCommit);
			writer.endRecord();
			System.out.println("Complete: " + complete);
		}
		writer.close();
	}
	
	public static void main(String[] args) throws Exception{
		GitBranchCalculator ob = new GitBranchCalculator();
		ob.calculateGitBranch();
		System.out.println("Program finishes successfully");
	}
}
