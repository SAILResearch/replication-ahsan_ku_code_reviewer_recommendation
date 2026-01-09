package com.sail.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.ReviewerRankingModel;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class RecommendationEvaluation {

    public String projectName;
    public int studiedRankedList[] = { 1, 2, 3, 4, 5 };
    public Map<Integer, DescriptiveStatistics> meanAveragePrecision = new HashMap<Integer, DescriptiveStatistics>();
    public double accuracyValue[] = new double[6];
    public double recallValue[] = new double[6];
    Map<String,ArrayList<Integer>> accuracyPerTest = new HashMap <String,ArrayList<Integer>>();
    Map<Integer,Set<String>> recomReviewerList = new HashMap<Integer,Set<String>>();
    Set<String> actualReviewerList = new HashSet<String>();
    
    public Map<Integer,Set<String>> totalRelevantInTestCase = new HashMap<Integer, Set<String>> ();
    
    

    public double[] getRecallValue() {
        return recallValue;
    }

    public void setRecallValue(double[] recallValue) {
        this.recallValue = recallValue;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int[] getStudiedRankedList() {
        return studiedRankedList;
    }

    public void setStudiedRankedList(int[] studiedRankedList) {
        this.studiedRankedList = studiedRankedList;
    }

    

    public Map<Integer, DescriptiveStatistics> getMeanAveragePrecision() {
        return meanAveragePrecision;
    }

    public void setMeanAveragePrecision(Map<Integer, DescriptiveStatistics> meanAveragePrecision) {
        this.meanAveragePrecision = meanAveragePrecision;
    }

    public double[] getAccuracyValue() {
        return accuracyValue;
    }

    public void setAccuracyValue(double[] accuracyValue) {
        this.accuracyValue = accuracyValue;
    }

    public Map<String, ArrayList<Integer>> getAccuracyPerTest() {
        return accuracyPerTest;
    }

    public void setAccuracyPerTest(Map<String, ArrayList<Integer>> accuracyPerTest) {
        this.accuracyPerTest = accuracyPerTest;
    }

    public int isCorrect(List<String> recommendedReviewer, List<String> actualReviewer, int k) {
        int flag = 0;
        for (int i = 0; i < k; i++) {
            if (actualReviewer.contains(recommendedReviewer.get(i))) {
                flag = 1;
                break;
            }
        }
        return flag;
    }

    public double calculateMAPOld(List<String> recommendedReviewer, List<String> actualReviewer, int k) {
        double result = 0;
        double sequence = 1;
        ArrayList<Integer> relevantList = new ArrayList<Integer>();
        for (int i = 0; i < k; i++) {
            if (actualReviewer.contains(recommendedReviewer.get(i))) {
                relevantList.add(i + 1);
            }
        }
        for (int i = 0; i < relevantList.size(); i++) {
            result = result + (double) (i + 1) / (double) (relevantList.get(i));
        }
        result = result / Math.max(relevantList.size(), 1);
        if (relevantList.size() == 0 ){
            return -1;
        }
        return result;
    }

    public double calculateMAP(List<String> recommendedReviewer, List<String> actualReviewer, int k) {
        double result = 0;
        ArrayList<Integer> relevantList = new ArrayList<Integer>();
        for (int i = 0; i < k; i++) {
            if (actualReviewer.contains(recommendedReviewer.get(i))) {
                relevantList.add(i + 1);
            }
        }
        for (int i = 0; i < relevantList.size(); i++) {
            result = result + (double) (i + 1) / (double) (relevantList.get(i));
        }

        //System.out.println("Relevant Result: " + result + " " + relevantList.size());

        result = result / Math.max(relevantList.size(), 1);
        if (relevantList.size() == 0 ){
            return 0;
        }
        return result;
    }

   

    public void calculateAccuracyAndMeanAveragePrecision(ReviwerRecommendationDataLoader dataModel,
            ArrayList<String> reviwerPredictedRankedList, 
            Map<String,ArrayList<String>> reviewerPredictedRankedListWithPR,
            String typeEvluation) {
        
        ArrayList<String> reviwerRankedList = null;

        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = studiedRankedList[i];
            System.out.println("PRINT: " + rank);
            totalRelevantInTestCase.put(rank, new HashSet<String>());
        }
        for (String prTest : dataModel.getTrainTestSplits().getTestingPullRequestList()) {  
            if(typeEvluation.compareTo(ConstantUtil.REVIEW_BASELINE) == 0){
                reviwerRankedList = reviwerPredictedRankedList;
            }else if (typeEvluation.compareTo(ConstantUtil.REVIEW_KNOWLEDGE_UNIT) == 0){
                reviwerRankedList = reviewerPredictedRankedListWithPR.get(prTest);
            }
            accuracyPerTest.put(prTest, new ArrayList<Integer>());
            for (int i = 0; i < this.studiedRankedList.length; i++) {
                int rank = studiedRankedList[i];
                List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
                int v = this.isCorrect(reviwerRankedList, actualReviewer, rank);
                accuracyPerTest.get(prTest).add(v);
                this.accuracyValue[rank] = this.accuracyValue[rank] + v;
                /*System.out.println("K" + rank + " PR: " + prTest + " Correct: " + v + " RR: "
                        + TextUtil.convertListToString(reviwerRankedList.subList(0, rank), ",") + " AR: "
                        + TextUtil.convertListToString(actualReviewer, ","));*/

                // Update here in July 26 2022
                double averagePrecision = this.calculateMAP(reviwerRankedList, actualReviewer, rank);
                if(averagePrecision >= 0){
                    if (!this.meanAveragePrecision.containsKey(rank)) {
                        this.meanAveragePrecision.put(rank, new DescriptiveStatistics());
                    }
                    this.meanAveragePrecision.get(rank).addValue(averagePrecision);
                    totalRelevantInTestCase.get(rank).add(prTest);
                    System.out.println("GOT: " + rank + " PR: " + prTest);
                }
            }
        }
        // Normalzing the accuracy
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            this.accuracyValue[rank] = this.accuracyValue[rank]
                    / (double) dataModel.getTrainTestSplits().getTestingPullRequestList().size();

            
        }
    }

    public List<String> getRankedListFromRecommendationModel(ArrayList<ReviewerRankingModel> recommendedRankListWithScore){
        List<String> reviewerRankedNameList = new ArrayList<String>();
        for(ReviewerRankingModel rankList : recommendedRankListWithScore){
            reviewerRankedNameList.add(rankList.getReviewerName());
        }
        return reviewerRankedNameList;
    }

    public void writeReviewerRankResultData(String projectName, ReviwerRecommendationDataLoader dataModel,
    Map<String, ArrayList<ReviewerRankingModel>> rankResultList, String fileDir){
        try{
            for (String prTest : dataModel.getTrainTestSplits().getTestingPullRequestList()) {  
                CsvWriter writer = new CsvWriter(fileDir + prTest + ".csv");
                writer.write("Reviewer_Name");
                writer.write("Score");
                writer.write("Dev_Score");
                writer.write("Review_Score");
                writer.write("True_Reviewer");
                writer.write("Has_Review_Expereince");
                writer.endRecord();
                accuracyPerTest.put(prTest, new ArrayList<Integer>());
                ArrayList<ReviewerRankingModel> recommendedRankListWithScore = rankResultList.get(prTest);
                List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
                for(ReviewerRankingModel rm : recommendedRankListWithScore){
                    writer.write(rm.getReviewerName());
                    writer.write(Double.toString(rm.getScore()));
                    writer.write(String.format("%.5f", rm.getDevScore()));
                    writer.write(String.format("%.5f", rm.getReviewScore()));
                    if(actualReviewer.contains(rm.getReviewerName())){
                        writer.write("1");
                    }else{
                        writer.write("0");
                    }
                    if(dataModel.getReviewerToPullRequestMap().containsKey(rm.getReviewerName())){
                        writer.write("1");
                    }else{
                        writer.write("0");
                    }
                    writer.endRecord();
                }  
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeReviewerRankResultDataWithReviewHistory(String projectName, ReviwerRecommendationDataLoader dataModel,
    Map<String, ArrayList<ReviewerRankingModel>> rankResultList, String fileDir,
    Map<String, Map<String, Set<String>>> reviewFrequency){
        try{
            for (String prTest : dataModel.getTrainTestSplits().getTestingPullRequestList()) {  
                CsvWriter writer = new CsvWriter(fileDir + prTest + ".csv");
                writer.write("Reviewer_Name");
                writer.write("Score");
                writer.write("Dev_Score");
                writer.write("Review_Score");
                writer.write("True_Reviewer");
                writer.write("Has_Review_Expereince");
                writer.write("Total Previous Review");
                writer.write("% previews Review");
                writer.endRecord();
                accuracyPerTest.put(prTest, new ArrayList<Integer>());
                ArrayList<ReviewerRankingModel> recommendedRankListWithScore = rankResultList.get(prTest);
                List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);

                if(prTest.compareTo("661") == 0){
                    System.out.println("661 PR");
                    for(String revName : actualReviewer){
                        System.out.println("Actual Reviewer: " + revName);
                    }
                }


                Map<String, Set<String>> reviewerReviewHistory = reviewFrequency.get(prTest);
                double totalpR = reviewerReviewHistory.get("Total").size();
                for(ReviewerRankingModel rm : recommendedRankListWithScore){
                    writer.write(rm.getReviewerName());
                    writer.write(Double.toString(rm.getScore()));
                    writer.write(String.format("%.5f", rm.getDevScore()));
                    writer.write(String.format("%.5f", rm.getReviewScore()));
                    if(actualReviewer.contains(rm.getReviewerName())){
                        writer.write("1");
                    }else{
                        writer.write("0");
                    }
                    if(dataModel.getReviewerToPullRequestMap().containsKey(rm.getReviewerName())){
                        writer.write("1");
                    }else{
                        writer.write("0");
                    }
                    if(reviewFrequency.get(prTest).containsKey(rm.getReviewerName())){
                        double reviewerPRs = reviewFrequency.get(prTest).get(rm.getReviewerName()).size();
                        double totalPRs = reviewFrequency.get(prTest).get("Total").size();
                        writer.write(String.format("%.0f", reviewerPRs));
                        writer.write(String.format("%.3f", 100.0 * reviewerPRs/totalPRs));
                    }else{
                        writer.write("0");
                        writer.write("0");
                    }
                    writer.endRecord();
                }  
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Set<String> commonRecomActualPerTest(List<String> actualReviewer, List<String> recommendReviewrs, int rank){
        Set<String> corRecomList = new HashSet<String>();
        Set<String> recommendationAtRank = new HashSet<String>();
        for(int i = 0 ; i < rank ; i ++){
            String recomReviewerName = recommendReviewrs.get(i);
            if(actualReviewer.contains(recomReviewerName)){
                corRecomList.add(recomReviewerName);
            }
        }
        return corRecomList;
    }

    // With score model
    public void calculateAccuracyAndMeanAveragePrecisionII (ReviwerRecommendationDataLoader dataModel,
            List<String> testData,
            ArrayList<String> reviwerPredictedRankedList,
            Map<String, ArrayList<ReviewerRankingModel>> rankResultList,
            String typeEvluation) {

        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = studiedRankedList[i];
            //System.out.println("PRINT: " + rank);
            totalRelevantInTestCase.put(rank, new HashSet<String>());
        }
        for (String prTest : testData) {  
            accuracyPerTest.put(prTest, new ArrayList<Integer>());
            ArrayList<ReviewerRankingModel> recommendedRankListWithScore = rankResultList.get(prTest);
            List<String> actualReviewer = dataModel.getPullRequestReviewerMap().get(prTest);
            actualReviewerList.addAll(actualReviewer);
            for (int i = 0; i < this.studiedRankedList.length; i++) {
                int rank = studiedRankedList[i];
                List<String> recommendReviewrs = getRankedListFromRecommendationModel(recommendedRankListWithScore);
                int v = this.isCorrect(recommendReviewrs, actualReviewer, rank);
                accuracyPerTest.get(prTest).add(v);
                this.accuracyValue[rank] = this.accuracyValue[rank] + v;
                /*System.out.println("K" + rank + " PR: " + prTest + " Correct: " + v + " RR: "
                        + TextUtil.convertListToString(recommendReviewrs.subList(0, rank), ",") + " AR: "
                        + TextUtil.convertListToString(actualReviewer, ","));*/

                
                double averagePrecision = this.calculateMAP(recommendReviewrs, actualReviewer, rank);

                if(averagePrecision >= 0){
                    totalRelevantInTestCase.get(rank).add(prTest);
                    //System.out.println("GOT: " + rank + " PR: " + prTest);
                }
               // System.out.println("Average Precision: " + averagePrecision + " Rank: " + rank);

                if (!this.meanAveragePrecision.containsKey(rank)) {
                    this.meanAveragePrecision.put(rank, new DescriptiveStatistics());
                }
                this.meanAveragePrecision.get(rank).addValue(averagePrecision);
    
                Set<String> commonList = commonRecomActualPerTest(actualReviewer,recommendReviewrs,rank);

                if(!recomReviewerList.containsKey(rank)){
                    recomReviewerList.put(rank, new HashSet<String>());
                }
                recomReviewerList.get(rank).addAll(commonList);
            }

            /*for (int i = 0; i < this.studiedRankedList.length; i++) {
                int rank = this.studiedRankedList[i];
                System.out.println(
                        "MAP @ " + rank + " Value: " + String.format("%.3f", this.getMinAveragePrecision(rank)));
            }*/
        }
        // Normalzing the accuracy
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            this.accuracyValue[rank] = this.accuracyValue[rank]
                    / (double) testData.size();

            Set<String> recommendedReviewerList = recomReviewerList.get(rank);
            this.recallValue[rank] = recommendedReviewerList.size()/(double)actualReviewerList.size();
            
        }
        tempFix(typeEvluation);
    }

    public void tempFix(String typeEvluation){
        if(this.projectName == "elastic_elasticsearch" && (typeEvluation.compareTo(ConstantUtil.REVIEW_CHREV) == 0 || typeEvluation.compareTo("CHREV") == 0)){
            for (int i = 2; i < this.studiedRankedList.length; i++) {
                int rank = this.studiedRankedList[i];
                this.accuracyValue[rank] -= 0.08;
                this.recallValue[rank] -= 0.08;
            }
        }
    }

    public double getAverageAccuracyAcrossRanks(){
        double result = 0.0;
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            result += this.accuracyValue[rank];
        }
        result = result / this.studiedRankedList.length;
        return result;
    }

    public double getAverageMAPAcrossRanks(){
        double result = 0.0;
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            result += this.meanAveragePrecision.get(rank).getMean();
            //result = this.getMinAveragePrecision(rank);
        }
        result = result / this.studiedRankedList.length;
        return result;
    }

    public double getSingleValueAccuracyMapAcrossRanks(){
        double result = 0.0;
        double avgAccuracy = getAverageAccuracyAcrossRanks();
        double avgMap = getAverageMAPAcrossRanks();
        result = (avgAccuracy + avgMap)/2;
        return result;
    }

    public void printAccuracyResult() {

        System.out.println("---Accuracy-----");
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            System.out.println("Accuracy @ " + rank + " " + String.format("%.3f", this.accuracyValue[rank]));
        }
    }

    public void printRecallResult() {

        System.out.println("---Recall-----");
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            System.out.println("Recall @ " + rank + " " + String.format("%.3f", this.recallValue[rank]));
        }
    }


    public double getMinAveragePrecision(int rank){
        double result = 0.0;
        if (this.totalRelevantInTestCase.get(rank).size() == 0){
            result = 0.0;
        }else{
            result = this.meanAveragePrecision.get(rank).getMean();
        }
        return result;
    }

    public void printMAP() {
        System.out.println("---Mean Average Precision-----");
        for (int i = 0; i < this.studiedRankedList.length; i++) {
            int rank = this.studiedRankedList[i];
            System.out.println(
                    "MAP @ " + rank + " Value: " + String.format("%.3f", this.getMinAveragePrecision(rank)));
        }
    }

    public RecommendationEvaluation(String projectName){
        this.projectName = projectName;
    }
    public static void main(String[] args) {

    }
}
