package reviewerrecommendation;

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
import com.sail.github.model.KUFileModel;
import com.sail.java.exam.dev.profile.KnowledgeUnitExtractorDevStudy;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.model.SimilarityModel;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.PullRequestReviewerModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.util.ConstantUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class ReviewerRecommendationKnowledgeUnit {

    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();

    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    

    ArrayList<GitCommitModel> gitCommitList;
    ArrayList<GitCommitModel> selectedGitCommits;

    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    public Map<String, ArrayList<Map<String, Double>>> trainingReviewerRecommendationModelWithPullRequest(
            String projectName, ReviwerRecommendationDataLoader dataModel,
            Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor,
            DateTime prDate) {
        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
        List<String> trainingPRList = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        int totalTraining = 0;
        for (String prNumber : dataModel.getTrainTestSplits().getFullPullRequestList()){
            //System.out.println("[Training] PR Analysis: " + prNumber + " Working: " + (++totalTraining));
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            if (pullRequest.getPrCreatedJodaTime().isAfter(prDate)){
                continue;
            }
            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);
            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
            String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
            Map<String, KUFileModel> knowledgeUnitData = null;
            if (!savedKnowledgeUnits.containsKey(fileLocation)) {
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            } else {
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            for (String fileName : changedFileList) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim();
                // System.out.println("FileName: " + fileName);
                if (knowledgeUnitData.containsKey(fileName)) {
                    Map<String, Double> fileKu = knowledgeUnitData.get(fileName).getKnowledgeUnitPerFile();
                    // System.out.println("KUUUU FILEEE : " + fileKu.size());
                    for (String reviwerNameKey : dataModel.getPrOnlyReviewerInfo().get(prNumber)) {
                        String reviewerName = reviwerNameKey.split("!")[0].trim();
                        if (!reviwerPreviousExpertise.containsKey(reviewerName)) {
                            reviwerPreviousExpertise.put(reviewerName, new ArrayList<Map<String, Double>>());
                        }
                        reviwerPreviousExpertise.get(reviewerName).add(fileKu);
                    }
                } else {
                    missingMappingChangedFile.add(fileName);
                }
            }
        }
        return reviwerPreviousExpertise;
    }


    public void updateDevExpertise(String projectName, DateTime leftBoundaryDateTime,
            DateTime rightBoundaryDateTime,
            KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
            Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise) {
        for (String devName : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().keySet()) {
            for (String commitId : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().get(devName)
                    .keySet()) {
                GitCommitModel commitModel = developmentKnowledgeUnits.getGitCommitsMapWithCommit().get(commitId);
                if (commitModel.getCommitAuthorJodaDate().isBefore(leftBoundaryDateTime)) {
                    continue;
                }
                else if (commitModel.getCommitAuthorJodaDate().isAfter(rightBoundaryDateTime)) {
                    continue;
                }
                for (String fileName : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().get(devName)
                        .get(commitId).keySet()) {
                    if (!reviwerPreviousExpertise.containsKey(devName)) {
                        reviwerPreviousExpertise.put(devName, new ArrayList<Map<String, Double>>());
                    }
                    reviwerPreviousExpertise.get(devName).add(developmentKnowledgeUnits
                            .getDeveloperfileKnowledgeUnitMatrix().get(devName).get(commitId).get(fileName));
                }
            }
        }

    }

    public void updateReviewExpertise(String projectName, DateTime leftBoundaryDateTime, DateTime rightBoundaryDateTime,
            ReviwerRecommendationDataLoader dataModel, Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor,
            Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise) {

        List<String> trainingPRList = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        int totalTraining = 0;
        for (String prNumber : dataModel.getTrainTestSplits().getFullPullRequestList()) {
            // System.out.println("[Training] PR Analysis: " + prNumber + " Working: " +
            // (++totalTraining));
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            if (pullRequest.getPrCreatedJodaTime().isBefore(leftBoundaryDateTime)) {
                continue;
            } else if (pullRequest.getPrCreatedJodaTime().isAfter(rightBoundaryDateTime)) {
                continue;
            }
            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);
            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
            String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
            Map<String, KUFileModel> knowledgeUnitData = null;
            if (!savedKnowledgeUnits.containsKey(fileLocation)) {
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            } else {
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            for (String fileName : changedFileList) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim();
                // System.out.println("FileName: " + fileName);
                if (knowledgeUnitData.containsKey(fileName)) {
                    Map<String, Double> fileKu = knowledgeUnitData.get(fileName).getKnowledgeUnitPerFile();
                    // System.out.println("KUUUU FILEEE : " + fileKu.size());
                    for (String reviwerNameKey : dataModel.getPrOnlyReviewerInfo().get(prNumber)) {
                        String reviewerName = reviwerNameKey.split("!")[0].trim();
                        if (!reviwerPreviousExpertise.containsKey(reviewerName)) {
                            reviwerPreviousExpertise.put(reviewerName, new ArrayList<Map<String, Double>>());
                        }
                        reviwerPreviousExpertise.get(reviewerName).add(fileKu);
                    }
                } else {
                    missingMappingChangedFile.add(fileName);
                }
            }
        }
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

    public  Map<String, Double> getDeveloperLatestChangeFileTime( ReviwerRecommendationDataLoader dataModel,String prTest){
        Map<String, Double> developerFileUpdateScore = new HashMap<String, Double>();
        List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prTest));
        PullRequestModel prModel = dataModel.getPullRequestList().get(prTest);
        DateTime prTime = prModel.getPrCreatedJodaTime();
        Map<String, ArrayList<String>> devHistory = dataModel.getAuthorCommitList();
        
        for (String devName : devHistory.keySet()) {
            for (String commitId : devHistory.get(devName)) {
                GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
                
                if (commit.getCommitJodaDate().isAfter(prTime)) {
                    continue;
                }
                double days = Days.daysBetween(commit.getCommitJodaDate(), prTime).getDays() + 1;
                double score = 1/days;
                //System.out.println("Diff: " + days);
                if(!developerFileUpdateScore.containsKey(devName)){
                    developerFileUpdateScore.put(devName, -100.0);
                }

                List<String> commitChangeFileList = commit.getChangedJavaFileList();
                for (String fileName : commitChangeFileList) {
                    if(reviewFileList.contains(fileName)){
                        if(developerFileUpdateScore.containsKey(devName)){
                            double updateScore = Math.max(score, developerFileUpdateScore.get(devName));
                            developerFileUpdateScore.put(devName, updateScore);
                        }
                    }
                }
            }
        }
        return developerFileUpdateScore;
    }

    public Map<String, Map<String, Double>> getKnowledgeLastAccessDate(ReviwerRecommendationDataLoader dataModel,
    Map<String, Double> kuPresentInChangeFiles, KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
    String prTest){
        List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prTest));
        Set<String> missingSet = new HashSet<String>();
        PullRequestModel prModel = dataModel.getPullRequestList().get(prTest);
        DateTime prTime = prModel.getPrCreatedJodaTime();
        Map<String, ArrayList<String>> devHistory = dataModel.getAuthorCommitList();
        Map<String, Map<String, Double>> devKULastAccess = new HashMap<String, Map<String, Double>>();
        for (String devName : devHistory.keySet()) {
            for(String ku : ConstantUtil.majorTopicList){
               if(!devKULastAccess.containsKey(devName)){
                   devKULastAccess.put(devName, new HashMap<String,Double>());
               }
               devKULastAccess.get(devName).put(ku, 0.0);
            }
            for (String commitId : devHistory.get(devName)) {
                GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
               
                if (commit.getCommitJodaDate().isAfter(prTime)) {
                    continue;
                }


                double days = Days.daysBetween(commit.getCommitJodaDate(), prTime).getDays() + 1;
                double score = 1/days;

                
                Map<String, Map<String,Double>> fileKUMaps = developmentKnowledgeUnits.getFileKnowledgeUnitOccuranceMatrix().get(commit.getCommitId());

                if(fileKUMaps == null){
                    continue;
                }

                List<String> commitChangeFileList = commit.getChangedJavaFileList();
                
                Set<String> kuList = new HashSet<String>();
                Map<String, Map<String, Map<String, Double>>> devKUFile = developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().get(commit.getAuthorName());
                if(devKUFile.containsKey(commit.getCommitId())){
                    Map<String, Map<String, Double>> fileKUMap = devKUFile.get(commit.getCommitId());
                    for(String ff : fileKUMap.keySet()){
                       if(reviewFileList.contains(ff)){
                          for(String ku : fileKUMap.get(ff).keySet()){
                            if(fileKUMap.get(ff).get(ku) > 0){
                                kuList.add(ku);
                            }
                          }
                       }
                    }
                }
                /*
                //System.out.println("Check: " + developmentKnowledgeUnits.getFileKnowledgeUnitOccuranceMatrix().containsKey(commit.getCommitId()) + " " +  commit.getChangedJavaFileList().size())
                for(String file : fileKUMaps.keySet()){
                    for(String ku : fileKUMaps.get(file).keySet()){
                        if(fileKUMaps.get(file).get(ku) > 0){
                            kuList.add(ku);
                        }
                    }
                }*/

                for(String ku : kuPresentInChangeFiles.keySet()){
                    if(kuPresentInChangeFiles.get(ku) > 0){
                        if(kuList.contains(ku)){
                            devKULastAccess.get(devName).put(ku, Math.max(devKULastAccess.get(devName).get(ku), score));
                        }
                    }
                }

            }
        }
        return devKULastAccess;
    }

    public Map<String, Map<String, Double>> getKnowledgeLastAccessDateFromPR(ReviwerRecommendationDataLoader dataModel,
    Map<String, Double> kuPresentInChangeFiles, KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
    String prTest){
        List<String> reviewFileList = filePathUpdater(dataModel.getPrChangedFileList().get(prTest));
        PullRequestModel prModel = dataModel.getPullRequestList().get(prTest);
        DateTime prTime = prModel.getPrCreatedJodaTime();
        Map<String, ArrayList<String>> devHistory = dataModel.getAuthorCommitList();
        Map<String, Map<String, Double>> devKULastAccess = new HashMap<String, Map<String, Double>>();

        for (String devName : devHistory.keySet()) {
            for(String ku : ConstantUtil.majorTopicList){
               if(!devKULastAccess.containsKey(devName)){
                   devKULastAccess.put(devName, new HashMap<String,Double>());
               }
               devKULastAccess.get(devName).put(ku, 0.0);
            }
        }
        for (String prNumber : dataModel.getTrainTestSplits().getFullPullRequestList()){
            
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            if (pullRequest.getPrCreatedJodaTime().isAfter(prTime)){
                continue;
            }
            double days = Days.daysBetween(pullRequest.getPrCreatedJodaTime(), prTime).getDays() + 1;
                double score = 1/days;

            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);

            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String devName = gitCommit.getAuthorName();
            String commitId = gitCommit.getCommitId();
            Map<String, Map<String,Double>> fileKUMaps = developmentKnowledgeUnits.getFileKnowledgeUnitOccuranceMatrix().get(gitCommit.getCommitId());
            if(fileKUMaps == null){
                continue;
            }
            Set<String> kuList = new HashSet<String>();
            Map<String, Map<String, Map<String, Double>>> devKUFile = developmentKnowledgeUnits
                    .getDeveloperfileKnowledgeUnitMatrix().get(gitCommit.getAuthorName());
            if (devKUFile.containsKey(gitCommit.getCommitId())) {
                Map<String, Map<String, Double>> fileKUMap = devKUFile.get(gitCommit.getCommitId());
                for (String ff : fileKUMap.keySet()) {
                    if (reviewFileList.contains(ff)) {
                        for (String ku : fileKUMap.get(ff).keySet()) {
                            if (fileKUMap.get(ff).get(ku) > 0) {
                                kuList.add(ku);
                            }
                        }
                    }
                }
            }
            /*
            for(String file : fileKUMaps.keySet()){
                for(String ku : fileKUMaps.get(file).keySet()){
                    if(fileKUMaps.get(file).get(ku) > 0){
                        kuList.add(ku);
                    }
                }
            }*/

            for(String ku : kuPresentInChangeFiles.keySet()){
                if(kuPresentInChangeFiles.get(ku) > 0){
                    if(kuList.contains(ku)){
                        devKULastAccess.get(devName).put(ku, Math.max(devKULastAccess.get(devName).get(ku), score));
                    }
                }
            }

        }
        return devKULastAccess;
    }

    /*
        Training last 15 with only developement history (Weight based ranking)
    */
    public Map<String, ArrayList<ReviewerRankingModel>> trainingReviewerRecommendationWithDevelopement(
            String projectName,
            ReviwerRecommendationDataLoader dataModel, 
            Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor) {
        
        List<Double> weights = new ArrayList<Double>();
        for(int i = 0 ; i < ConstantUtil.majorTopicList.size(); i ++ ){
            weights.add(0.034);
        }

        
        int trainResultThreshold =  dataModel.getTrainTestSplits().getTrainintPullRequestList().size() > 15 ? 15 : 0;
        System.out.println("THRESHOLD: " + trainResultThreshold);

        // Get the last PR Training Date
        List<String> trainingPRs = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        String lastPRNumberForTraining = trainingPRs.get(trainResultThreshold);
        PullRequestModel lastPRmodel = dataModel.getPullRequestList().get(lastPRNumberForTraining);
        DateTime lastPRDate = lastPRmodel.getPrCreatedJodaTime();

        // Development expertise
        Map<String, ArrayList<Map<String, Double>>> developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        Map<String, Double> normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);
        Map<String, Map<String, Double>> reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

        // Review Expertise
        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
                projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
        Map<String, Double> normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
                reviwerPreviousExpertiseWithPR);
        Map<String, Map<String, Double>> reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
                reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        
        
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultListTraining = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        List<String> trainingPRList = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        int totalTraining = trainingPRList.size() - trainResultThreshold + 1;
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);

        for (int p = trainResultThreshold + 1 ; p < trainingPRList.size() ; p ++ ) {
            String prNumber = trainingPRList.get(p);
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            Set<String> devAdded = new HashSet<String>();
            List<String> reviewerList = dataModel.getPrOnlyReviewerInfo().get(prNumber);
            String trainKey = projectName + "-" + prNumber;
            
            Map<String, Double> devLastFileUpdateTimeScore = getDeveloperLatestChangeFileTime(dataModel,prNumber);
            String trainingCaseNumber = "Training-" + prNumber;
            Map<String, ArrayList<Map<String, Double>>> testingCaseExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
            System.out.println("[Training] PR Analysis: " + prNumber + " Remaining: " + (totalTraining--));
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);
            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
            String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
            Map<String, KUFileModel> knowledgeUnitData = null;
            if (!savedKnowledgeUnits.containsKey(fileLocation)) {
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            } else {
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            ArrayList< Map<String, Double>> changeFileKuList = new ArrayList<Map<String,Double>>();
            for (String fileName : changedFileList) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim();
                if (knowledgeUnitData.containsKey(fileName)) {
                    Map<String, Double> fileKu = knowledgeUnitData.get(fileName).getKnowledgeUnitPerFile();
                    changeFileKuList.add(fileKu);
                } else {
                    missingMappingChangedFile.add(fileName);
                }
            }
            
            Map<String, Double> kuPresentInChangeFiles = new HashMap<String, Double>();
            for(String kuName : ConstantUtil.majorTopicList){
                kuPresentInChangeFiles.put(kuName, 0.0);
            }
            for(int i = 0 ; i < changeFileKuList.size() ; i ++){
                Map<String,Double> kuFile = changeFileKuList.get(i);
                for(String kuName : kuFile.keySet()){
                    double kuValue = kuFile.get(kuName);
                    if(kuValue > 0.0){
                        kuPresentInChangeFiles.put(kuName, 1.0);
                    }
                }
            }
            Map<String, Map<String, Double>> devKULastAccess = getKnowledgeLastAccessDate(dataModel,kuPresentInChangeFiles,developmentKnowledgeUnits, prNumber);
            Map<String, Map<String, Double>> devKULastAccessPR = getKnowledgeLastAccessDateFromPR(dataModel,kuPresentInChangeFiles,developmentKnowledgeUnits, prNumber);

            
            // Calculate Ranking for reviewers
            //ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess2(reviewerDevelopementEpertiseHistory, 
            //                                            reviewerReviewEpertiseHistory, kuPresentInChangeFiles, weights, devLastFileUpdateTimeScore);

            ArrayList<ReviewerRankingModel> rankReviewersTraining = RecommendationUtil.rankingProcess3(reviewerDevelopementEpertiseHistory, 
                                                        reviewerReviewEpertiseHistory, kuPresentInChangeFiles, weights,devKULastAccess,devKULastAccessPR);
            
            for(ReviewerRankingModel rm : rankReviewersTraining){
                devAdded.add(rm.getReviewerName());
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
                    if (o1.getScore() < o2.getScore()) {
                        return 1;
                    } else if (o1.getScore() > o2.getScore()) {
                        return -1;
                    }
                    return 0;
                }
            });
            rankingResultListTraining.put(prNumber, rankReviewersTraining);
            perTrainDevScore.put(trainKey, rankReviewersTraining);

        //Update developer knowledge
        DateTime prevDate = lastPRDate;
        lastPRDate = pullRequest.getPrCreatedJodaTime();

        //updateDevExpertise( projectName,  prevDate,lastPRDate, developmentKnowledgeUnits, developerPreviousDevelopmentExpertise);
        //updateReviewExpertise( projectName, prevDate, lastPRDate, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, reviwerPreviousExpertiseWithPR);

        developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);
        reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

         // Review Expertise
        reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
            projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
         normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
            reviwerPreviousExpertiseWithPR);
         reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
            reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        }
    return rankingResultListTraining;
}

    /*
        Testing with only developement history (Weight based ranking)
    */
    public Map<String, ArrayList<ReviewerRankingModel>> testingReviewerRecommendationWithDevelopement(
            String projectName,
            ReviwerRecommendationDataLoader dataModel, 
            Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor) {
        
        List<Double> weights = new ArrayList<Double>();
        for(int i = 0 ; i < ConstantUtil.majorTopicList.size(); i ++ ){
            weights.add(0.034);
        }

        // Get the last PR Training Date
        List<String> trainingPRs = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        String lastPRNumberForTraining = trainingPRs.get(trainingPRs.size() - 1);
        PullRequestModel lastPRmodel = dataModel.getPullRequestList().get(lastPRNumberForTraining);
        DateTime lastPRDate = lastPRmodel.getPrCreatedJodaTime();

        // Development expertise
        Map<String, ArrayList<Map<String, Double>>> developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        Map<String, Double> normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);
        Map<String, Map<String, Double>> reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

        // Review Expertise
        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
                projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
        Map<String, Double> normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
                reviwerPreviousExpertiseWithPR);
        Map<String, Map<String, Double>> reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
                reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        
        
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        int totalTesting = testingPRList.size();
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);

        for (String prNumber : testingPRList) {
            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            Set<String> devAdded = new HashSet<String>();

            String testKey = projectName + "-" + prNumber;
            Map<String, Double> devLastFileUpdateTimeScore = getDeveloperLatestChangeFileTime(dataModel,prNumber);
            String testingCaseName = "Testing-" + prNumber;
            Map<String, ArrayList<Map<String, Double>>> testingCaseExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
            System.out.println("[Testing] PR Analysis: " + prNumber + " Remaining: " + (totalTesting--));
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);
            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
            String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
            Map<String, KUFileModel> knowledgeUnitData = null;
            if (!savedKnowledgeUnits.containsKey(fileLocation)) {
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            } else {
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            ArrayList< Map<String, Double>> changeFileKuList = new ArrayList<Map<String,Double>>();
            for (String fileName : changedFileList) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim();
                if (knowledgeUnitData.containsKey(fileName)) {
                    Map<String, Double> fileKu = knowledgeUnitData.get(fileName).getKnowledgeUnitPerFile();
                    changeFileKuList.add(fileKu);
                } else {
                    missingMappingChangedFile.add(fileName);
                }
            }
            
            Map<String, Double> kuPresentInChangeFiles = new HashMap<String, Double>();
            for(String kuName : ConstantUtil.majorTopicList){
                kuPresentInChangeFiles.put(kuName, 0.0);
            }
            for(int i = 0 ; i < changeFileKuList.size() ; i ++){
                Map<String,Double> kuFile = changeFileKuList.get(i);
                for(String kuName : kuFile.keySet()){
                    double kuValue = kuFile.get(kuName);
                    if(kuValue > 0.0){
                        kuPresentInChangeFiles.put(kuName, 1.0);
                    }
                }
            }
            Map<String, Map<String, Double>> devKULastAccess = getKnowledgeLastAccessDate(dataModel,kuPresentInChangeFiles,developmentKnowledgeUnits, prNumber);
            Map<String, Map<String, Double>> devKULastAccessPR = getKnowledgeLastAccessDateFromPR(dataModel,kuPresentInChangeFiles,developmentKnowledgeUnits, prNumber);

            
            // Calculate Ranking for reviewers
            //ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess2(reviewerDevelopementEpertiseHistory, 
            //                                            reviewerReviewEpertiseHistory, kuPresentInChangeFiles, weights, devLastFileUpdateTimeScore);

            ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess3(reviewerDevelopementEpertiseHistory, 
                                                        reviewerReviewEpertiseHistory, kuPresentInChangeFiles, weights,devKULastAccess,devKULastAccessPR);
            
            for(ReviewerRankingModel rm : rankingResult){
                devAdded.add(rm.getReviewerName());
            }
            for(String devName : fullDeveloperReviewerList){
                if (!devAdded.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)0.0);
                    rankingResult.add(rm);
                }
            }

            rankingResult.sort(new Comparator<ReviewerRankingModel>() {
                @Override
                public int compare(ReviewerRankingModel o1, ReviewerRankingModel o2) {
                    if (o1.getScore() < o2.getScore()) {
                        return 1;
                    } else if (o1.getScore() > o2.getScore()) {
                        return -1;
                    }
                    return 0;
                }
            });
            rankingResultList.put(prNumber, rankingResult);
            perTestDevScore.put(testKey, rankingResult);

        //Update developer knowledge
        DateTime prevDate = lastPRDate;
        lastPRDate = pullRequest.getPrCreatedJodaTime();

        //updateDevExpertise( projectName,  prevDate,lastPRDate, developmentKnowledgeUnits, developerPreviousDevelopmentExpertise);
        //updateReviewExpertise( projectName, prevDate, lastPRDate, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, reviwerPreviousExpertiseWithPR);

        developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);
        reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

         // Review Expertise
        reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
            projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
         normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
            reviwerPreviousExpertiseWithPR);
         reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
            reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        }
    return rankingResultList;
}

    public void testingReviewerRecommendationWithPullRequest(String projectName,
            ReviwerRecommendationDataLoader dataModel, Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor,
            Map<String, Map<String, Double>> reviewerEpertiseHistory,
            Map<String, Map<String, Double>> reviewerTestingExpertise,
            Map<String, ArrayList<String>> reviewerPredictedRankedListWithPR) {
        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        int totalTesting = testingPRList.size();

        for (String prNumber : testingPRList) {
            String testingCaseName = "Testing-" + prNumber;
            Map<String, ArrayList<Map<String, Double>>> testingCaseExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
            System.out.println("[Testing] PR Analysis: " + prNumber + " Remaining: " + (totalTesting--));
            PullRequestModel pullRequest = dataModel.getPullRequestList().get(prNumber);
            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(),
                    this.selectedGitCommits);
            List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
            String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
            Map<String, KUFileModel> knowledgeUnitData = null;
            if (!savedKnowledgeUnits.containsKey(fileLocation)) {
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            } else {
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            for (String fileName : changedFileList) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim();
                // System.out.println("FileName: " + fileName);
                if (!testingCaseExpertise.containsKey(testingCaseName)) {
                    testingCaseExpertise.put(testingCaseName, new ArrayList<Map<String, Double>>());
                }
                if (knowledgeUnitData.containsKey(fileName)) {
                    Map<String, Double> fileKu = knowledgeUnitData.get(fileName).getKnowledgeUnitPerFile();
                    testingCaseExpertise.get(testingCaseName).add(fileKu);
                } else {
                    missingMappingChangedFile.add(fileName);
                }
            }
            Map<String, Double> normalizedKUFactor = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(testingCaseExpertise);
            Map<String, Map<String, Double>> testingExpertise = RecommendationUtil.calculateReviewExpertise(testingCaseExpertise,
                    normalizedKUFactor);
            reviewerTestingExpertise.put(testingCaseName, testingExpertise.get(testingCaseName));

            SimilarityModel smModel = cosineSimilarityRanking(reviewerEpertiseHistory, testingExpertise,
                    testingCaseName);

            reviewerPredictedRankedListWithPR.put(prNumber, smModel.getRankedName());
            // System.out.println("Testing: " + testingCaseName + " REsult");
        }
    }

    public Map<String, ArrayList<Map<String, Double>>> trainingWithDevelopmentHistory(String projectName,
            DateTime boundaryDateTime,
            KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits) {
        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();

        for (String devName : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().keySet()) {
            for (String commitId : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().get(devName)
                    .keySet()) {
                GitCommitModel commitModel = developmentKnowledgeUnits.getGitCommitsMapWithCommit().get(commitId);
                if (commitModel.getCommitAuthorJodaDate().isAfter(boundaryDateTime)) {
                    continue;
                }
                for (String fileName : developmentKnowledgeUnits.getDeveloperfileKnowledgeUnitMatrix().get(devName)
                        .get(commitId).keySet()) {
                    if (!reviwerPreviousExpertise.containsKey(devName)) {
                        reviwerPreviousExpertise.put(devName, new ArrayList<Map<String, Double>>());
                    }
                    reviwerPreviousExpertise.get(devName).add(developmentKnowledgeUnits
                            .getDeveloperfileKnowledgeUnitMatrix().get(devName).get(commitId).get(fileName));
                }
            }
        }
        return reviwerPreviousExpertise;
    }

    public Map<String, Map<String, Set<String>>> getReviewerFrequencyDataPerTestCase(ReviwerRecommendationDataLoader dataModel){

        // Test Number, Reviewer Name, Total Review
        Map<String, Map<String, Set<String>>> reviewFrequency = new HashMap<String,Map<String,Set<String>>>();
        // Reviwer Name, set of reviews
        Map<String, Set<String>> reviewerReviewHistory = new HashMap<String,Set<String>>();
        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        String firstPRTest = testingPRList.get(0);
        DateTime firstTestPRCreationDate = dataModel.getPullRequestList().get(testingPRList.get(0)).getPrCreatedJodaTime();
        int total = 0;

        for(int i = 0 ; i < dataModel.getTrainTestSplits().fullPullRequestList.size() ; i ++ ){
            String prNumber = dataModel.getTrainTestSplits().fullPullRequestList.get(i);
            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            if(prModel.getPrCreatedJodaTime().isBefore(firstTestPRCreationDate)){
                ArrayList<PullRequestReviewerModel> reviewInfoList = dataModel.getPrReviewerListFull().get(prNumber);
                for(PullRequestReviewerModel revModel : reviewInfoList){
                    String reviewerName = revModel.getReviewerName();
                    if(!reviewerReviewHistory.containsKey(reviewerName)){
                        reviewerReviewHistory.put(reviewerName, new HashSet<String>());
                    }
                    reviewerReviewHistory.get(reviewerName).add(prNumber);
                }
                if(!reviewerReviewHistory.containsKey("Total")){
                    reviewerReviewHistory.put("Total", new HashSet<String>());
                }
                reviewerReviewHistory.get("Total").add(prNumber);
                ++total;
            }else{
                break;
            }
        }

        for (String prNumber : testingPRList) {
            ArrayList<PullRequestReviewerModel> reviewInfoList = dataModel.getPrReviewerListFull().get(prNumber);
            reviewFrequency.put(prNumber , new HashMap<String, Set<String>>());
            for(PullRequestReviewerModel revModel : reviewInfoList){
                String reviewerName = revModel.getReviewerName();
                if(!reviewerReviewHistory.containsKey(reviewerName)){
                    reviewerReviewHistory.put(reviewerName, new HashSet<String>());
                }
                reviewerReviewHistory.get(reviewerName).add(prNumber);
                
            }
            reviewerReviewHistory.get("Total").add(prNumber);
            
            for(String reviewerName : reviewerReviewHistory.keySet()){
                reviewFrequency.get(prNumber).put(reviewerName, new HashSet<String>());
                reviewFrequency.get(prNumber).get(reviewerName).addAll(reviewerReviewHistory.get(reviewerName));
            }
            reviewFrequency.get(prNumber).put("Total", new HashSet<String>());
            reviewFrequency.get(prNumber).get("Total").addAll(reviewerReviewHistory.get("Total"));
        }
        return reviewFrequency;
    }

    public RecommendationEvaluation knowlegeUnitRecommendationModelGeneration(String projectName, String pullRequestPath,
            String pullReviewPath, String pullCommentPath, String pullFileChangPath) {

        Map<String, ArrayList<String>> reviewerPredictedRankedListWithPR = new HashMap<String, ArrayList<String>>();
        Map<String, Map<String, KUFileModel>> savedKnowledgeUnits = new HashMap<String, Map<String, KUFileModel>>();

        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
        KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor = new KnowledgeUnitExtractorDevStudy();
        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath,
                pullReviewPath, pullCommentPath, pullFileChangPath);

        this.gitCommitList = dataModel.getGitCommitList();
        this.selectedGitCommits = dataModel.getSelectedGitCommits();

        ArrayList<String> studiedPrList = dataModel.getPullRequestForReviewRecommendation();
        List<String> trainingPRList = dataModel.getTrainTestSplits().getTrainintPullRequestList();
        List<String> testingPRList = dataModel.getTrainTestSplits().getTestingPullRequestList();

        KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits = new KnonwledgeUnitsExpertiseFromDevelopment(
                projectName, false, dataModel);
        developmentKnowledgeUnits.extractKnowledgeUnits();


        Map<String, ArrayList<ReviewerRankingModel>> rankingResultListTraining = trainingReviewerRecommendationWithDevelopement(
                     projectName,
                     dataModel, 
                    savedKnowledgeUnits,
                    developmentKnowledgeUnits,
                    knowledgeUnitExtractor);

        Map<String, ArrayList<ReviewerRankingModel>> ranktResultList = testingReviewerRecommendationWithDevelopement(
                     projectName,
                     dataModel, 
                    savedKnowledgeUnits,
                    developmentKnowledgeUnits,
                    knowledgeUnitExtractor);

        Map<String, Map<String, Set<String>>> reviewFrequency = getReviewerFrequencyDataPerTestCase(dataModel);
        
        int trainResultThreshold =  dataModel.getTrainTestSplits().getTrainintPullRequestList().size() > 16 ? dataModel.getTrainTestSplits().getTrainintPullRequestList().size() - 16 : 0;

        RecommendationEvaluation evaluationRestTraining = new RecommendationEvaluation(projectName);
        evaluationRestTraining.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTrainintPullRequestList().subList(trainResultThreshold + 1, dataModel.getTrainTestSplits().getTrainintPullRequestList().size()), 
        null, rankingResultListTraining,
        "KUREC_Training");
        System.out.println("Project: " + projectName + " Training");
        evaluationRestTraining.printAccuracyResult();
        evaluationRestTraining.printMAP();
        trainingEvaluationList.add(evaluationRestTraining);
        
        //perTrainDevScore.put(projectName, rankingResultListTraining);
        
        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        String fileDir = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Recommendation_Rank_Analysis/dev_review_exp/" + projectName + "/";
        //evaluationRest.writeReviewerRankResultDataWithReviewHistory(projectName, dataModel, ranktResultList, fileDir, reviewFrequency);

        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
                                                                    dataModel.getTrainTestSplits().getTestingPullRequestList(), 
                                                                    null, ranktResultList,
                                                                    ConstantUtil.REVIEW_KNOWLEDGE_UNIT);
        System.out.println("Project: " + projectName + " Testing");
        System.out.println("-------------------------------");
        evaluationRest.printAccuracyResult();
        evaluationRest.printMAP();

        for(String prTest: dataModel.getTrainTestSplits().getTestingPullRequestList()){
            List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
            String testKey = projectName + "-" + prTest;
            actualReviewerListPR.put(testKey, actualReviewer);
        }

         return evaluationRest;
    }

    public void writeExpertiseKUVector(Map<String, Map<String, Double>> reviewerEpertiseHistory,
            Map<String, Map<String, Double>> reviewerTestingExpertise, RecommendationEvaluation evaluationRest,
            String projectName) {
        String fileName = "/scratch/ahsan/Java_Exam_Work/Result/reviewer_recommendation/model_investigation/"
                + projectName + "-ku_model_invest_ch.csv";
        try {
            CsvWriter writer = new CsvWriter(fileName);
            writer.write("Dev/Test");
            for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                writer.write(ConstantUtil.majorTopicList.get(i));
            }
            for (int i = 0; i < evaluationRest.studiedRankedList.length; i++) {
                int rank = evaluationRest.studiedRankedList[i];
                writer.write("AC-" + rank);
            }
            writer.endRecord();

            // Writing developer historical PR experience
            for (String devName : reviewerEpertiseHistory.keySet()) {
                writer.write(devName);
                for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                    String kuName = ConstantUtil.majorTopicList.get(i);
                    writer.write(Double.toString(reviewerEpertiseHistory.get(devName).get(kuName)));
                }
                for (int i = 0; i < evaluationRest.studiedRankedList.length; i++) {
                    writer.write("NAN");
                }
                writer.endRecord();

            }
            // Writing pull request Test
            for (String testPR : reviewerTestingExpertise.keySet()) {
                // System.out.println(testPR);
                writer.write(testPR);
                for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                    String kuName = ConstantUtil.majorTopicList.get(i);
                    writer.write(Double.toString(reviewerTestingExpertise.get(testPR).get(kuName)));
                }
                for (int i = 0; i < evaluationRest.studiedRankedList.length; i++) {
                    int rank = evaluationRest.studiedRankedList[i] - 1;
                    writer.write(Double
                            .toString(evaluationRest.getAccuracyPerTest().get(testPR.split("-")[1].trim()).get(rank)));
                }
                writer.endRecord();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SimilarityModel cosineSimilarityRanking(Map<String, Map<String, Double>> trainingExpertise,
            Map<String, Map<String, Double>> testingExpertise, String testingCaseName) {

        SimilarityModel smModel = new SimilarityModel();
        Map<String, Double> testingCase = testingExpertise.get(testingCaseName);
        Map<String, Double> similarityValues = new HashMap<String, Double>();

        for (String reviewerName : trainingExpertise.keySet()) {
            double xy = 0.0;
            double x = 0;
            double y = 0;
            Map<String, Double> trainingCase = trainingExpertise.get(reviewerName);
            for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                String kuName = ConstantUtil.majorTopicList.get(i);

                xy = xy + (testingCase.get(kuName) * trainingCase.get(kuName));
                x = x + testingCase.get(kuName) * testingCase.get(kuName);
                y = y + trainingCase.get(kuName) + trainingCase.get(kuName);

            }
            x = Math.sqrt(x);
            y = Math.sqrt(y);
            double similarity = (xy) / (x * y);
            smModel.getRankedName().add(reviewerName);
            smModel.getRankedValue().put(reviewerName, similarity);
        }
        smModel.getRankedName().sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if (smModel.getRankedValue().get(o1) < smModel.getRankedValue().get(o2)) {
                    return -1;
                } else if (smModel.getRankedValue().get(o1) > smModel.getRankedValue().get(o2)) {
                    return 1;
                }
                return 0;
            }
        });

        return smModel;
    }

    public ReviewerRecommendationKnowledgeUnit() {

    }

    public void startRecoommendation(){

        long startTime = System.currentTimeMillis();
        /*List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_stratos", "apache_groovy",
                "elastic_elasticsearch");*/
        
        //List<String> projectList = Arrays.asList("apache_groovy");
        //List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        //"apache_hbase","apache_hive", "apache_storm","apache_wicket","elastic_elasticsearch");
        
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket","elastic_elasticsearch");
        
        
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = knowlegeUnitRecommendationModelGeneration(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }

        String resultPath = RecommendationUtil.RESULT_DIR + "KUREC.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //writePerTestDevScore();
        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "KUREC");
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"KUREC", actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"KUREC", actualReviewerListPR);
        
        
        String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "KUREC_Training.csv";
        //RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }

    public void writePerTestDevScore(){
        try{
            for(String prNumber : perTestDevScore.keySet()){
                CsvWriter writer = new CsvWriter(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH + "KUREC/" + prNumber + ".csv");
                writer.write("dev_name");
                writer.write("score");
                writer.endRecord();
                ArrayList<ReviewerRankingModel> reviewerList = perTestDevScore.get(prNumber);
                reviewerList.sort(new Comparator<ReviewerRankingModel>() {
                    @Override
                    public int compare(ReviewerRankingModel o1, ReviewerRankingModel o2) {
                        return (o1.getReviewerName().compareTo(o2.getReviewerName()));
                    }
                    
                });
                for(ReviewerRankingModel rm : perTestDevScore.get(prNumber)){
                    writer.write(rm.getReviewerName());
                    writer.write(Double.toString(rm.getScore()));
                    writer.endRecord();
                    
                }
                writer.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        ReviewerRecommendationKnowledgeUnit ob = new ReviewerRecommendationKnowledgeUnit();
        
        // ob.analyzeReviewrDevelopementHistory();
        
        /*ob.knowlegeUnitRecommendationModelGeneration(projectName, prFilePath,
         prReviewerPath, prCommentFilePath,
         prChagneFilePath);*/

        ob.startRecoommendation();
        //ob.startWeightAnalysis();
        // ReviwerRecommendationDataLoader dataModel = new
        // ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath,
        // prCommentFilePath, prChagneFilePath);

        long endTime = System.currentTimeMillis();

        long timeDiff = (endTime - startTime) / (60*1000);

        System.out.println("Total Runtime [" + timeDiff + "] minutes");
        System.out.println("Program finishes successfully");
    }
}