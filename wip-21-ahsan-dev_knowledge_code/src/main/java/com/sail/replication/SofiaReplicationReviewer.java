package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.csvreader.CsvReader;
import com.sail.github.model.GitCommitModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.replication.model.SofiaDeveloperModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

public class SofiaReplicationReviewer {

    public String DEV_SCORE_PATH = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
    public String MODELNAME = "CHREV";

    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    public Map<String,ArrayList<ReviewerRankingModel>> readData(String projectName, ReviwerRecommendationDataLoader dataModel,
    String modelName, String devScorePath){
        Map<String,ArrayList<ReviewerRankingModel>> devScoreFullData = new HashMap<String,ArrayList<ReviewerRankingModel>>();
        
        System.out.println("Start Loading Model data: " + modelName);
        String fullPath = devScorePath  + modelName + "/" + projectName + "/";
        
        List<String> testDataSet = dataModel.getTrainTestSplits().getTestingPullRequestList();
        for (String prTest : testDataSet) {
            ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
            String testKey = projectName + "-" + prTest;
            String scoreFilePath = fullPath + testKey + ".csv";
            try {
                CsvReader reader = new CsvReader(scoreFilePath);
                reader.readHeaders();
                while (reader.readRecord()) {
                    String devName = reader.get("dev_name");
                    Double score = Double.parseDouble(reader.get("score"));
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore(score);
                    rankReviewers.add(rm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            devScoreFullData.put(prTest, rankReviewers);
        }
            
        System.out.println("Complete data Read.");
        return devScoreFullData;
    }
    
    public Map<String, Set<String>> identifyFileDeveloperList(ReviwerRecommendationDataLoader dataModel,
                            DateTime lastPRTime){
        
        Map<String, Set<String>> fileDeveloperList = new HashMap<String, Set<String>>();
        ArrayList<GitCommitModel> commitList =  dataModel.getGitCommitList();
        
        for( GitCommitModel commit : commitList){

            // Only traverse commit before the last training pr
            if (commit.getCommitJodaDate().isAfter(lastPRTime.minusDays(1))) {
                continue;
            }
            List<String> commitChangeFileList = commit.getChangedJavaFileList();
            for (String fileName : commitChangeFileList) {
                if (!fileDeveloperList.containsKey(fileName)){
                    fileDeveloperList.put(fileName, new HashSet<String>());
                }
                fileDeveloperList.get(fileName).add(commit.getCommitterName());
            }
        }
        return fileDeveloperList;
    }

    public Map<String, Set<String>> identifyFileReviewerList(ReviwerRecommendationDataLoader dataModel){
        Map<String, Set<String>> fileReviewerList = new HashMap<String, Set<String>>();
        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();

        for(String prNumber : trainTestModel.getTrainintPullRequestList()){
            List<String> reviewerList = dataModel.getPullRequestReviewerMap().get(prNumber);
            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);

            List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumber));

            for(String changedFile : reviewFileList){
                for(String reviewer: reviewerList){
                    if(!fileReviewerList.containsKey(changedFile)){
                        fileReviewerList.put(changedFile, new HashSet<String>());
                    }
                    fileReviewerList.get(changedFile).add(reviewer);
                }
            }
        }
        return fileReviewerList;
    }

    public boolean isActivateChRev(ReviwerRecommendationDataLoader dataModel,
                            String prNumber, 
                            Map<String, Set<String>> fileDeveloperList, 
                            Map<String ,Set<String>> fileRevieerList){
        boolean result = false;

        PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
        List<String> changeFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumber));

        
        DescriptiveStatistics reviewerStat = new DescriptiveStatistics();

        for(String fileName : changeFileList){
            
            double totalDeveloperReviewer = 0;

            if(fileDeveloperList.containsKey(fileName)){
                totalDeveloperReviewer = totalDeveloperReviewer + fileDeveloperList.get(fileName).size();
            }
            if(fileRevieerList.containsKey(fileName)){
                totalDeveloperReviewer = totalDeveloperReviewer + fileRevieerList.get(fileName).size();
            }

            System.out.println(fileName + " " + totalDeveloperReviewer);

            reviewerStat.addValue(totalDeveloperReviewer);

            if (totalDeveloperReviewer <= 2){
                return true;
            }
        }

        return result;
    }
    
    public void statKnowledgeableFile(ReviwerRecommendationDataLoader dataModel,
    Map<String, Set<String>> fileDeveloperList, Map<String ,Set<String>> fileRevieerList){

        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();

        double total = 0;
        double totalCHREVActivate = 0;

        for(String prNumber : trainTestModel.getTestingPullRequestList()){
            ++total;
            boolean activateCHREV = isActivateChRev(dataModel, prNumber, fileDeveloperList, fileRevieerList);
            if (activateCHREV){
                ++totalCHREVActivate;
            }
        }

        double activateSofia = (total - totalCHREVActivate);
        double activateSofiaRatio = 100.0 * activateSofia/total;
        System.out.println(String.format(">>>> Activate Sofia %.2f (%.2f) Total = %.2f", activateSofia, activateSofiaRatio, total));

    }


    public List<String> filePathUpdater(List<String> reviewFileList){
        List<String> filePaths = new ArrayList<String>();
        for(String f : reviewFileList){
            String ff = f.substring(0, f.lastIndexOf(".java"));
            ff = ff.replace("/", ".");
            filePaths.add(ff);
        }
        return filePaths;
    }


    public Map<String, SofiaDeveloperModel> generateMetricsForDeveloper(ReviwerRecommendationDataLoader dataModel,
    Set<String> fullDeveloperReviewerList, String prNumberTesting){

        PullRequestModel prModel = dataModel.getPullRequestList().get(prNumberTesting);
        DateTime prCreationTime = prModel.getPrCreatedJodaTime();

        List<String> prChangedFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumberTesting));
        Map<String, ArrayList<String>> devCommitHistory = dataModel.getAuthorCommitList();
        
        Map<String, SofiaDeveloperModel> developerCommitReviewFilePast = new HashMap<String, SofiaDeveloperModel>();

        // Get the development History
        for (String devName : fullDeveloperReviewerList) {
            developerCommitReviewFilePast.put(devName, new SofiaDeveloperModel());

            if (devCommitHistory.containsKey(devName)) {
                // Developer committed
                for (String commitId : devCommitHistory.get(devName)) {
                    GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);

                    if (commit.getCommitJodaDate().isAfter(prCreationTime.minusDays(1))) {
                        break;
                    }

                    int days = Days.daysBetween(commit.getCommitJodaDate(), prCreationTime).getDays();
                    if(days <= 365){
                        // Last one year activity
                        developerCommitReviewFilePast.get(devName).getCommitReviewListPastYear().add(commitId);
                        String monthId = Integer.toString(commit.getCommitJodaDate().getMonthOfYear());
                        developerCommitReviewFilePast.get(devName).getActivityMonthLastYear().add(monthId);
                    }


                    List<String> commitChangeFileList = commit.getChangedJavaFileList();
                    for (String f : commitChangeFileList) {
                        if (prChangedFileList.contains(f)) {
                            developerCommitReviewFilePast.get(devName).getFileReviewCommitList().add(f);
                            developerCommitReviewFilePast.get(devName).getFileCommitList().add(f);
                        }
                    }
                }
            }
        }

        // Get the review History
        for (String prNumber : dataModel.getTrainTestSplits().getFullPullRequestList()){
            PullRequestModel pr = dataModel.getPullRequestList().get(prNumber);
            String prCreatorName = pr.getPrCreaterGitLoginName();
            DateTime creationTime = pr.getPrCreatedJodaTime();
            if (creationTime.isAfter(prCreationTime.minusDays(1))) {
                break;
            }

            if (!developerCommitReviewFilePast.containsKey(prCreatorName)){
                developerCommitReviewFilePast.put(prCreatorName, new SofiaDeveloperModel());
            }

            List<String> changedFileThisPR = filePathUpdater(dataModel.getPrChangedFileList().get(prNumber));

            for(String f : changedFileThisPR){
                if (prChangedFileList.contains(f)) {
                    developerCommitReviewFilePast.get(prCreatorName).getFileReviewCommitList().add(f);
                    developerCommitReviewFilePast.get(prCreatorName).getFileReviewedList().add(f);
                }
            }

            int days = Days.daysBetween(creationTime, prCreationTime).getDays();
            if (days <= 365) {
                // Last one year activity
                developerCommitReviewFilePast.get(prCreatorName).getCommitReviewListPastYear().add(prNumber);
                String monthId = Integer.toString(creationTime.getMonthOfYear());
                developerCommitReviewFilePast.get(prCreatorName).getActivityMonthLastYear().add(monthId);
            }
        }
        return developerCommitReviewFilePast;
    }


    public Map<String, Map<String, SofiaDeveloperModel>> generateMetricsForDeveloperWholeTestData (ReviwerRecommendationDataLoader dataModel,
    Set<String> allDeveloperReviewerList,Map<String,Set<String>> fullDeveloperReviewerListPerPR, List<String> testData){

        Map<String, Map<String, SofiaDeveloperModel>> developerGenerateMetrics = new HashMap<String, Map<String, SofiaDeveloperModel>>();
        Map<String, ArrayList<String>> devCommitHistory = dataModel.getAuthorCommitList();
       
        // Get the development History
        for (String devName : allDeveloperReviewerList) {
        
            if (devCommitHistory.containsKey(devName)) {
                // Developer committed
                for (String commitId : devCommitHistory.get(devName)) {
                    GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);

                    /*if (commit.getCommitJodaDate().isAfter(prCreationTime.minusDays(1))) {
                        break;
                    }*/

                    for(String prNumberTesting : testData){
                        
                        PullRequestModel prModel = dataModel.getPullRequestList().get(prNumberTesting);
                        DateTime prCreationTime = prModel.getPrCreatedJodaTime();
                        List<String> prChangedFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumberTesting));
                        
                        if(!developerGenerateMetrics.containsKey(prNumberTesting)){
                            developerGenerateMetrics.put(prNumberTesting, new HashMap<String, SofiaDeveloperModel>());
                        }

                        if(!developerGenerateMetrics.get(prNumberTesting).containsKey(devName)){
                            developerGenerateMetrics.get(prNumberTesting).put(devName, new SofiaDeveloperModel());
                        }

                        if (commit.getCommitJodaDate().isAfter(prCreationTime.minusDays(1))) {
                            continue;
                        }

                        int days = Days.daysBetween(commit.getCommitJodaDate(), prCreationTime).getDays();
                        if(days <= 365){
                            // Last one year activity
                            developerGenerateMetrics.get(prNumberTesting).get(devName).getCommitReviewListPastYear().add(commitId);
                            String monthId = Integer.toString(commit.getCommitJodaDate().getMonthOfYear());
                            developerGenerateMetrics.get(prNumberTesting).get(devName).getActivityMonthLastYear().add(monthId);
                        }
                        List<String> commitChangeFileList = commit.getChangedJavaFileList();
                        for (String f : commitChangeFileList) {
                            if (prChangedFileList.contains(f)) {
                                developerGenerateMetrics.get(prNumberTesting).get(devName).getFileReviewCommitList().add(f);
                                developerGenerateMetrics.get(prNumberTesting).get(devName).getFileCommitList().add(f);
                            }
                        }
                    }
                }
            }
        }

        // Get the review History
        for (String prNumber : dataModel.getTrainTestSplits().getFullPullRequestList()){
            PullRequestModel pr = dataModel.getPullRequestList().get(prNumber);
            String prCreatorName = pr.getPrCreaterGitLoginName();
            DateTime creationTime = pr.getPrCreatedJodaTime();
            List<String> changedFileThisPR = filePathUpdater(dataModel.getPrChangedFileList().get(prNumber));


            for(String prNumberTesting : testData){
                PullRequestModel prModel = dataModel.getPullRequestList().get(prNumberTesting);
                DateTime prCreationTime = prModel.getPrCreatedJodaTime();
                List<String> prChangedFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prNumberTesting));

                if(!developerGenerateMetrics.containsKey(prNumberTesting)){
                    developerGenerateMetrics.put(prNumberTesting, new HashMap<String, SofiaDeveloperModel>());
                }

                if(!developerGenerateMetrics.get(prNumberTesting).containsKey(prCreatorName)){
                    developerGenerateMetrics.get(prNumberTesting).put(prCreatorName, new SofiaDeveloperModel());
                }

                if (creationTime.isAfter(prCreationTime.minusDays(1))) {
                    continue;
                }

                for(String f : changedFileThisPR){
                    if (prChangedFileList.contains(f)) {
                        developerGenerateMetrics.get(prNumberTesting).get(prCreatorName).getFileReviewCommitList().add(f);
                        developerGenerateMetrics.get(prNumberTesting).get(prCreatorName).getFileReviewedList().add(f);
                    }
                }

                int days = Days.daysBetween(creationTime, prCreationTime).getDays();
                if (days <= 365) {
                    // Last one year activity
                    developerGenerateMetrics.get(prNumberTesting).get(prCreatorName).getCommitReviewListPastYear().add(prNumber);
                    String monthId = Integer.toString(creationTime.getMonthOfYear());
                    developerGenerateMetrics.get(prNumberTesting).get(prCreatorName).getActivityMonthLastYear().add(monthId);
                }
            }
        }
        return developerGenerateMetrics;
    }

    public ArrayList<ReviewerRankingModel> generateDevleoperScore(Map<String, SofiaDeveloperModel> developerMetricList,
                    List<String> prChangedFileList){
        
        ArrayList<ReviewerRankingModel> rankReviewersList = new ArrayList<ReviewerRankingModel>();
        double totalPRChangedFile = prChangedFileList.size();
        // Get the commit Review List Last year in the entire project
        Set<String> commitReviewListEntireProject = new HashSet<String>();
        for(String devName : developerMetricList.keySet()){
            SofiaDeveloperModel sf = developerMetricList.get(devName);
            commitReviewListEntireProject.addAll(sf.getCommitReviewListPastYear());
        }

        for(String devName : developerMetricList.keySet()){
            SofiaDeveloperModel sf = developerMetricList.get(devName);
            // Calculate reviewerKnows
            double reviewerKnows = (double) sf.getFileReviewCommitList().size()/ totalPRChangedFile;
            double learnRec = 1 - reviewerKnows;

            double contributionRatioLastYear = (double)sf.getCommitReviewListPastYear().size()/(double)commitReviewListEntireProject.size();
            double consistencyRatio = (double)sf.getActivityMonthLastYear().size() / 12.0;

            double retentionRec = contributionRatioLastYear * consistencyRatio;

            double turnoverRec = learnRec * retentionRec;

            //System.out.println(devName + " " + turnoverRec);

            sf.setDeveloperName(devName);
            sf.setLearnRec(learnRec);
            sf.setContributionRatio(contributionRatioLastYear);
            sf.setConsistencyRatio(consistencyRatio);
            sf.setRetationRec(retentionRec);
            sf.setTurnoverRec(turnoverRec);

            //sf.printAllScoreValues();

            ReviewerRankingModel rm = new ReviewerRankingModel();
            rm.setReviewerName(devName);
            rm.setScore(retentionRec);
            rankReviewersList.add(rm);
            
        }

        rankReviewersList.sort(new Comparator<ReviewerRankingModel>() {
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

        return rankReviewersList;
    }


    public RecommendationEvaluation recommendReviewerWithSofia(String projectName, String pullRequestPath, 
    String pullReviewPath, String pullCommentPath, String pullFileChangPath){

        

        System.out.println("<<< Finish CHREV Recommendation >>>");


        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath, pullReviewPath,
                pullCommentPath, pullFileChangPath);
        
        Map<String,ArrayList<ReviewerRankingModel>> chrevResult = readData(projectName, dataModel, MODELNAME, DEV_SCORE_PATH);

        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();  
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperListSofia(dataModel);
        
        Map<String, Map<String,Double>> supportValuePairReviewer = new HashMap<String, Map<String,Double>>();

        
        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();

        String lastTrainedPR = trainTestModel.getTrainintPullRequestList().get(trainTestModel.getTrainintPullRequestList().size() - 1);
        DateTime lastPRTime = dataModel.getPullRequestList().get(lastTrainedPR).getPrCreatedJodaTime();


        // File, developer/reviewer
        Map<String, Set<String>> fileDeveloperList = identifyFileDeveloperList(dataModel, lastPRTime);
        Map<String ,Set<String>> fileRevieerList = identifyFileReviewerList(dataModel);

        //statKnowledgeableFile(dataModel, fileDeveloperList, fileRevieerList);
        Set<String> developerReviewerListAllPr = new HashSet<String>();
        
        for(String prNumber : trainTestModel.getFullPullRequestList()){
            developerReviewerListAllPr.addAll(fullDeveloperReviwerListPerPR.get(prNumber));
        }

        Map<String, Map<String, SofiaDeveloperModel>> developerGenerateFullMetricList = generateMetricsForDeveloperWholeTestData(dataModel, 
        developerReviewerListAllPr, fullDeveloperReviwerListPerPR, trainTestModel.getTestingPullRequestList());
            

        int working = 0;
        for (String prNumber : trainTestModel.getTestingPullRequestList()){
            String testKey = projectName + "-" + prNumber;
            ++working;
            System.out.println(String.format("Working [%s] [%d/%d]", prNumber, working, trainTestModel.getTestingPullRequestList().size()));
            boolean enableChrev = isActivateChRev(dataModel, prNumber, fileDeveloperList, fileRevieerList);
            
            System.out.println("Enable CHREV: " + enableChrev);

            List<String> prChangedFileList = dataModel.getPrChangedFileList().get(prNumber);
            Map<String, SofiaDeveloperModel> developerMetricList = developerGenerateFullMetricList.get(prNumber);
            
            System.out.println("Developer: " + developerMetricList.size());
            
            ArrayList<ReviewerRankingModel> sofiaDeveloperRankList = generateDevleoperScore(developerMetricList, prChangedFileList);
            ArrayList<ReviewerRankingModel> chrevDevleoperRankList = chrevResult.get(prNumber);

            if(enableChrev){
                rankingResultList.put(prNumber, chrevDevleoperRankList);
                perTestDevScore.put(testKey, chrevDevleoperRankList);
            }else{
                rankingResultList.put(prNumber, sofiaDeveloperRankList);
                perTestDevScore.put(testKey, sofiaDeveloperRankList);
            }
        }

        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTestingPullRequestList(), 
        null, rankingResultList,
        ConstantUtil.REVIEW_COMMENT_NETWORK);
        
        System.out.println("SOFIA Project: " + projectName + " Testing");

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
        
        long startTime = System.currentTimeMillis();
       
       List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");

        //List<String> projectList = Arrays.asList("apache_activemq");

        //List<String> projectList = Arrays.asList("apache_activemq");

        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();
        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = recommendReviewerWithSofia(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }

        String resultPath = RecommendationUtil.RESULT_DIR + "SOFIA_Test.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //writePerTestDevScore();

        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "Sofia",actualReviewerListPR);
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "SOFIA",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"SOFIA", actualReviewerListPR);
        
        //String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "Sfia_Training.csv";
        //RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }
    
    public static void main(String[] args) {
        SofiaReplicationReviewer ob = new SofiaReplicationReviewer();
        ob.startRecoommendation();
        System.out.println("Program finishes successfully");
    }
}
