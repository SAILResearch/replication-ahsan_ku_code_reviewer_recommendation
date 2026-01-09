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

public class BaseLineDevComFreq {

    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();

    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();


    public List<String> filePathUpdater(List<String> reviewFileList){
        List<String> filePaths = new ArrayList<String>();
        for(String f : reviewFileList){
            String ff = f.substring(0, f.lastIndexOf(".java"));
            ff = ff.replace("/", ".");
            filePaths.add(ff);
        }
        return filePaths;
    }

    public RecommendationEvaluation baseModelReviewRecommendation(String projectName, String pullRequestPath, String pullReviewPath, String pullCommentPath,
            String pullFileChangPath) {

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath, pullReviewPath,
                pullCommentPath, pullFileChangPath);
        Map<Integer, DescriptiveStatistics> meanAveragePrecision = new HashMap<Integer, DescriptiveStatistics>();
        Map<String, Set<String>> reviewerReviewExperience = new HashMap<String, Set<String>>();
        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();

        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();  
        
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultListTraining = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        int totalTraining = 0;
        
        int trainResultThreshold =  trainTestModel.getTrainintPullRequestList().size() > 15 ? 15 : 0;
        System.out.println("THRESHOLD: " + trainResultThreshold);
        
        // Training Start
        for(int i = trainResultThreshold ; i  < trainTestModel.getTrainintPullRequestList().size() ; i ++){
            String prNumber = trainTestModel.getTrainintPullRequestList().get(i);
            ArrayList<ReviewerRankingModel> rankReviewersTraining = new ArrayList<ReviewerRankingModel>();
            List<String> reviewerList = dataModel.getPrOnlyReviewerInfo().get(prNumber);
            Set<String> devAdded = new HashSet<String>();
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            String trainKey = projectName + "-" + prNumber;

            List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumber));
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();

            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            DateTime prTime = prModel.getPrCreatedJodaTime();

            Map<String, ArrayList<String>> devHistory = dataModel.getAuthorCommitList();
            Map<String, Set<String>> devCommitFreq = new HashMap<String,Set<String>>();
            Set<String> devListChangeReviewFiles = new HashSet<String>();

            for(String devName : devHistory.keySet()){
                for(String commitId : devHistory.get(devName)){
                    GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
                    if(commit.getCommitJodaDate().isAfter(prTime)){
                        continue;
                    }
                    /*if(commit.getChangedJavaFileList().size() <= 0){
                        continue;
                    }*/
                    List<String> commitChangeFileList = commit.getChangedJavaFileList();
                    for (String fileName : commitChangeFileList) {
                        if(reviewFileList.contains(fileName)){
                            devListChangeReviewFiles.add(devName);
                        }
                    }
                    // we need to consider these values
                    if(!devCommitFreq.containsKey(devName)){
                        devCommitFreq.put(devName, new HashSet<String>());
                    }
                    devCommitFreq.get(devName).add(commitId);
                }
            }
            for(String devName : devCommitFreq.keySet()){
                devAdded.add(devName);
                if(!devListChangeReviewFiles.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)0.0);
                    rankReviewersTraining.add(rm);
                    continue;
                }
                ReviewerRankingModel rm = new ReviewerRankingModel();
                rm.setReviewerName(devName);
                rm.setScore((double)devCommitFreq.get(devName).size());
                rankReviewersTraining.add(rm);
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
               perTrainDevScore.put(trainKey,rankReviewersTraining);
        }

        RecommendationEvaluation evaluationRestTraining = new RecommendationEvaluation(projectName);
        evaluationRestTraining.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTrainintPullRequestList().subList(trainResultThreshold, dataModel.getTrainTestSplits().getTrainintPullRequestList().size()), 
        null, rankingResultListTraining,
        "CF_Training");

          
        System.out.println("Project: " + projectName + " Training");
        evaluationRestTraining.printAccuracyResult();
        evaluationRestTraining.printMAP();
        trainingEvaluationList.add(evaluationRestTraining);


        // Testing start
        for(String prTest : trainTestModel.getTestingPullRequestList()){
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prTest);
            Set<String> devAdded = new HashSet<String>();
            String testKey = projectName + "-" + prTest;
            List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prTest));
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();

            PullRequestModel prModel = dataModel.getPullRequestList().get(prTest);
            DateTime prTime = prModel.getPrCreatedJodaTime();

            Map<String, ArrayList<String>> devHistory = dataModel.getAuthorCommitList();
            Map<String, Set<String>> devCommitFreq = new HashMap<String,Set<String>>();
            Set<String> devListChangeReviewFiles = new HashSet<String>();

            for(String devName : devHistory.keySet()){
                for(String commitId : devHistory.get(devName)){
                    GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
                    if(commit.getCommitJodaDate().isAfter(prTime)){
                        continue;
                    }
                    /*if(commit.getChangedJavaFileList().size() <= 0){
                        continue;
                    }*/
                    List<String> commitChangeFileList = commit.getChangedJavaFileList();
                    for (String fileName : commitChangeFileList) {
                        if(reviewFileList.contains(fileName)){
                            devListChangeReviewFiles.add(devName);
                        }
                    }
                    // we need to consider these values
                    if(!devCommitFreq.containsKey(devName)){
                        devCommitFreq.put(devName, new HashSet<String>());
                    }
                    devCommitFreq.get(devName).add(commitId);
                }
            }
            for(String devName : devCommitFreq.keySet()){
                devAdded.add(devName);
                if(!devListChangeReviewFiles.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)0.0);
                    rankReviewers.add(rm);
                    continue;
                }
                ReviewerRankingModel rm = new ReviewerRankingModel();
                rm.setReviewerName(devName);
                rm.setScore((double)devCommitFreq.get(devName).size());
                rankReviewers.add(rm);
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
        }

        

        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTestingPullRequestList(), 
        null, rankingResultList,
        ConstantUtil.REVIEW_COMMIT_EXP);
        // Now we are going to test
        
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
        String resultPath = RecommendationUtil.RESULT_DIR + "CF_Test.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        
        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "CF",actualReviewerListPR);
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "CF",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"CF", actualReviewerListPR);
        
        String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "CF_Training.csv";
        RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");

    }


    public static void main(String[] args) {

        String projectName = "apache_hbase";
        String prFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
        String prChagneFilePath = ConstantUtil.scratchDataLocBackup
                + "/pull_request_changed_files/pull_request_files_csv/" + projectName + "_files.csv";
        String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/"
                + projectName + "_comments_with_discussion.csv";
        String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/"
                + projectName + "_review.csv";
     
        BaseLineDevComFreq ob = new BaseLineDevComFreq();
        ob.startRecoommendation();
        
        System.out.println("Program finishes successfully");
    }
}
