package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.ImportStatementModel;
import com.sail.replication.model.PRCommitModel;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

public class CORRECTRecommender {


    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    ArrayList<GitCommitModel> gitCommitList;
    ArrayList<GitCommitModel> selectedGitCommits;
    
    public int PREVIOUS_PR_THRESHOLD = 30;

    public List<String> getPreviousPullRequest(ReviwerRecommendationDataLoader dataModel, String targetPrNumber,
        int previousThreshold){

        List<String> resultPR = new ArrayList<String>();
        List<String> selectedPR = new ArrayList<String>();
        List<String> fullPRList = dataModel.getTrainTestSplits().getFullPullRequestList();
        
        for(String prNumber : fullPRList){
            if (targetPrNumber.equals(prNumber)){
                break;
            }
            selectedPR.add(prNumber);
        }

        int selectedPRSize = selectedPR.size();
        if (selectedPRSize > previousThreshold){
            resultPR = selectedPR.subList(selectedPRSize - previousThreshold, selectedPRSize);
            //resultPR = selectedPR;
        }else{
            resultPR = selectedPR;
        }

        //esultPR = dataModel.getTrainTestSplits().getTrainintPullRequestList().subList(dataModel.getTrainTestSplits().getTrainintPullRequestList().size() - PREVIOUS_PR_THRESHOLD, dataModel.getTrainTestSplits().getTrainintPullRequestList().size());


        return resultPR;
    }

    public List<String> getLibTechToekens(List<String> importList){
        List<String> tokenList = new ArrayList<String>();
        for(String im : importList){
           String tokens[]  = im.split("\\.");
           for(String token : tokens){
                //if (token.trim().length() > 0){
                if (token.trim().length() > 0 && !token.trim().equals("*")){
                    tokenList.add(token.trim());
                }
            }
        }
        return tokenList;
    }

    public List<String> getPRChangedFileImportStatements(ReviwerRecommendationDataLoader dataModel,
    Map<String, ArrayList<ImportStatementModel>> importData, Map<String,PRCommitModel> prCommitData,
     String prNumber){
        Set<String> prImportStatements = new HashSet<String>();
        String commitId = prCommitData.get(prNumber).getCommitId();
        ArrayList<ImportStatementModel> importStatementList = importData.get(commitId);
        Set<String> changedFiles = new HashSet(dataModel.getPrChangedFileList().get(prNumber));
        for(ImportStatementModel imp : importStatementList){
            String fileName = imp.getFileName();
            if (changedFiles.contains(fileName)){
                prImportStatements.addAll(imp.getImportList());
            }
        }

        List<String> prImportList = new ArrayList<String>(prImportStatements);

        //System.out.println("PRIMPORT: " + prImportList.size() + " " + prImportStatements.size());
        return prImportList;
    }

    public Map<String, Integer> getPRTechonlogyTerms(ReviwerRecommendationDataLoader dataModel, 
        String targetPrNumber,
        Map<String, ArrayList<ImportStatementModel>> importData,
        Map<String,PRCommitModel> prCommitData){
        
        Map<String, Integer> tokenFrequency = new HashMap<String, Integer>();

        String commitId = prCommitData.get(targetPrNumber).getCommitId();
        if (importData.containsKey(commitId)) {
            // Continue working with this PR
            List<String> importList = getPRChangedFileImportStatements(dataModel, importData, prCommitData, targetPrNumber);
            List<String> tokenList = getLibTechToekens(importList);

            for (String token : tokenList) {
                if (!tokenFrequency.containsKey(token)) {
                    tokenFrequency.put(token, 0);
                }
                tokenFrequency.put(token, tokenFrequency.get(token) + 1);
            }
        }
        return tokenFrequency;
    }

    public ArrayList<ReviewerRankingModel>  getReviewerScoring(ReviwerRecommendationDataLoader dataModel, 
    String targetPrNumber,
    int previousThreshold, 
    Map<String, ArrayList<ImportStatementModel>> importData,
    Map<String,PRCommitModel> prCommitData){
        
        Map<String,Integer> currentPRTermsFrequency = getPRTechonlogyTerms(dataModel, targetPrNumber, importData, prCommitData);
        List<String> previousPRList = getPreviousPullRequest(dataModel, targetPrNumber, PREVIOUS_PR_THRESHOLD);
        ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
        
        Map<String, Double> reviewerScore = new HashMap<String, Double>();
        for(String prNumber : previousPRList){
            
            Map<String,Integer> previousPRTermsFrequency = getPRTechonlogyTerms(dataModel, prNumber, importData, prCommitData);
            double score = getCosineScore(previousPRTermsFrequency,currentPRTermsFrequency);
            List<String> reviewerList = dataModel.getPullRequestReviewerMap().get(prNumber);

            for(String reviewerName : reviewerList){
                if(!reviewerScore.containsKey(reviewerName)){
                    reviewerScore.put(reviewerName, 0.0);
                }
                reviewerScore.put(reviewerName, score);
            }
        }
        for(String reviewerName : reviewerScore.keySet()){
            ReviewerRankingModel rm = new ReviewerRankingModel();
            rm.setReviewerName(reviewerName);
            rm.setScore(reviewerScore.get(reviewerName));
            rankReviewers.add(rm);
        }
        return rankReviewers;
    }
   
    
    public double getCosineScore(Map<String,Integer> previousPRTechonlogyTermList,
    Map<String,Integer> currentPRTechnologyTermList){
        double score = 0.0;
        Set<String> fullListTerm = new HashSet(previousPRTechonlogyTermList.keySet()); 
        Set<String> currentPRTerms = new HashSet(currentPRTechnologyTermList.keySet());
        fullListTerm.addAll(currentPRTerms);

        double numerator = 0.0;
        double denominatorPrevious = 0.0;
        double denominatorCurrent = 0.0;

        for(String term : fullListTerm){
            double prev = 0.0;
            double cur = 0.0;

            if (previousPRTechonlogyTermList.containsKey(term)){
                prev = previousPRTechonlogyTermList.get(term);
            }
            if (currentPRTechnologyTermList.containsKey(term)){
                cur = currentPRTechnologyTermList.get(term);
            }

            //System.out.println(term + " Prev: " + prev + " Cur: " + cur);

            numerator = numerator + (prev * cur);
            denominatorPrevious = denominatorPrevious + (prev * prev);
            denominatorCurrent = denominatorCurrent + (cur * cur);
        }

        double denominator = Math.sqrt(denominatorPrevious) * Math.sqrt(denominatorCurrent);

        if (denominator == 0.0 ){
            score = 0.0;
        }else{
            score = numerator / denominator;
        }
        



        //System.out.println("Num :" + numerator + " Denom: " + denominator);

        return score;
    }

    public RecommendationEvaluation recommendingReviewersWithCORRECT(String projectName, String pullRequestPath,
    String pullReviewPath, String pullCommentPath, String pullFileChangPath){
    
        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath,
                pullReviewPath, pullCommentPath, pullFileChangPath);

        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        
        int totalPRTraining = dataModel.getTrainTestSplits().getTrainintPullRequestList().size();
        int totalPRTesting = testingPRList.size();

        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);

        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();
    
        int trainResultThreshold =  trainTestModel.getTrainintPullRequestList().size() > 15 ? 15 : 0;

        System.out.println("THRESHOLD: " + trainResultThreshold);

        String root = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/import_statements/";
        String importPath = String.format("%scommit_pr_%s.csv",root, projectName);
        Map<String, ArrayList<ImportStatementModel>> importData = FileUtil.readImportStatementData(importPath);
        
        String prCommitLinkPath = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/pull_request_commit.csv";
        Map<String,PRCommitModel> prCommitData = FileUtil.readCommitPRInfoWithPRKey(prCommitLinkPath).get(projectName);

        
        for (String prNumber : trainTestModel.getTestingPullRequestList()){

            String testKey = projectName + "-" + prNumber;
            
            PullRequestModel pr =  dataModel.getPullRequestList().get(prNumber);
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            
            Set<String> devAdded = new HashSet<String>();

            ArrayList<ReviewerRankingModel> rankReviewers = getReviewerScoring(dataModel, prNumber, trainResultThreshold, importData, prCommitData);
            for(ReviewerRankingModel rm : rankReviewers){
                devAdded.add(rm.getReviewerName());
            }
            for(String devName : fullDeveloperReviewerList){
                if (!devAdded.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)0.0);
                    rankReviewers.add(rm);
                }
            }

            Collections.sort(rankReviewers, new Comparator<ReviewerRankingModel>(){

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

               rankingResultList.put(prNumber, rankReviewers);
               perTestDevScore.put(testKey, rankReviewers);
        }

        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTestingPullRequestList(), 
        null, rankingResultList,
                ConstantUtil.REVIEW_CHREV);
        System.out.println("Project: " + projectName + " Testing");
        System.out.println("-----------------------");
        evaluationRest.printAccuracyResult();
        evaluationRest.printMAP();

        for(String prTest: dataModel.getTrainTestSplits().getFullPullRequestList()){
            List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
            String testKey = projectName + "-" + prTest;
            actualReviewerListPR.put(testKey, actualReviewer);
        }

        return evaluationRest;
    }

    public void collectRequiredPullRequestCommit(){

        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/pull_request_commit.csv");
        
        try {
            writer.write("ProjectName");
            writer.write("PRId");
            writer.write("CommitId");
            writer.endRecord();

            for (String projectName : projectList) {
                System.out.println("Working [" + projectName + "]");
                String prFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request/pr_reports_" + projectName
                        + ".csv";
                String prChagneFilePath = ConstantUtil.scratchDataLocBackup
                        + "/pull_request_changed_files/pull_request_files_csv/" + projectName + "_files.csv";
                String prCommentFilePath = ConstantUtil.scratchDataLocBackup
                        + "/pull_request_comments/comments_csv_files/" + projectName + "_comments_with_discussion.csv";
                String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/"
                        + projectName + "_review.csv";

                ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                        prReviewerPath, prCommentFilePath, prChagneFilePath);

                List<String> fullPRlist = dataModel.getTrainTestSplits().getFullPullRequestList();
                this.gitCommitList = dataModel.getGitCommitList();
                this.selectedGitCommits = dataModel.getSelectedGitCommits();

                for (String prNumber : fullPRlist) {
                    PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
                    GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(
                            pullRequest.getPrCreatedJodaTime(),
                            this.selectedGitCommits);
                    
                    writer.write(projectName);
                    writer.write(prNumber);
                    writer.write(gitCommit.getCommitId());
                    writer.endRecord();
                }

            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void startRecoommendation(){
        
        long startTime = System.currentTimeMillis();
       
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        
        //List<String> projectList = Arrays.asList("apache_activemq");
        

        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = recommendingReviewersWithCORRECT(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }
        
        String resultPath = RecommendationUtil.RESULT_DIR + "CORRECT.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //writePerTestDevScore();

        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "CHREV",actualReviewerListPR);
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "CORRECT",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"CORRECT", actualReviewerListPR);
        
        String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "CORRECT_Training.csv";
        //RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }
    public static void main(String[] args) {
        CORRECTRecommender ob = new CORRECTRecommender();
        //ob.collectRequiredPullRequestCommit();
        ob.startRecoommendation();
        System.out.println("Program finishes successfully");
    }
}
