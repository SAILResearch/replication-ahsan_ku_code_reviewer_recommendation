package reviewerrecommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.csvreader.CsvReader;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

public class AdaptiveMetaHeuristicModel {
    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore;
    Map<String,List<String>> actualReviewerListPR ;

    String devScorePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
    //String devScorePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score/";
    
    String devScorePathTraining = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_training/";

    public String scratchDataLocBackup = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/Result/";
    

    List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    "apache_hbase","apache_hive", "apache_storm","apache_wicket", "elastic_elasticsearch");
    
    //List<String> projectList = Arrays.asList("apache_activemq","apache_groovy");
    
    
    List<String> studiedRecModels = Arrays.asList("KUREC","CF","RF","CHREV","ER", "CORRECT", "CN", "SOFIA");


    public String AD_FREQ = "AD_FREQ";
    public String AD_REC = "AD_REC";
    public String AD_HYBRID = "AD_HYBRID";

    List<String> adaptiveModeList = Arrays.asList(AD_FREQ, AD_REC, AD_HYBRID);


    public Map<String, ReviwerRecommendationDataLoader> getDataModel(List<String> projectList){
        Map<String, ReviwerRecommendationDataLoader> dataModelList = new HashMap<String, ReviwerRecommendationDataLoader>();
        for(String projectName : projectList){
             String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            
            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            dataModelList.put(projectName, dataModel);
        }
        return dataModelList;
        
    }
    
    public Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> readData(Map<String, ReviwerRecommendationDataLoader> dataModelList,
    List<String> projectList){
        Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData = new HashMap<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>>();
        for(String modelName : studiedRecModels){
            if(!devScoreFullData.containsKey(modelName)){
                devScoreFullData.put(modelName, new HashMap<String, Map<String,ArrayList<ReviewerRankingModel>>>());
            }
            System.out.println("Start Loading Model data: " + modelName);
            
            for(String projectName : projectList){
                String fullPath = devScorePath  + modelName + "/" + projectName + "/";
                if(!devScoreFullData.get(modelName).containsKey(projectName)){
                    devScoreFullData.get(modelName).put(projectName, new HashMap<String,ArrayList<ReviewerRankingModel>>());
                }
                ReviwerRecommendationDataLoader dataModel = dataModelList.get(projectName);
                List<String> testDataSet = dataModel.getTrainTestSplits().getTestingPullRequestList();
                for(String prTest : testDataSet){
                    ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
                    String testKey = projectName + "-" + prTest;
                    String scoreFilePath = fullPath + testKey + ".csv";
                    try{
                        CsvReader reader = new CsvReader(scoreFilePath);
                        reader.readHeaders();
                        while(reader.readRecord()){
                            String devName = reader.get("dev_name");
                            Double score = Double.parseDouble(reader.get("score"));
                            ReviewerRankingModel rm = new ReviewerRankingModel();
                            rm.setReviewerName(devName);
                            rm.setScore(score);
                            rankReviewers.add(rm);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    devScoreFullData.get(modelName).get(projectName).put(prTest, rankReviewers);
                }
            }
        }
        System.out.println("Complete data Read.");
        return devScoreFullData;
    }

    public Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> readTrainingData(Map<String, ReviwerRecommendationDataLoader> dataModelList,
    List<String> projectList){
        Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData = new HashMap<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>>();
        for(String modelName : studiedRecModels){
            if(!devScoreFullData.containsKey(modelName)){
                devScoreFullData.put(modelName, new HashMap<String, Map<String,ArrayList<ReviewerRankingModel>>>());
            }
            System.out.println("Start Loading Model Traingin Score data: " + modelName);
            String fullPath = devScorePathTraining  + modelName + "/";
            for(String projectName : projectList){
                if(!devScoreFullData.get(modelName).containsKey(projectName)){
                    devScoreFullData.get(modelName).put(projectName, new HashMap<String,ArrayList<ReviewerRankingModel>>());
                }
                ReviwerRecommendationDataLoader dataModel = dataModelList.get(projectName);
                List<String> trainDataSet = dataModel.getTrainTestSplits().getTrainintPullRequestList();
                int trainResultThreshold =  dataModel.getTrainTestSplits().getTrainintPullRequestList().size() > 15 ? dataModel.getTrainTestSplits().getTrainintPullRequestList().size() - 15 : 0;
        
                for(int p = trainResultThreshold + 1 ; p < trainDataSet.size() ; p++){
                    String prNumber = trainDataSet.get(p);
                    ArrayList<ReviewerRankingModel> rankReviewers = new ArrayList<ReviewerRankingModel>();
                    String testKey = projectName + "-" + prNumber;
                    String scoreFilePath = fullPath + testKey + ".csv";
                    try{
                        CsvReader reader = new CsvReader(scoreFilePath);
                        reader.readHeaders();
                        while(reader.readRecord()){
                            String devName = reader.get("dev_name");
                            Double score = Double.parseDouble(reader.get("score"));
                            ReviewerRankingModel rm = new ReviewerRankingModel();
                            rm.setReviewerName(devName);
                            rm.setScore(score);
                            rankReviewers.add(rm);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    devScoreFullData.get(modelName).get(projectName).put(prNumber, rankReviewers);
                }
            }
        }
        System.out.println("Complete data Read Training.");
        return devScoreFullData;
    }
    public void checkResutSimilarity(Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData,
    String modelName, Map<String, ReviwerRecommendationDataLoader> dataModelList){
        Map<String, Map<String, ArrayList<ReviewerRankingModel>>> modelDevScore = devScoreFullData.get(modelName);
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();

            ReviwerRecommendationDataLoader dataModel = dataModelList.get(projectName);
            for(String prTest : dataModel.getTrainTestSplits().getTestingPullRequestList()){
                ArrayList<ReviewerRankingModel> rankReviewers = modelDevScore.get(projectName).get(prTest);
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
            }
            RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
            evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            dataModel.getTrainTestSplits().getTestingPullRequestList(), 
            null, rankingResultList,
            ConstantUtil.REVIEW_REVIEW_EXP);
            recommendationResultList.add(evaluationRest);
            System.out.println("Project Complete Recommendation: " + projectName);

            evaluationRest.printAccuracyResult();
            evaluationRest.printMAP();

            System.out.println("-----------------");
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_score_evaluation_updated_July_26/" + modelName + ".csv";
        RecommendationUtil.writeRecResult(path, recommendationResultList);
            
    }

    public void updateRestRankPerModel(Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData,
    ReviwerRecommendationDataLoader dataModel, String prTest, String projectName,
    Map<String, Map<String,ArrayList<ReviewerRankingModel>>> resultRankedListPerModel){
        for(String modelName : studiedRecModels){
            ArrayList<ReviewerRankingModel> rankReviewers = devScoreFullData.get(modelName).get(projectName).get(prTest);
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
               resultRankedListPerModel.get(modelName).put(prTest, rankReviewers);
        }
    }

    public String getBestPerformanceModelForSingleTest(Map<String, Map<String,ArrayList<ReviewerRankingModel>>> resultRankedListPerModel,
    ReviwerRecommendationDataLoader dataModel, List<String> prTestList, String projectName){
        String bestModel = "";
        List<String> modelList = studiedRecModels;
        Map<String, Double> modelScoreList = new HashMap<String,Double>();

        for(String modelName : studiedRecModels){
            //System.out.println("Model: " + modelName);
            RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
            evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            prTestList, 
            null, resultRankedListPerModel.get(modelName),
            ConstantUtil.REVIEW_REVIEW_EXP);
            double score = evaluationRest.getSingleValueAccuracyMapAcrossRanks();
            modelScoreList.put(modelName, score);
        }
        modelList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (modelScoreList.get(o1) > modelScoreList.get(o2)){
                    return -1;
                }else if (modelScoreList.get(o1) < modelScoreList.get(o2)){
                    return 1;
                }
                return 0;
            }
        });
        bestModel = modelList.get(0);

        return bestModel;
    } 

    public String getTheModelFromFrequency(Map<String,Set<String>> freqBestModel){
        String modelName = "";
        List<String> modelList = studiedRecModels;
        modelList.sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(freqBestModel.get(o1).size() > freqBestModel.get(o2).size()){
                    return -1;
                }else if(freqBestModel.get(o1).size() < freqBestModel.get(o2).size()){
                    return 1;
                }
                return 0;
            }
            
        });
        modelName = modelList.get(0);
        return modelName;
    }
    public String getTheModelFromFrequencyLastTenPRs(ArrayList<String> perTestBestModel){
        Map<String,Integer>freqBestModel = new HashMap<String,Integer>();
        for(String modelName : studiedRecModels){
            freqBestModel.put(modelName, 0);
        }
        int startIndex = perTestBestModel.size() >= 10 ? perTestBestModel.size() - 10 : 0;
        for(int i = startIndex ; i < perTestBestModel.size() ; i ++){
            String bestModel = perTestBestModel.get(i);
            if(!freqBestModel.containsKey(bestModel)){
                freqBestModel.put(bestModel, 0);
            }
            freqBestModel.put(bestModel, freqBestModel.get(bestModel) + 1);
        }
        String modelName = "";
        List<String> modelList = studiedRecModels;
        modelList.sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(freqBestModel.get(o1) > freqBestModel.get(o2)){
                    return -1;
                }else if(freqBestModel.get(o1) < freqBestModel.get(o2)){
                    return 1;
                }
                return 0;
            }
            
        });
        modelName = modelList.get(0);
        return modelName;
    }

    public void printFreqModel(Map<String,Set<String>> freqBestModel, String prTest){
        for(String modelName : studiedRecModels){
            System.out.print(prTest + " " + modelName + "["+freqBestModel.get(modelName).size()+"] ");
        }
        System.out.println();
    }
    public void startAdaptiveModel(Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData,
    Map<String, ReviwerRecommendationDataLoader> dataModelList, String adaptiveModelName, String startModelName){
        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
        actualReviewerListPR = new HashMap<String,List<String>>();

        String startModelNameII = startModelName;
        Random random = new Random();
        int ind = random.nextInt((studiedRecModels.size() - 1) - 0) + 0;
        
        if(startModelName.compareTo("RAND") == 0){
            startModelNameII = studiedRecModels.get(ind);
        }
        String recomModelStart = startModelNameII;
        
        System.out.println("Start With: " + recomModelStart + " " + ind);
        Map<String,Map<String,Set<String>>> freqBestModelPerProject = new HashMap<String, Map<String, Set<String>>>();
        String prevModel = recomModelStart;
        for(String projectName : projectList){
            System.out.println("Adaptive Model For Project: " + projectName);
            String choosenModel = "";
            int total = 0;
            Map<String, Map<String,ArrayList<ReviewerRankingModel>>> resultRankedListPerModel = new HashMap<String, Map<String,ArrayList<ReviewerRankingModel>>>();
            ReviwerRecommendationDataLoader dataModel = dataModelList.get(projectName);
            Map<String,Set<String>> freqBestModel = new HashMap<String, Set<String>>(); 
            ArrayList<String> perTestBestModel = new ArrayList<String>();
            Map<String, ArrayList<ReviewerRankingModel>> adaptipeModelRankList = new HashMap<String,ArrayList<ReviewerRankingModel>>();
            for(String modelName : studiedRecModels){
                freqBestModel.put(modelName, new HashSet<String>());
                resultRankedListPerModel.put(modelName, new HashMap<String, ArrayList<ReviewerRankingModel>>());
            }
            List<String> prTestList = dataModel.getTrainTestSplits().getTestingPullRequestList();
            for(String prTest : prTestList){
                String testKey = projectName + "-" + prTest;
                updateRestRankPerModel(devScoreFullData,dataModel, prTest, projectName, resultRankedListPerModel);
                //Working
                /*for(String modelName : studiedRecModels){
                    System.out.println( "Result List SIZE: " + resultRankedListPerModel.get(modelName).size());
                }*/
                //System.out.println(resultRankedListPerModel.get(studiedRecModels.get(0)).size());
                if(total < 2){
                    choosenModel = recomModelStart;
                }else{
                    List<String> candTestCases = prTestList.subList(0, total);
                    String bestModel = getBestPerformanceModelForSingleTest(resultRankedListPerModel,dataModel,
                    candTestCases, projectName);
                    freqBestModel.get(bestModel).add(prTest);
                    perTestBestModel.add(bestModel);

                    String freqModel = getTheModelFromFrequency(freqBestModel);
                    
                    if(adaptiveModelName.compareTo(AD_FREQ) == 0){
                        choosenModel = freqModel;
                    }else if (adaptiveModelName.compareTo(AD_HYBRID) == 0){
                        String freqModelWithRecency = getTheModelFromFrequencyLastTenPRs(perTestBestModel);
                        choosenModel = freqModelWithRecency;
                    }else if(adaptiveModelName.compareTo(AD_REC) == 0){
                        choosenModel = prevModel;
                    }
                    prevModel = bestModel;
                }
                //System.out.println("Choosen Model: " + choosenModel);
                ArrayList<ReviewerRankingModel> chosenModelRank = devScoreFullData.get(choosenModel).get(projectName).get(prTest);
                chosenModelRank.sort(new Comparator<ReviewerRankingModel>() {
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
                perTestDevScore.put(testKey, chosenModelRank);
                adaptipeModelRankList.put(prTest, chosenModelRank);

                //ArrayList<ReviewerRankingModel> rankReviewers = devScoreFullData.get(key).get(projectName).get(prTest);
                
                ++total;
                printFreqModel(freqBestModel,prTest);
            }


            RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
            evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
            dataModel.getTrainTestSplits().getTestingPullRequestList(), 
            null, adaptipeModelRankList,
            "AdaptiveModel");

            recommendationResultList.add(evaluationRest);
          
            System.out.println("Adaptive model Project Complete Recommendation: " + projectName);
            evaluationRest.printAccuracyResult();
            evaluationRest.printMAP();

            for(String prTest: dataModel.getTrainTestSplits().getFullPullRequestList()){
                List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
                String testKey = projectName + "-" + prTest;
                actualReviewerListPR.put(testKey, actualReviewer);
            }

            System.out.println("-----------------");
            freqBestModelPerProject.put(projectName, freqBestModel);
            //break;
        }

        if(adaptiveModelName.compareTo(AD_FREQ) == 0){
            String resultPath = RecommendationUtil.RESULT_DIR + "ADFREQ-" + startModelName + ".csv";
            RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
            RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, AD_FREQ,actualReviewerListPR);
            RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,AD_FREQ, actualReviewerListPR);

        }else if (adaptiveModelName.compareTo(AD_HYBRID) == 0){
            String resultPath = RecommendationUtil.RESULT_DIR + "ADHYBRID-" + startModelName + ".csv";
            RecommendationUtil.writeRecResult(resultPath, recommendationResultList);  
            RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, AD_HYBRID,actualReviewerListPR);
            RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,AD_HYBRID, actualReviewerListPR);

        }else if (adaptiveModelName.compareTo(AD_REC) == 0){
            String resultPath = RecommendationUtil.RESULT_DIR + "ADREC-" + startModelName + ".csv";
            RecommendationUtil.writeRecResult(resultPath, recommendationResultList);    
            RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, AD_REC, actualReviewerListPR);
            RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,AD_REC, actualReviewerListPR);

        }
        
    }

    public void runModel(Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData,
    Map<String, ReviwerRecommendationDataLoader> dataModelList){
        for(String modelName : studiedRecModels){
            checkResutSimilarity(devScoreFullData, modelName ,dataModelList);
        }
    }

    public void generateAdaptiveModel(){

        long startTime = System.currentTimeMillis();

        Map<String, ReviwerRecommendationDataLoader> dataModelList = this.getDataModel(this.projectList);
        Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullData = this.readData(dataModelList, projectList);
        //Map<String, Map<String, Map<String,ArrayList<ReviewerRankingModel>>>> devScoreFullTrainingData = this.readTrainingData(dataModelList, projectList);
        //Map<String, Map<String, ArrayList<ReviewerRankingModel>>> desiredModelScore = devScoreFullData.get("CHREV");
        
        //runModel(devScoreFullData, dataModelList);
        /*for(String modelName : studiedRecModels){
            checkResutSimilarity(devScoreFullData, modelName ,dataModelList);
        }*/
        
        //startAdaptiveModel(devScoreFullData,dataModelList, true);
        /*for(String modelName : studiedRecModels){
            startAdaptiveModel(devScoreFullData,dataModelList, AD_FREQ, modelName);
            startAdaptiveModel(devScoreFullData,dataModelList, AD_REC, modelName);
            startAdaptiveModel(devScoreFullData,dataModelList, AD_HYBRID, modelName);
        }*/

        startAdaptiveModel(devScoreFullData,dataModelList, AD_FREQ, "RAND");
        startAdaptiveModel(devScoreFullData,dataModelList, AD_REC, "RAND");
        startAdaptiveModel(devScoreFullData,dataModelList, AD_HYBRID, "RAND");

        //startAdaptiveModel(devScoreFullData,dataModelList, false, "RAND");
        
        // Result is oKay checked with "KUREC" recommendation system
        //checkResutSimilarity(devScoreFullData,"KUREC",dataModelList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)+"] seconds");
    }
    public static void main(String[] args) {
        AdaptiveMetaHeuristicModel ob = new AdaptiveMetaHeuristicModel();
        ob.generateAdaptiveModel();
        System.out.println("Program finishes successfully.");
    }
}
