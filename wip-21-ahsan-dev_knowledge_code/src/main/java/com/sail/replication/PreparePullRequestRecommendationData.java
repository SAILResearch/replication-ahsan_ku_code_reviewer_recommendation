package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

public class PreparePullRequestRecommendationData {
 
    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    
    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    public PreparePullRequestRecommendationData() {

    }

    public RecommendationEvaluation baseModelReviewRecommendation(String projectName, String pullRequestPath, String pullReviewPath, String pullCommentPath,
            String pullFileChangPath) {

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath, pullReviewPath,
                pullCommentPath, pullFileChangPath);
        Map<Integer, DescriptiveStatistics> meanAveragePrecision = new HashMap<Integer, DescriptiveStatistics>();
        Map<String, Set<String>> reviewerReviewExperience = new HashMap<String, Set<String>>();
        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();
        
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultListTraining = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        
        int totalTraining = 0;
        int trainResultThreshold =  trainTestModel.getTrainintPullRequestList().size() > 15 ?  15 : 0;
        System.out.println("THRESHOLD: " + trainResultThreshold);
        // Training Start
        for (String prNumber : trainTestModel.getTrainintPullRequestList()) {
            //System.out.println("Total Training: " + totalTraining);
            ArrayList<ReviewerRankingModel> rankReviewersTraining = new ArrayList<ReviewerRankingModel>();
            List<String> reviewerList = dataModel.getPrOnlyReviewerInfo().get(prNumber);
            Set<String> devAdded = new HashSet<String>();
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            String trainKey = projectName + "-" + prNumber;
            if(totalTraining >= trainResultThreshold){
                for(String devName : reviewerReviewExperience.keySet()){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)reviewerReviewExperience.get(devName).size());
                    rankReviewersTraining.add(rm);
                    devAdded.add(devName);
                }
                for(String devName : fullDeveloperReviewerList){
                    if (!devAdded.contains(devName)){
                        ReviewerRankingModel rm = new ReviewerRankingModel();
                        rm.setReviewerName(devName);
                        rm.setScore((double)0.0);
                        rankReviewersTraining.add(rm);
                    }
                }
                rankReviewersTraining.sort(new Comparator<ReviewerRankingModel>() {
                    @Override
                    public int compare(ReviewerRankingModel o1, ReviewerRankingModel o2) {
                        if(o1.getScore() < o2.getScore()){
                            return 1;
                        }else if (o1.getScore() > o2.getScore()){
                            return -1;
                        }
                        return 0;
                    }
                   });
                   rankingResultListTraining.put(prNumber, rankReviewersTraining);
                   perTrainDevScore.put(trainKey, rankReviewersTraining);
            }
            for (String reviewString : reviewerList) {
                String reviewerName = reviewString.split("!")[0].trim();
                if (!reviewerReviewExperience.containsKey(reviewerName)) {
                    reviewerReviewExperience.put(reviewerName, new HashSet<String>());
                }
                reviewerReviewExperience.get(reviewerName).add(prNumber);
            }
            ++totalTraining;
        }

        RecommendationEvaluation evaluationRestTraining = new RecommendationEvaluation(projectName);
        evaluationRestTraining.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTrainintPullRequestList().subList(trainResultThreshold, dataModel.getTrainTestSplits().getTrainintPullRequestList().size()), 
        null, rankingResultListTraining,
        "RF_Training");

          
        System.out.println("Project: " + projectName + " Training");
        evaluationRestTraining.printAccuracyResult();
        evaluationRestTraining.printMAP();
        trainingEvaluationList.add(evaluationRestTraining);
        
        // Testing Start
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();

        for(String prTest : trainTestModel.getTestingPullRequestList()){
            String testKey = projectName + "-" + prTest;
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prTest);
            Set<String> devAdded = new HashSet<String>();
            for(String devName : reviewerReviewExperience.keySet()){
                ReviewerRankingModel rm = new ReviewerRankingModel();
                rm.setReviewerName(devName);
                rm.setScore((double)reviewerReviewExperience.get(devName).size());
                rankReviewers.add(rm);
                devAdded.add(devName);
            }
            for(String devName : fullDeveloperReviewerList){
                if (!devAdded.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)0.0);
                    rankReviewers.add(rm);
                }
            }
            rankReviewers.sort(new Comparator<ReviewerRankingModel>() {
                @Override
                public int compare(ReviewerRankingModel o1, ReviewerRankingModel o2) {
                    if(o1.getScore() < o2.getScore()){
                        return 1;
                    }else if (o1.getScore() > o2.getScore()){
                        return -1;
                    }
                    return 0;
                }
               });
               rankingResultList.put(prTest, rankReviewers);
               perTestDevScore.put(testKey, rankReviewers);
               List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
               for(String reviewerName : actualReviewer){
                    if (!reviewerReviewExperience.containsKey(reviewerName)) {
                        reviewerReviewExperience.put(reviewerName, new HashSet<String>());
                    }
                    reviewerReviewExperience.get(reviewerName).add(prTest);
               }
        }

        for(String devName : fullDeveloperReviwerListPerPR.get(dataModel.getTrainTestSplits().getTestingPullRequestList().get(dataModel.getTrainTestSplits().getTestingPullRequestList().size() - 1))){
            if(!reviewerReviewExperience.containsKey(devName)){
                reviewerReviewExperience.put(devName, new HashSet<String>());
            }
        }

        //writeReviewerExperiencePerProject(projectName,reviewerReviewExperience);

        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTestingPullRequestList(), 
        null, rankingResultList,
        ConstantUtil.REVIEW_REVIEW_EXP);

          
        System.out.println("Project: " + projectName + " Testing");

        evaluationRest.printAccuracyResult();
        evaluationRest.printMAP();

        System.out.println(dataModel.getCandidateReviewerList().size()); 

        for(String prTest: dataModel.getTrainTestSplits().getFullPullRequestList()){
            List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
            String testKey = projectName + "-" + prTest;
            actualReviewerListPR.put(testKey, actualReviewer);
        }
        return evaluationRest;

    }

    public void writeReviewerExperiencePerProject(String projectName, Map<String, Set<String>> reviewerReviewExperience){
        try{
            String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/review_frequency/";
            CsvWriter writer = new CsvWriter(path + "rev_freq_" + projectName + ".csv");
            writer.write("Project_Name");
            writer.write("Dev_Name");
            writer.write("Total_Reviews");
            writer.endRecord();

            for(String devName : reviewerReviewExperience.keySet()){
                writer.write(projectName);
                writer.write(devName);
                writer.write(Integer.toString(reviewerReviewExperience.get(devName).size()));
                writer.endRecord();
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void startRecoommendation(){
        long startTime = System.currentTimeMillis();

        /*List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_stratos", "apache_groovy",
                "elastic_elasticsearch");*/
        
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();
        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = baseModelReviewRecommendation(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }
        String resultPath = RecommendationUtil.RESULT_DIR + "RF_Test.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "RF");
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "RF",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"RF", actualReviewerListPR);
        

        String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "RF_Training.csv";
        RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }
    public static void main(String[] args) {

        String projectName = "apache_hbase";
        String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + "apache_hbase" + ".csv";
        String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/apache_hbase_files.csv";
        String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/apache_hbase_comments.csv";
        String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/apache_hbase_review.csv";
        
        PreparePullRequestRecommendationData ob = new PreparePullRequestRecommendationData();
        ob.startRecoommendation();
        
        System.out.println("Program finishes successfully");
    }
}
