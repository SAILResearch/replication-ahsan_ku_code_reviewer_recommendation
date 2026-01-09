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
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.CHREVRecommendScoreModel;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class CHREVReplication {
    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();

    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    public CHREVRecommendScoreModel calculateCommentContributor(ReviwerRecommendationDataLoader dataModel,
    String prNumberTesting,
     List<String> changeFileListTesting,
    DateTime prDateTimeTesting,
    String projectName){

        List<String> trainingPRList = dataModel.getTrainTestSplits().getFullPullRequestList();
        Set<String> reviewerListTesting = new HashSet<String>();
        Set<String> changeFileSetTesting = new HashSet<String>();

        Map<String,ArrayList<DateTime>> fullCommentsToFileList = new HashMap<String, ArrayList<DateTime>>();
        Map<String,Set<String>> fullWorkDay = new HashMap<String, Set<String>>();
        Map<String, Map<String, ArrayList<DateTime>>> reviewCommentFileList = new HashMap<String, Map<String, ArrayList<DateTime>>>();
        Map<String, Map<String, Set<String>>> reviewerWorkDayToFile = new HashMap<String, Map<String, Set<String>>>();

        Map<String, DateTime> fullCommentRecentWorkDay = new HashMap<String,DateTime>();
        Map<String, Map <String, DateTime>> reviewerRecentWorkDay = new HashMap<String, Map <String, DateTime>>();

        Map<String,Map<String, Double>> commentContributionRatio = new HashMap<String, Map<String, Double>>();
        Map<String,Map<String, Double>> workDayContributionRatio = new HashMap<String, Map<String, Double>>();
        Map<String,Map<String, Double>> recentWorkDayContributionRatio = new HashMap<String, Map<String, Double>>();
        Map<String, Map<String, Double>> workDayRatios = new HashMap<String, Map<String, Double>>();

        for(String revName : dataModel.getPullRequestReviewerMap().get(prNumberTesting)){
            reviewerListTesting.add(revName);
        }
        for(String changeFile: changeFileListTesting){
            changeFileSetTesting.add(changeFile);
        }

        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);

        for (String prNumber : trainingPRList) {

            ArrayList<ReviewerRankingModel> rankReviewersTraining = new ArrayList<ReviewerRankingModel>();
            List<String> reviewerList = dataModel.getPrOnlyReviewerInfo().get(prNumber);
            Set<String> devAdded = new HashSet<String>();
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            String trainKey = projectName + "-" + prNumber;

            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            if (prModel.getPrCreatedJodaTime().isAfter(prDateTimeTesting)) {
                break;
            }
            for (String changeFile : dataModel.getPrChangedFileList().get(prNumber)) {
                if (changeFileSetTesting.contains(changeFile)) {
                    if (dataModel.getReviewerComments().containsKey(changeFile)) {
                        if (dataModel.getReviewerComments().get(changeFile).containsKey(prNumber)) {
                            Map<String, ArrayList<DateTime>> commenterDateTimeList = dataModel.getReviewerComments()
                                    .get(changeFile).get(prNumber);
                            //System.out
                              //      .println("PR Number: " + prNumber + " Commenter: " + commenterDateTimeList.size());

                            for (String commenterName : commenterDateTimeList.keySet()) {
                                for (DateTime commentDateTime : commenterDateTimeList.get(commenterName)) {
                                    if (commentDateTime.isAfter(prDateTimeTesting)) {
                                        break;
                                    }
                                    String commentTimeString = commentDateTime.getYear() + "-"
                                            + commentDateTime.getMonthOfYear() + "-" + commentDateTime.getDayOfMonth();
                                    // need to take this comment
                                    // Update Full Comment File map
                                    if (!fullCommentsToFileList.containsKey(changeFile)) {
                                        fullCommentsToFileList.put(changeFile, new ArrayList<DateTime>());
                                    }
                                    fullCommentsToFileList.get(changeFile).add(commentDateTime);

                                    // Update full work day
                                    if (!fullWorkDay.containsKey(changeFile)) {
                                        fullWorkDay.put(changeFile, new HashSet<String>());
                                    }
                                    fullWorkDay.get(changeFile).add(commentTimeString);

                                    // Recent work daay
                                    if (!fullCommentRecentWorkDay.containsKey(changeFile)) {
                                        fullCommentRecentWorkDay.put(changeFile, commentDateTime);
                                    } else {
                                        if (commentDateTime.isAfter(fullCommentRecentWorkDay.get(changeFile))) {
                                            fullCommentRecentWorkDay.put(changeFile, commentDateTime);
                                        }

                                        // Update Reviewer File Map

                                        if (!reviewCommentFileList.containsKey(changeFile)) {
                                            reviewCommentFileList.put(changeFile,
                                                    new HashMap<String, ArrayList<DateTime>>());
                                        }
                                        if (!reviewCommentFileList.get(changeFile).containsKey(commenterName)) {
                                            reviewCommentFileList.get(changeFile).put(commenterName,
                                                    new ArrayList<DateTime>());
                                        }
                                        reviewCommentFileList.get(changeFile).get(commenterName).add(commentDateTime);

                                        // Update review work day
                                        if (!reviewerWorkDayToFile.containsKey(changeFile)) {
                                            reviewerWorkDayToFile.put(changeFile, new HashMap<String, Set<String>>());
                                        }
                                        if (!reviewerWorkDayToFile.get(changeFile).containsKey(commenterName)) {
                                            reviewerWorkDayToFile.get(changeFile).put(commenterName,
                                                    new HashSet<String>());
                                        }
                                        reviewerWorkDayToFile.get(changeFile).get(commenterName).add(commentTimeString);

                                        // Update review recent day

                                        if (!reviewerRecentWorkDay.containsKey(changeFile)) {
                                            reviewerRecentWorkDay.put(changeFile, new HashMap<String, DateTime>());
                                        }
                                        if (!reviewerRecentWorkDay.get(changeFile).containsKey(commenterName)) {
                                            reviewerRecentWorkDay.get(changeFile).put(commenterName, commentDateTime);
                                        }
                                        if (commentDateTime
                                                .isAfter(reviewerRecentWorkDay.get(changeFile).get(commenterName))) {
                                            reviewerRecentWorkDay.get(changeFile).put(commenterName, commentDateTime);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Finish Training
        for(String changeFile : changeFileListTesting){
            if (!fullCommentsToFileList.containsKey(changeFile)){
                continue;
            }
            // Sum full Comment
            double fullCommentsValueForAllReviewersForThisFile = fullCommentsToFileList.get(changeFile).size();
            double fullWorkDayForThisFile = fullWorkDay.get(changeFile).size();
            DateTime fullRecentWorkDay = fullCommentRecentWorkDay.get(changeFile);
            if(!reviewCommentFileList.containsKey(changeFile)){
                continue;
            }
            // sum all reviewer
            for(String commenterName : reviewCommentFileList.get(changeFile).keySet()){
                double reviewerContribution = 0.0;
                double reviewerWorkDay = 0.0;
                
                if(reviewCommentFileList.get(changeFile).containsKey(commenterName)){
                    reviewerContribution = reviewCommentFileList.get(changeFile).get(commenterName).size();
                }
                double ratioCommentContrib = reviewerContribution/fullCommentsValueForAllReviewersForThisFile;

                if(reviewerWorkDayToFile.get(changeFile).containsKey(commenterName)){
                    reviewerWorkDay = reviewerWorkDayToFile.get(changeFile).get(commenterName).size();
                }
                double ratioWorkContrib = reviewerWorkDay/fullWorkDayForThisFile;

                double ratioRecentWorkContrib = 1.0;
                if(reviewerRecentWorkDay.get(changeFile).containsKey(commenterName)){
                    DateTime reviewerWorkDayRecent = reviewerRecentWorkDay.get(changeFile).get(commenterName);
                    double dayDiff = Math.abs(Days.daysBetween(reviewerWorkDayRecent, fullRecentWorkDay).getDays());
                    //System.out.println("Day Diff: " + dayDiff);
                    if(dayDiff > 0){
                        ratioRecentWorkContrib = 1 / dayDiff;
                    }
                }

                if(!commentContributionRatio.containsKey(changeFile)){
                    commentContributionRatio.put(changeFile, new HashMap<String, Double>());
                }
                if(!workDayContributionRatio.containsKey(changeFile)){
                    workDayContributionRatio.put(changeFile, new HashMap<String, Double>());
                }
                if(!recentWorkDayContributionRatio.containsKey(changeFile)){
                    recentWorkDayContributionRatio.put(changeFile, new HashMap<String, Double>());
                }
                commentContributionRatio.get(changeFile).put(commenterName, ratioCommentContrib);
                workDayContributionRatio.get(changeFile).put(commenterName, ratioWorkContrib);
                recentWorkDayContributionRatio.get(changeFile).put(commenterName, ratioRecentWorkContrib);
            }
        }
        CHREVRecommendScoreModel ob = new CHREVRecommendScoreModel();
        ob.setCommentContributionRatio(commentContributionRatio);
        ob.setWorkDayContributionRatio(workDayContributionRatio);
        ob.setRecentWorkDayContributionRatio(recentWorkDayContributionRatio);
        
        return ob;
    }
    

    public Map<String, ArrayList<ReviewerRankingModel>> testingReviewerRecommendation(){
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        return rankingResultList;
    }

    public Map<String, ArrayList<ReviewerRankingModel>> rankingResultList ;
    

    public Map<String, ArrayList<ReviewerRankingModel>> getRankingResultList() {
        return rankingResultList;
    }

    public RecommendationEvaluation recommendingReviewersWithCHREV(String projectName, String pullRequestPath,
    String pullReviewPath, String pullCommentPath, String pullFileChangPath){

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath,
                pullReviewPath, pullCommentPath, pullFileChangPath);

        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        
        rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        
        int totalPRTraining = dataModel.getTrainTestSplits().getTrainintPullRequestList().size();
        int totalPRTesting = testingPRList.size();

        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);

        Map<String, ArrayList<ReviewerRankingModel>> rankingResultListTraining = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();
        //hat
        //int trainResultThreshold =  trainTestModel.getTrainintPullRequestList().size() > 15 ? trainTestModel.getTrainintPullRequestList().size()-15 : 0;
        int trainResultThreshold =  trainTestModel.getTrainintPullRequestList().size() > 15 ? 15 : 0;

        System.out.println("THRESHOLD: " + trainResultThreshold);
        
        // Training
        for(int i = trainResultThreshold; i < trainTestModel.getTrainintPullRequestList().size(); i ++){
            String prNumber = trainTestModel.getTrainintPullRequestList().get(i);
            System.out.println("Working " + prNumber + " Remaining " + (totalPRTraining--));
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            Set<String> devAdded = new HashSet<String>();
            ArrayList<ReviewerRankingModel> rankReviewersTraining = new ArrayList<ReviewerRankingModel>();
            String trainKey = projectName + "-" + prNumber;
            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            DateTime prDateTime = prModel.getPrCreatedJodaTime();
            List<String> changeFileList = dataModel.getPrChangedFileList().get(prNumber);

            CHREVRecommendScoreModel chrevResult = calculateCommentContributor( dataModel, prNumber, changeFileList, prDateTime, projectName);
            rankingResultList.put(prNumber, new ArrayList<ReviewerRankingModel>());
            Map<String, Double> reviewerScoreList = new HashMap<String, Double>();
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
            for(String changeFile : chrevResult.getCommentContributionRatio().keySet()){
                for(String commenterName : chrevResult.getCommentContributionRatio().get(changeFile).keySet()){
                    double score = 
                    chrevResult.getCommentContributionRatio().get(changeFile).get(commenterName)
                    + chrevResult.getWorkDayContributionRatio().get(changeFile).get(commenterName) 
                    + chrevResult.getRecentWorkDayContributionRatio().get(changeFile).get(commenterName)
                    ;

                    if(!reviewerScoreList.containsKey(commenterName)){
                        reviewerScoreList.put(commenterName, 0.0);
                    }
                    reviewerScoreList.put(commenterName, reviewerScoreList.get(commenterName) + score);
                }
            }
            for(String revName : reviewerScoreList.keySet()){
                ReviewerRankingModel revScore = new ReviewerRankingModel();
                revScore.setReviewerName(revName);
                revScore.setScore(reviewerScoreList.get(revName));
                //System.out.println("SCORE: " + revScore.getScore());
                rankReviewersTraining.add(revScore);
                devAdded.add(revName);
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

        RecommendationEvaluation evaluationRestTraining = new RecommendationEvaluation(projectName);
        evaluationRestTraining.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTrainintPullRequestList().subList(trainResultThreshold, dataModel.getTrainTestSplits().getTrainintPullRequestList().size()), 
        null, rankingResultListTraining,
        "CHREV_Training");

          
        System.out.println("Project: " + projectName + " Training");
        evaluationRestTraining.printAccuracyResult();
        evaluationRestTraining.printMAP();
        trainingEvaluationList.add(evaluationRestTraining);

        totalPRTesting = testingPRList.size();

        // Testing
        for(String prNumber : testingPRList){
            System.out.println("Working " + prNumber + " Remaining " + (totalPRTesting--));
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            Set<String> devAdded = new HashSet<String>();

            String testKey = projectName + "-" + prNumber;
            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            DateTime prDateTime = prModel.getPrCreatedJodaTime();
            List<String> changeFileList = dataModel.getPrChangedFileList().get(prNumber);

            CHREVRecommendScoreModel chrevResult = calculateCommentContributor( dataModel, prNumber, changeFileList, prDateTime, projectName);
            rankingResultList.put(prNumber, new ArrayList<ReviewerRankingModel>());
            Map<String, Double> reviewerScoreList = new HashMap<String, Double>();
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
            for(String changeFile : chrevResult.getCommentContributionRatio().keySet()){
                for(String commenterName : chrevResult.getCommentContributionRatio().get(changeFile).keySet()){
                    double score = 
                    chrevResult.getCommentContributionRatio().get(changeFile).get(commenterName)
                    + chrevResult.getWorkDayContributionRatio().get(changeFile).get(commenterName) 
                    + chrevResult.getRecentWorkDayContributionRatio().get(changeFile).get(commenterName)
                    ;

                    if(!reviewerScoreList.containsKey(commenterName)){
                        reviewerScoreList.put(commenterName, 0.0);
                    }
                    reviewerScoreList.put(commenterName, reviewerScoreList.get(commenterName) + score);
                }
            }
            for(String revName : reviewerScoreList.keySet()){
                ReviewerRankingModel revScore = new ReviewerRankingModel();
                revScore.setReviewerName(revName);
                revScore.setScore(reviewerScoreList.get(revName));
                //System.out.println("SCORE: " + revScore.getScore());
                rankReviewers.add(revScore);
                devAdded.add(revName);
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
            perTestDevScore.put(testKey, rankReviewers);
            rankingResultList.put(prNumber, rankReviewers);

        }
        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        //String fileDir = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Recommendation_Rank_Analysis/chrev_replication_recommendation/" + projectName + "/";
        //evaluationRest. writeReviewerRankResultData(projectName, dataModel, rankingResultList, fileDir);

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
    public void startRecoommendation(){
        /*List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_stratos", "apache_groovy",
                "elastic_elasticsearch");*/
        
        long startTime = System.currentTimeMillis();
       
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = recommendingReviewersWithCHREV(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }

        String resultPath = RecommendationUtil.RESULT_DIR + "CHREV_Test.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //writePerTestDevScore();

        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "CHREV",actualReviewerListPR);
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "CHREV",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"CHREV", actualReviewerListPR);
        
        String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "CHREV_Training.csv";
        //RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }

    public static void main(String[] args) {
        CHREVReplication ob = new CHREVReplication();
        ob.startRecoommendation();
        System.out.println("Program finishes successfully");
    }
}
