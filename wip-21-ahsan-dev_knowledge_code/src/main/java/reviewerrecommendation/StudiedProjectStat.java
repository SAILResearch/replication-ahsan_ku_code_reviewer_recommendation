package reviewerrecommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;

public class StudiedProjectStat {

    public void studiedProjectStatistics(){
        long startTime = System.currentTimeMillis();
       
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();
        try{

            CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/studied_project_stat.csv");
            writer.write("ProjectName");
            writer.write("NumberPullRequest");
            writer.write("NumberCommits");
            writer.write("NumberReviewer");
            writer.write("NumberCommitter");
            writer.write("CandidateReviewer");
            writer.endRecord();
            for(String projectName : projectList){
                System.out.println("Working ["+ projectName +"]");
                String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
                String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
                String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
                String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
                
                ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                    prReviewerPath, prCommentFilePath, prChagneFilePath);

                Set<String> candidateReviewer = new HashSet(dataModel.getReviewerToPullRequestMap().keySet()); 
                Set<String> devleoeprList = new HashSet(dataModel.getDeveloperChangedJavaFilesCommits().keySet());
                candidateReviewer.addAll(devleoeprList);
                

                writer.write(projectName);
                writer.write(Integer.toString(dataModel.getPullRequestList().size()));
                writer.write(Integer.toString(dataModel.getGitCommitList().size()));
                writer.write(Integer.toString(dataModel.getReviewerToPullRequestMap().size()));
                writer.write(Integer.toString(dataModel.getDeveloperChangedJavaFilesCommits().size()));
                writer.write(Integer.toString(candidateReviewer.size()));
                writer.endRecord();

            }

            writer.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        final long endTimeForProject = System.currentTimeMillis();
		double durationMinutes = ((endTimeForProject - startTime) / 1000)/60.0;
		System.out.println(String.format("Total execution time: [%.2f] minutes", durationMinutes));
	
        
    }
    public static void main(String[] args) {
        StudiedProjectStat ob = new StudiedProjectStat();
        ob.studiedProjectStatistics();
        System.out.println("Program finishes successfully");
    }
}
