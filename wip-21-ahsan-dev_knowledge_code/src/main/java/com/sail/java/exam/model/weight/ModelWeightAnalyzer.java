package com.sail.java.exam.model.weight;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.DeveloperExperienceDataSerialization;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

public class ModelWeightAnalyzer {

    public String WEIGHT_DIR = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/weight_analysis_ser/";


    public DeveloperExperienceDataSerialization loadSerializedObject(String path) {
        try {
            FileInputStream fi = new FileInputStream(new File(path));
            ObjectInputStream oi = new ObjectInputStream(fi);
            DeveloperExperienceDataSerialization ob = (DeveloperExperienceDataSerialization) oi.readObject();
            oi.close();
            fi.close();
            return ob;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<Double>> readWeightValues(String path){
        List<List<Double>> weightValues = new ArrayList<List<Double>>();
        try{
            CsvReader reader = new CsvReader(path);
            reader.readHeaders();
            int totalaColumns = reader.getHeaderCount();
            while(reader.readRecord()){
                List<Double> weights = new ArrayList<Double>();
                for(int i = 0 ; i < totalaColumns ; i ++){
                    weights.add(Double.parseDouble(reader.get(i)));
                }
                weightValues.add(weights);
            }
            System.out.println("[Complete] Wight Loading: " + weightValues.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return weightValues;
    }

    public void analysisWeightCalculation(String projectName) {

        String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName + ".csv";
        String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/"
                + projectName + "_files.csv";
        String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/"
                + projectName + "_comments_with_discussion.csv";
        String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/"
                + projectName + "_review.csv";

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                prReviewerPath, prCommentFilePath, prChagneFilePath);

        String serializeObjectPath = String.format(
                "%s%s.ser",WEIGHT_DIR,projectName);

        DeveloperExperienceDataSerialization serObject = loadSerializedObject(serializeObjectPath);

        // Training and validation set generation

        List<String> sampleList = serObject.getTrainingPRList();

        ArrayList<String> trainingList = new ArrayList<String>();
        ArrayList<String> validationList = new ArrayList<String>();

        int cutOffPoint = (int) Math.ceil((sampleList.size() * 0.70));
        for (int i = 0; i < cutOffPoint; i++) {
            trainingList.add(sampleList.get(i));
        }
        for (int i = cutOffPoint; i < sampleList.size(); i++) {
            validationList.add(sampleList.get(i));
        }

        // Done: Training and validation set generation

        String weightFilePath = WEIGHT_DIR + "weight_comb.csv";
        List<List<Double>> weightValues = readWeightValues(weightFilePath);

        double maxAccuracy = -1;
        double maxMAP = -1;
        int indWeightMaxAccuracy = -1;
        int indWeightMaxMAP = -1;

        for(int i = 0 ; i < 100000 ; i ++ ){
            Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
            List<Double> weights = weightValues.get(i);
            for(String prNumber : validationList){
                Map<String,Map<String,Double>> reviewerDevelopementEpertiseHistory = serObject.getDeveloperExpertiseHistory().get(prNumber);
                Map<String,Map<String,Double>> reviewerReviewEpertiseHistory = serObject.getReviewerExpertiseHistory().get(prNumber);
                Map<String,Double> kuPresentInChangeFiles = serObject.getKuPresentInChangeFiles().get(prNumber);
                ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess1(reviewerDevelopementEpertiseHistory, reviewerReviewEpertiseHistory, 
                                                                kuPresentInChangeFiles, weights);
                rankingResultList.put(prNumber, rankingResult);
            }
            RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
            evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            validationList, null, rankingResultList, "Test-" + i);

            if(evaluationRest.getAccuracyValue()[5] > maxAccuracy){
                maxAccuracy = evaluationRest.getAccuracyValue()[5];
                indWeightMaxAccuracy = i;
            }
            if(evaluationRest.getMeanAveragePrecision().get(5).getMean() > maxMAP){
                maxMAP = evaluationRest.getMeanAveragePrecision().get(5).getMean();
                indWeightMaxMAP = i;
            }
            System.out.println("Finish Testing [" + i + "]");
        }


        List<Double> weights = weightValues.get(indWeightMaxAccuracy);
        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        for(String prNumber : dataModel.getTrainTestSplits().getTestingPullRequestList()){
            Map<String,Map<String,Double>> reviewerDevelopementEpertiseHistory = serObject.getDeveloperExpertiseHistory().get(prNumber);
            Map<String,Map<String,Double>> reviewerReviewEpertiseHistory = serObject.getReviewerExpertiseHistory().get(prNumber);
            Map<String,Double> kuPresentInChangeFiles = serObject.getKuPresentInChangeFiles().get(prNumber);
            ArrayList<ReviewerRankingModel> rankingResult = RecommendationUtil.rankingProcess1(reviewerDevelopementEpertiseHistory, reviewerReviewEpertiseHistory, 
                                                            kuPresentInChangeFiles, weights);
            rankingResultList.put(prNumber, rankingResult);
        }

        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
            evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            dataModel.getTrainTestSplits().getTestingPullRequestList(), null, rankingResultList, "Test-");
        evaluationRest.printAccuracyResult();
        evaluationRest.printMAP();

        System.out.println("Max Accuracy :" + maxAccuracy + " Weight: " + indWeightMaxAccuracy);
        System.out.println("Max MAP :" + maxMAP + " Weight: " + indWeightMaxMAP);
        System.out.println("Total number of weights = " + ConstantUtil.topicCategoryList.size());
    }
    

    public static void main(String[] args) throws Exception {

        ModelWeightAnalyzer ob = new ModelWeightAnalyzer();
        ob.analysisWeightCalculation("apache_activemq");
        //System.out.println(ConstantUtil.majorTopicList.size());
        System.out.println("Program finishes successfully");
    }
}
