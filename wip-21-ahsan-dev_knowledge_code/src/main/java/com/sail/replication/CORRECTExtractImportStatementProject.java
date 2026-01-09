package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PRCommitModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.ImportStatementExtractor;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.ShellUtil;

public class CORRECTExtractImportStatementProject {

    public void startTraversingCommitsAndExtractImport(){

        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");

        //ist<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        // "apache_storm","apache_wicket");

        //List<String> projectList = Arrays.asList("apache_hbase");

        //List<String> projectList = Arrays.asList("apache_hive");

        //List<String> projectList = Arrays.asList("elastic_elasticsearch");

        //List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        String prCommitPath = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/pull_request_commit.csv";
        Map<String, ArrayList<PRCommitModel>> prCommitList = FileUtil.readCommitPRInfo(prCommitPath);

        ImportStatementExtractor extractor = new ImportStatementExtractor();

        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            
            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                prReviewerPath, prCommentFilePath, prChagneFilePath);
            
            ArrayList<PRCommitModel> projectPRCommitList = prCommitList.get(projectName);
            Set<String> commitsInAnalysisDone = new HashSet<String>();
            
            final long startTimeForProject = System.currentTimeMillis();
		    int finishProcessing = 0;
            String projectRepoPath = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/repo_data/" + projectName + "/";
            String outputPath = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/import_statements/commit_pr_" + projectName + ".csv";
            try{
                CsvWriter writer = new CsvWriter(outputPath);
                writer.write("ProjectName");
                writer.write("PRId");
                writer.write("CommitId");
                writer.write("FileName");
                writer.write("ImportStatements");
                writer.endRecord();

                for  (int i = 0 ; i < projectPRCommitList.size() ; i ++){
                    ++finishProcessing;
                    PRCommitModel commitInfo = projectPRCommitList.get(i);
                    String commitId = commitInfo.getCommitId();
                    if(!commitsInAnalysisDone.contains(commitId)){
                        // Do this analysis
                        String commandCheckout[] = {"git", "checkout", commitId};
                        String outputCheckout = ShellUtil.runCommand(projectRepoPath, commandCheckout);
                        System.out.println("Output: " + outputCheckout);
                        Map<String,String> importStatements = extractor.extractImportStatement(projectRepoPath);
                        
                        // Write the output
                        System.out.println(String.format("Finish <%s> [%d/%d] Switch to git [%s]", projectName, finishProcessing, projectList.size(),commitId));

                        for(String fileName : importStatements.keySet()){
                            String importStatement = importStatements.get(fileName);
                            writer.write(projectName);
                            writer.write(commitInfo.getPrNumber());
                            writer.write(commitId);
                            writer.write(fileName.substring(projectRepoPath.length()));
                            writer.write(importStatement);
                            writer.endRecord();
                        }
                    }
                    commitsInAnalysisDone.add(commitId);
                }
                writer.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            final long endTimeForProject = System.currentTimeMillis();
		    double durationMinutes = ((endTimeForProject - startTimeForProject) / 1000)/60.0;
		    System.out.println(String.format("Finish: [%s] Total execution time: [%.2f] minutes", projectName, durationMinutes));
	
        }

    }

    public static void main(String[] args) {
        CORRECTExtractImportStatementProject ob = new CORRECTExtractImportStatementProject();
        ob.startTraversingCommitsAndExtractImport();
        System.out.println("Program finishes successfully");
    }
}
