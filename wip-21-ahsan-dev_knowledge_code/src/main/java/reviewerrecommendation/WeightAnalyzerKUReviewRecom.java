package reviewerrecommendation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sail.github.model.GitCommitModel;
import com.sail.github.model.KUFileModel;
import com.sail.java.exam.dev.profile.KnowledgeUnitExtractorDevStudy;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.DeveloperExperienceDataSerialization;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.util.ConstantUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

import org.joda.time.DateTime;

public class WeightAnalyzerKUReviewRecom {

    ArrayList<GitCommitModel> gitCommitList;
    ArrayList<GitCommitModel> selectedGitCommits;
    
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
                        String reviewerName = reviwerNameKey.split("-")[0].trim();
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
                        String reviewerName = reviwerNameKey.split("-")[0].trim();
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

    public DeveloperExperienceDataSerialization kuWeightInvestigationObjectCreation(
            String projectName,
            ReviwerRecommendationDataLoader dataModel, 
            Map<String, Map<String, KUFileModel>> savedKnowledgeUnits,
            KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits,
            KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor) {
        


        // Get the last PR Training Date
        List<String> trainingPRs = dataModel.getTrainTestSplits().getFullPullRequestList();
        String lastPRNumberForTraining = trainingPRs.get(0);
        PullRequestModel lastPRmodel = dataModel.getPullRequestList().get(lastPRNumberForTraining);
        DateTime lastPRDate = lastPRmodel.getPrCreatedJodaTime();

        // Development expertise
        Map<String, ArrayList<Map<String, Double>>> developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        Map<String, Double> normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);

        // <Reviewer Name, KU, Values>
        Map<String, Map<String, Double>> reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

        // Review Expertise
        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
                projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
        Map<String, Double> normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
                reviwerPreviousExpertiseWithPR);
        Map<String, Map<String, Double>> reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
                reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        
        DeveloperExperienceDataSerialization seirlizeObject = new DeveloperExperienceDataSerialization();

        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        List<String> trainingPRList = dataModel.getTrainTestSplits().getFullPullRequestList();
        int totalTesting = trainingPRList.size();
        seirlizeObject.setTrainingPRList(trainingPRList);
        seirlizeObject.setProjectName(projectName);

        for (int k = 1 ; k < trainingPRList.size() ; k ++) {
            String prNumber = trainingPRList.get(k);
            String testingCaseName = "Testing-" + prNumber;
            Map<String, ArrayList<Map<String, Double>>> testingCaseExpertise = new HashMap<String, ArrayList<Map<String, Double>>>();
            System.out.println("[Training][Testing] PR Analysis: " + prNumber + " Remaining: " + (totalTesting--));
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

            seirlizeObject.getKuPresentInChangeFiles().put(prNumber, kuPresentInChangeFiles);
            seirlizeObject.getDeveloperPreviousDevelopmentExpertise().put(prNumber, developerPreviousDevelopmentExpertise);
            seirlizeObject.getReviwerPreviousExpertiseWithPR().put(prNumber, reviwerPreviousExpertiseWithPR);

            seirlizeObject.getDeveloperExpertiseHistory().put(prNumber,reviewerDevelopementEpertiseHistory);
            seirlizeObject.getReviewerExpertiseHistory().put(prNumber, reviewerReviewEpertiseHistory);

            // Calculate Ranking for reviewers
            //ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess1(reviewerDevelopementEpertiseHistory, reviewerReviewEpertiseHistory, kuPresentInChangeFiles);
            //rankingResultList.put(prNumber, rankingResult);

        //Update developer knowledge
        DateTime prevDate = lastPRDate;
        lastPRDate = pullRequest.getPrCreatedJodaTime();

        updateDevExpertise( projectName,  prevDate,lastPRDate, developmentKnowledgeUnits, developerPreviousDevelopmentExpertise);
        updateReviewExpertise( projectName, prevDate, lastPRDate, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, reviwerPreviousExpertiseWithPR);

        //developerPreviousDevelopmentExpertise = trainingWithDevelopmentHistory(projectName, lastPRDate, developmentKnowledgeUnits);
        normalizedTrainKUFactor = RecommendationUtil.getNormalizedKUFactorFromDevelopementHistory(
                developmentKnowledgeUnits, lastPRDate);
        reviewerDevelopementEpertiseHistory =
        RecommendationUtil.calculateReviewExpertise (developerPreviousDevelopmentExpertise, normalizedTrainKUFactor);

         // Review Expertise
        //reviwerPreviousExpertiseWithPR = trainingReviewerRecommendationModelWithPullRequest(
        //    projectName, dataModel, savedKnowledgeUnits, knowledgeUnitExtractor, lastPRDate);
         normalizedTrainKUFactorWithPR = RecommendationUtil.getNormalizedTrainKUFactorFromPullRequest(
            reviwerPreviousExpertiseWithPR);
         reviewerReviewEpertiseHistory = RecommendationUtil.calculateReviewExpertise(
            reviwerPreviousExpertiseWithPR, normalizedTrainKUFactorWithPR);
        }
    return seirlizeObject;
}

    public void weightAnalysisMain(String projectName, String pullRequestPath,
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

        DeveloperExperienceDataSerialization seirlizeObject = kuWeightInvestigationObjectCreation(
                     projectName,
                     dataModel, 
                    savedKnowledgeUnits,
                    developmentKnowledgeUnits,
                    knowledgeUnitExtractor);

         try{
            String serFileLocation = String.format("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/weight_analysis_ser/%s.ser",projectName);
            FileOutputStream f = new FileOutputStream(new File(serFileLocation));
            ObjectOutputStream o = new ObjectOutputStream(f);
            // Write objects to file
		    o.writeObject(seirlizeObject);
		    o.close();
		    f.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void startWeightAnalysis(){
        /*List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_stratos", "apache_groovy",
                "elastic_elasticsearch");*/
        
        //List<String> projectList = Arrays.asList("apache_storm");
        List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
        "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            weightAnalysisMain(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            System.out.println("[Done]: " + projectName);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Program finishes successfully");
    }
}
