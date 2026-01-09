package com.sail.evaluatingevaluator.diff;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.sail.evaluatingevaluator.process.ProcessUtility;

	/** Sample Input:
    diff --git a/FrameworkInfoLoader/src/com/srlab/frameworkInfo/Utility.java b/FrameworkInfoLoader/src/com/srlab/frameworkInfo/Utility.java
	index 2e7a2a7..e39e8a2 100644
	--- a/FrameworkInfoLoader/src/com/srlab/frameworkInfo/Utility.java
	+++ b/FrameworkInfoLoader/src/com/srlab/frameworkInfo/Utility.java
	@@ -11,7 +11,7 @@ import org.eclipse.jdt.core.dom.MethodInvocation;
	
	 public class Utility {
	
	-    public static String[] frameworks = { "java.awt." };
	+    public static String[] frameworks = { "javax.swing." };
	     public static String basePath = "E:\\output\\";
	     public static final String framework_full_info_path = basePath +"framework_full_info" + ".txt";
	     public static final String framework_class_info_path = basePath +"framework_class_info" + ".txt";
 	*/

public class GitDiff {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GitDiff gitDiff = new GitDiff();
		try {
			//String output = gitDiff.run("/home/parvez/research/historic_evaluation/repositories/jhotdraw","ce37253635cf47c00ea9d2f68d523e3e87a23dbd", "eb284aece205e6c8c674ab4f2767418ee469fa75");
			String oldCommit = "c7cd67762a32a8228143ba1f17939487a2224191";
			String currentCommit = "c2956e9399f2122d0d9f1fe5c44ca87cc302a54a";
			String fileName = "common/src/java/org/apache/hadoop/hive/conf/HiveConf.java";
			String repository = "/home/ahsan/Documents/Queens_PHD/Courses/Fall_2019/CISC_850/Assignment/Assignment/hive/";
			String output = gitDiff.diffMyVersion(oldCommit, currentCommit,fileName,repository);
			System.out.println("Output of gitdiff: "+output);
			
			GitDiffChangeDescriptor gitDiffChangeDescriptor = new GitDiffChangeDescriptor(output);
	
			System.out.println("Added Lines: " + gitDiffChangeDescriptor.getAddedLines());
			System.out.println("Deleted Lines: " + gitDiffChangeDescriptor.getDeletedLines());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//both old file and new file need to be a complete path
	public String diff(String oldFile, String newFile) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder("git","diff","-w","--no-color", oldFile, newFile);
		Process process = pb.start();		
		String output = ProcessUtility.output(process.getInputStream());
		int errCode = process.waitFor();
		return output;
	}
	
	//both old file and new file need to be a complete path
	public String diffMyVersion(String prevCommitId, String currentCommitId, String filePath,String repository) throws IOException, InterruptedException {
			ProcessBuilder pb = new ProcessBuilder("git","diff",prevCommitId,currentCommitId, filePath);
			pb.directory(new File(repository));
			Process process = pb.start();		
			String output = ProcessUtility.output(process.getInputStream());
			int errCode = process.waitFor();
			return output;
	}
	
	//you can diff two different version of the same file
	public String diff(String repositoryPath,String oldSHA, String newSHA, String relativeFilePath) throws IOException, InterruptedException {
		//System.out.println("git"+" diff"+" -w"+" --no-color "+ oldSHA+" "+newSHA+" "+relativeFilePath);
		ProcessBuilder pb = new ProcessBuilder("git","diff","-w","--no-color", oldSHA, newSHA, "--",relativeFilePath);
		pb.directory(new File(repositoryPath));
		//System.out.println("diff: "+pb.command());
		
		Process process = pb.start();		
		String output = ProcessUtility.output(process.getInputStream());
		int errCode = process.waitFor();
		return output;
	}
	
	//we can diff two different files in two different revisions as follows: git diff <revision_1>:<file_1> <revision_2>:<file_2>
	public String diff(String repositoryPath, String oldSha, String oldFile, String newSha, String newFile) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder("git","diff","-w","--no-index","--no-color",oldSha+":"+oldFile,newSha+":"+newFile);
		//System.out.println("Command: "+pb.command());
		pb.directory(new File(repositoryPath));
		Process process = pb.start();		
		String output = ProcessUtility.output(process.getInputStream());
		int errCode = process.waitFor();
		//System.out.println("Error Code: "+errCode);
		return output;
	}

}
