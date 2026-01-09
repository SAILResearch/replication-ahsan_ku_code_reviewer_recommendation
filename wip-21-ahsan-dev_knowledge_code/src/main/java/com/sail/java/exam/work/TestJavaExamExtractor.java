package com.sail.java.exam.work;

import com.sail.util.ShellUtil;

public class TestJavaExamExtractor {
    public static void main(String args[]) throws Exception{

        final long startTime = System.currentTimeMillis();

        //apache_hive
        String commitId = "e2acd34f2566d916288a01fa4a145141c9c9ac3d";
        String projectLocation = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_4_2021/TempRepsitory/apache_hive/";
        String projectFullName = "apache_stratos";
        String outputFileLocation = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_4_2021/TestingResult/testing_apache_hive_"+commitId + ".csv";
        JavaExamTopicExtractor ob = new JavaExamTopicExtractor();

        String commandCheckout[] = { "git", "checkout", commitId };
		String outputCheckout = ShellUtil.runCommand(projectLocation, commandCheckout);

        String commandGitStatus[] = { "git", "status" };
		String outputStatus = ShellUtil.runCommand(projectLocation, commandGitStatus);
		System.out.println("Output Status: " + outputStatus);

        ob.startReleaseLevelWithDependencyChangeAnalysis(commitId, projectLocation, projectFullName, outputFileLocation);
        System.out.println("Program finishes successfully");

        String commandGitCheckoutMaster[] = { "git", "checkout", "master" };
		String outputCheckoutMasterStatus = ShellUtil.runCommand(projectLocation, commandGitCheckoutMaster);
		System.out.println("Output Status: " + outputCheckoutMasterStatus);

        final long endTime = System.currentTimeMillis();
		long durationSecond = (endTime - startTime) / 1000;

        System.out.println("Time taken: " + durationSecond + " seconds");

    }
}
