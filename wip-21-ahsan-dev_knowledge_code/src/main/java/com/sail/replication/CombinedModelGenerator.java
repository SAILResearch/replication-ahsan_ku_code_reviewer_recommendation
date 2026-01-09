package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class CombinedModelGenerator {

    List<String> studiedRecModels = Arrays.asList("KUREC","CF","RF","CHREV","ER");
    public String perDevModelScorePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score/";
    
    List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");


    public Map<String, ReviwerRecommendationDataLoader> getDataModel(List<String> projectList){
        Map<String, ReviwerRecommendationDataLoader> dataModelList = new HashMap<String, ReviwerRecommendationDataLoader>();
        for(String projectName : projectList){
            String pullRequestPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName +".csv";
            String pullFileChangPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String pullCommentPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String pullReviewPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            
            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath, pullReviewPath,
                    pullCommentPath, pullFileChangPath);
            dataModelList.put(projectName, dataModel);
        }
        return dataModelList;
        
    }

    public Map<String,Map<String,Map<String, Map<String,Double>>>> readScoreData(String path , List<String> recomModelList,
    Map<String, ReviwerRecommendationDataLoader> dataModelList){
        // project, testcase, Developer, model, score
        Map<String,Map<String,Map<String, Map<String,Double>>>> perModelDecScore = new HashMap<String,Map<String,Map<String,Map<String,Double>>>>();
        for(String modelName : recomModelList){
            System.out.println("Start Loading Model data: " + modelName);
            String fullPath = path  + modelName + "/";
            for(String projectName : projectList){
                if(!perModelDecScore.containsKey(projectName)){
                    perModelDecScore.put(projectName, new HashMap<String,Map<String,Map<String,Double>>>());
                }
                ReviwerRecommendationDataLoader dataModel  = dataModelList.get(projectName);
                List<String> testDataSet = dataModel.getTrainTestSplits().getTestingPullRequestList();
                for(String prTest : testDataSet){
                    String testKey = projectName + "-" + prTest;
                    String scoreFilePath = fullPath + testKey + ".csv";
                    if(!perModelDecScore.get(projectName).containsKey(prTest)){
                        perModelDecScore.get(projectName).put(prTest, new HashMap<String,Map<String,Double>>());
                    }
                    try{
                        CsvReader reader = new CsvReader(scoreFilePath);
                        reader.readHeaders();
                        while(reader.readRecord()){
                            String devName = reader.get("dev_name");
                            Double score = Double.parseDouble(reader.get("score"));
                            if(!perModelDecScore.get(projectName).get(prTest).containsKey(devName)){
                                perModelDecScore.get(projectName).get(prTest).put(devName, new HashMap<String,Double>());
                            }
                            if(!perModelDecScore.get(projectName).get(prTest).get(devName).containsKey(modelName)){
                                perModelDecScore.get(projectName).get(prTest).get(devName).put(modelName, score);
                            }

                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            
        }
        /*for(String projectName : perModelDecScore.keySet()){
            for(String prTest : perModelDecScore.get(projectName).keySet()){
                int total = 0;
                int missing = 0;
                for(String devName : perModelDecScore.get(projectName).get(prTest).keySet()){
                    ++total;
                    if(perModelDecScore.get(projectName).get(prTest).get(devName).size() < 2){
                        //System.out.println(projectName + " " + prTest + " " + devName);
                        ++missing;
                    }
                }
                //System.out.println("Total : " + total + " Missing: " + missing);
            }
        }*/
        System.out.println("Finish Data Loading");
        return perModelDecScore;
    }
    

    public void generateCombinedModel(List<String> myModels, String fileName,
    Map<String, ReviwerRecommendationDataLoader> dataModelList,
    Map<String,Map<String,Map<String, Map<String,Double>>>> perModelDecScore){
        
        List<RecommendationEvaluation> recommendationResultListGeomMean = new ArrayList<RecommendationEvaluation>();
        List<RecommendationEvaluation> recommendationResultListSumValues = new ArrayList<RecommendationEvaluation>();

        for(String projectName : perModelDecScore.keySet()){
            Map<String, ArrayList<ReviewerRankingModel>> rankingResultListGeomMean = new HashMap<String, ArrayList<ReviewerRankingModel>>();
            Map<String, ArrayList<ReviewerRankingModel>> rankingResultListSumValues = new HashMap<String, ArrayList<ReviewerRankingModel>>();

            for(String prTest : perModelDecScore.get(projectName).keySet()){
                ArrayList<ReviewerRankingModel> rankReviewersGeomMean = new ArrayList<ReviewerRankingModel>();
                ArrayList<ReviewerRankingModel> rankReviewersSum = new ArrayList<ReviewerRankingModel>();
                for(String devName : perModelDecScore.get(projectName).get(prTest).keySet()){
                    DescriptiveStatistics modelScore = new DescriptiveStatistics();
                   for(String modelName : myModels){
                        double scoreValue = 0.0;
                       if(perModelDecScore.get(projectName).get(prTest).get(devName).containsKey(modelName)){
                           scoreValue = perModelDecScore.get(projectName).get(prTest).get(devName).get(modelName);
                       }
                       modelScore.addValue(scoreValue);
                   }
                   double geomMean = modelScore.getGeometricMean();
                   double sumValue = modelScore.getSum();

                   // geomMean
                   ReviewerRankingModel rmGeom = new ReviewerRankingModel();
                   rmGeom.setReviewerName(devName);
                   rmGeom.setScore(geomMean);
                   rankReviewersGeomMean.add(rmGeom);
                   // sum
                    ReviewerRankingModel rmSum = new ReviewerRankingModel();
                    rmSum.setReviewerName(devName);
                    rmSum.setScore(sumValue);
                    rankReviewersSum.add(rmSum);

                }
                
                rankReviewersGeomMean.sort(new Comparator<ReviewerRankingModel>() {
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

                   rankingResultListGeomMean.put(prTest, rankReviewersGeomMean);

                   rankReviewersSum.sort(new Comparator<ReviewerRankingModel>() {
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
                   rankingResultListSumValues.put(prTest, rankReviewersSum);
            }

            ReviwerRecommendationDataLoader dataModel = dataModelList.get(projectName);
            
            RecommendationEvaluation evaluationRestGeomMean = new RecommendationEvaluation(projectName);
            evaluationRestGeomMean.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            dataModel.getTrainTestSplits().getTestingPullRequestList(), 
            null, rankingResultListGeomMean,
            "COMBINED_GEOM_MEAN");
            System.out.println("GEOM MEAN RESULT");
            evaluationRestGeomMean.printAccuracyResult();
            evaluationRestGeomMean.printMAP();
            recommendationResultListGeomMean.add(evaluationRestGeomMean);


            RecommendationEvaluation evaluationRestSumValues = new RecommendationEvaluation(projectName);
            evaluationRestSumValues.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            dataModel.getTrainTestSplits().getTestingPullRequestList(), 
            null, rankingResultListSumValues,
            "COMBINED_SUM_VALUES");
            System.out.println("SUM VALUES RESULT");
            evaluationRestSumValues.printAccuracyResult();
            evaluationRestSumValues.printMAP();
            recommendationResultListSumValues.add(evaluationRestSumValues);

        System.out.println("Project: " + projectName);
        }
        String resultPathGeomMean = RecommendationUtil.RESULT_DIR + "res_combined_"+ fileName + "_geom_mean.csv";
        RecommendationUtil.writeRecResult(resultPathGeomMean, recommendationResultListGeomMean);

        String resultPathSumValues = RecommendationUtil.RESULT_DIR + "res_combined_"+ fileName +"_sum_values.csv";
        RecommendationUtil.writeRecResult(resultPathSumValues, recommendationResultListSumValues);
    }
   
    public static void main(String[] args) {
        CombinedModelGenerator ob = new CombinedModelGenerator();
        Map<String, ReviwerRecommendationDataLoader> dataModelList = ob.getDataModel(ob.projectList);
        Map<String,Map<String,Map<String, Map<String,Double>>>> perModelDecScore = ob.readScoreData(ob.perDevModelScorePath,ob.studiedRecModels,dataModelList);
        
        for(int i = 1 ; i < ob.studiedRecModels.size() ; i ++ ){
            String fileName = "KUREC" + "_" + ob.studiedRecModels.get(i);
            ob.generateCombinedModel(Arrays.asList("KUREC",ob.studiedRecModels.get(i)),fileName,dataModelList,perModelDecScore);
        }
        
        System.out.println("Program finishes successfully");
    }
}
