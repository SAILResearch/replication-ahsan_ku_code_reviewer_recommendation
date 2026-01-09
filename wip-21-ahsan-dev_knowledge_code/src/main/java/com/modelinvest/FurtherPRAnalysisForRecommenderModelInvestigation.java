package com.modelinvest;

import java.lang.invoke.TypeDescriptor.OfMethod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.DataModel;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PullRequestModel;



public class FurtherPRAnalysisForRecommenderModelInvestigation {

    //public String scratchDataLocBackup = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/scratch_dev_ku_data/Result";
    public String scratchDataLocBackup = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/Result/";
    public String modelScorePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
    
    List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    "apache_hbase","apache_hive", "apache_storm","apache_wicket","elastic_elasticsearch");

    //List<String> projectList = Arrays.asList("apache_activemq","apache_groovy");
    
    List<String> investModels = Arrays.asList("KUREC","CHREV","ER","RF","CF");

    List<String> investModelsWithCombined = Arrays.asList("KUREC","CHREV","ER","RF","CF","CN","SOFIA","CORRECT","AD_FREQ", "AD_REC", "AD_HYBRID");

    //For testing the program
    //List<String> projectList = Arrays.asList("apache_activemq");    
    //List<String> investModels = Arrays.asList("KUREC");

    public Map<String,Map<String,PrScoreModel>> readScoreSummaryPerPr(String modelName){
        Map<String,Map<String,PrScoreModel>> modelSummaryResult = new HashMap<String,Map<String,PrScoreModel>>();
        String path = String.format("%saccuracy_per_test_%s.csv", modelScorePath, modelName);
        try{
            CsvReader reader = new CsvReader(path);
            reader.readHeaders();
            while(reader.readRecord()){
                String projectName = reader.get("ProjectName");
                String prNumber = reader.get("PrNumber");
                int rank1 = Integer.parseInt(reader.get("K=1"));
                int rank2 = Integer.parseInt(reader.get("K=2"));
                int rank3 = Integer.parseInt(reader.get("K=3"));
                int rank4 = Integer.parseInt(reader.get("K=4"));
                int rank5 = Integer.parseInt(reader.get("K=5"));
                PrScoreModel scoreModel = new PrScoreModel();
                scoreModel.setPrNumber(prNumber);
                scoreModel.setRank1(rank1);
                scoreModel.setRank2(rank2);
                scoreModel.setRank3(rank3);
                scoreModel.setRank4(rank4);
                scoreModel.setRank5(rank5);

                if(!modelSummaryResult.containsKey(projectName)){
                    modelSummaryResult.put(projectName, new HashMap<String,PrScoreModel>());
                }
                modelSummaryResult.get(projectName).put(prNumber, scoreModel);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Finish Reading Model Summary: " + modelName);
        return modelSummaryResult;
        
    }

    public int getNewChangedFiles(List<String> searchSpace, List<String> searchItems){
        int newFiles = 0;
        int matchedFile = getMatchedFiles(searchSpace, searchItems);
        newFiles = searchItems.size() - matchedFile;
        return newFiles;
    }

    public int getNewChangedFilesUpdate(List<String> searchSpace, List<String> searchItems){
        int newFiles = 0;
        int matchedFile = getMatchedFiles(searchSpace, searchItems);
        newFiles = searchItems.size() - matchedFile;
        return newFiles;
    }

    public List<String> getSetToList(Set<String>values){
        List<String> arrayList = new ArrayList<String>();
        for(String v : values){
            arrayList.add(v);
        }
        return arrayList;
    }

    public int getMatchedFiles(List<String> searchSpace, List<String> searchItems){
        int matchedFiles = 0;
        for(String v : searchItems){
            if(searchSpace.contains(v)){
                matchedFiles ++;
            }
        }
        return matchedFiles;
    }

    //List<String> projectList = Arrays.asList("apache_activemq","apache_groovy","apache_lucene",
    //"apache_hbase","apache_hive", "apache_storm","apache_wicket","elastic_elasticsearch");


    public Map<String,Map<String, Map<String,List<RecommendationScoreModel>>>> readRecommendationScorePerProject(String path, Map<String,ReviwerRecommendationDataLoader> dataLoaderList,
    List<String> investModels){
        Map<String,Map<String, Map<String,List<RecommendationScoreModel>>>> recomScoreList = new HashMap<String,Map<String,Map<String,List<RecommendationScoreModel>>>>();
        for(String modelName : investModels){
            recomScoreList.put(modelName, new HashMap<String,Map<String,List<RecommendationScoreModel>>>());
            for(String projectName : projectList){
                recomScoreList.get(modelName).put(projectName, new HashMap<String,List<RecommendationScoreModel>>());
                for(String prNumber : dataLoaderList.get(projectName).getTrainTestSplits().getTestingPullRequestList()){
                    recomScoreList.get(modelName).get(projectName).put(prNumber, new ArrayList<RecommendationScoreModel>());
                    try{
                        String scorePath = path + "/" + modelName + "/" + projectName + "/" + projectName + "-" + prNumber + ".csv";
                        CsvReader reader = new CsvReader(scorePath);
                        reader.readHeaders();
                        while(reader.readRecord()){
                            String reviewerName = reader.get("dev_name");
                            double score = Double.parseDouble(reader.get("score"));
                            String booleanString = reader.get("IsTrueReviewer");
                            boolean isTrueReviewer = false;
                            if(booleanString.compareTo("T") == 0){
                                isTrueReviewer = true;
                            }
                            RecommendationScoreModel scoreModel = new RecommendationScoreModel(reviewerName, score, isTrueReviewer);
                            recomScoreList.get(modelName).get(projectName).get(prNumber).add(scoreModel);
                           // System.out.println(" BOOLEAN: " + isTrueReviewer + " " + reader.get("IsTrueReviewer"));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    recomScoreList.get(modelName).get(projectName).get(prNumber).sort(new Comparator<RecommendationScoreModel>() {

                        @Override
                        public int compare(RecommendationScoreModel ob1, RecommendationScoreModel ob2) {
                            if(ob1.getScore() < ob2.getScore()){
                                return 1;
                            }else if (ob1.getScore() > ob2.getScore()){
                                return -1;
                            }
                            return 0;
                        }
                        
                    });
                }
            }
        }
        System.out.println("<<< Score Model Loading Done! >>>");
        return recomScoreList;
    }

    public void studyReviewerRankMissing(int topRank, List<String>investModels){
        Map<String, Map<String,Map<String,PrScoreModel>>> modelSummary = new HashMap<String,Map<String,Map<String,PrScoreModel>>>();
        Map<String,Map<String,List<String>>> missingPRsTopFive = new HashMap<String,Map<String,List<String>>>();
        Map<String,ReviwerRecommendationDataLoader> dataLoaderList = new HashMap<String,ReviwerRecommendationDataLoader>();
        for(String modelName : investModels){
            modelSummary.put(modelName, readScoreSummaryPerPr(modelName));
            missingPRsTopFive.put(modelName, new HashMap<String,List<String>>());
            for(String projectName : projectList){
                missingPRsTopFive.get(modelName).put(projectName, new ArrayList<String>());
            }
        }
        for(String projectName : projectList){
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            
            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            dataLoaderList.put(projectName, dataModel);
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/";
        Map<String,Map<String,Map<String,List<RecommendationScoreModel>>>> recomScoreList = readRecommendationScorePerProject(path,dataLoaderList, investModels);
   
        String outPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Major-EMSE/RecommenderResults/Testing/RessonableReviewerAnalysis";
        try{
            int total = 0;
            int problem = 0;
            for(String modelName : investModels){
                String filePath = String.format("%s/%s_FULL_top_rank%s.csv",outPath, modelName, topRank);  
                CsvWriter writer = new CsvWriter(filePath);
                writer.write("ModelName");
                writer.write("ProjectName");
                writer.write("PrNumber");
                writer.write("ReviewerName");
                writer.write("ReviewerScore");
                writer.write("RankInModel");
                writer.endRecord();

                for(String projectName : projectList){
                    for(String prNumber : dataLoaderList.get(projectName).getTrainTestSplits().getTestingPullRequestList()){
                       PrScoreModel prRankingResult = modelSummary.get(modelName).get(projectName).get(prNumber);
                       
                       // Model fail to recommend correct reviewers.

                        if(topRank == 5){
                            if(prRankingResult.getRankOneToFive() > 0){
                                continue;
                            }
                        }

                        if(topRank == 1){
                            if(prRankingResult.getRank1() > 0){
                                continue;
                            }
                        }
                        ++total;
                        List<RecommendationScoreModel> reviewerScoreList = recomScoreList.get(modelName).get(projectName).get(prNumber);
                        int firstCorrectReviewerIndex = getFirstRankedIndexedOfCorrectReviewer(reviewerScoreList);

                        if(firstCorrectReviewerIndex == -1){
                            System.out.println(modelName + " " + projectName + " PROBLEM");
                            ++problem;
                            continue;
                        }
                        writer.write(modelName);
                        writer.write(projectName);
                        writer.write(prNumber);
                        writer.write(reviewerScoreList.get(firstCorrectReviewerIndex).getReviewerName());
                        writer.write(String.format("%.4f", reviewerScoreList.get(firstCorrectReviewerIndex).getScore()));
                        writer.write(Integer.toString(firstCorrectReviewerIndex + 1));
                        writer.endRecord();
                    }
                    System.out.println(String.format("Done Model: [%s] Project: [%s] Top Rank [%s]", modelName,projectName, topRank));
                }
                writer.close();
            }
            
            System.out.println("Total: " + total + " Problem: " + problem);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getFirstRankedIndexedOfCorrectReviewer(List<RecommendationScoreModel> reviewerScoreList){
        int indexValue = -1;
        for(int i = 0 ; i < reviewerScoreList.size() ; i ++){
            if(reviewerScoreList.get(i).isTrueReviewer){
                indexValue = i;
                return indexValue;
            }
        }
        return indexValue;
    }

    public void studyNewReviewerForPR(){
        Map<String,Map<String,PrScoreModel>> projectModelResult = new HashMap<String,Map<String,PrScoreModel>>();
        Map<String, Map<String,Map<String,PrScoreModel>>> modelSummary = new HashMap<String,Map<String,Map<String,PrScoreModel>>>();
        Map<String,List<String>> prHasNewReviewers = new HashMap<String,List<String>>();
        
        double thresholdReviewer = 50;
        for(String modelName : investModels){
            modelSummary.put(modelName, readScoreSummaryPerPr(modelName));
        }
        for(String pName : projectList){
            prHasNewReviewers.put(pName, new ArrayList<String>());
        }
        for(String projectName : projectList){
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            
            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            
            Map<String,List<String>> reviewerPerPullRequest = dataModel.getPullRequestReviewerMap();
            List<String> prList = dataModel.getTrainTestSplits().getTrainintPullRequestList();
            List<String> prTestList = dataModel.getTrainTestSplits().getTestingPullRequestList();
        
            String previousPR = null;
            Map<String,List<String>> reviewerTraining = new HashMap<String,List<String>>();
            Map<String,List<String>> prPreviousReviewers = new HashMap<String,List<String>>();
            Set<String> reviewerList = new HashSet<String>();
            for(String prNumber : prList){
                List<String> reviewerListPerPr = reviewerPerPullRequest.get(prNumber);
                for(String rName : reviewerListPerPr){
                    if(!reviewerTraining.containsKey(rName)){
                        reviewerTraining.put(rName, new ArrayList<String>());
                    }
                    reviewerTraining.get(rName).add(prNumber);
                }
                if(previousPR != null){
                    prPreviousReviewers.put(prNumber, new ArrayList<String>());
                    for(String changeFileName : reviewerList){
                        prPreviousReviewers.get(prNumber).add(changeFileName);
                    }
                }else{
                    previousPR = prNumber;
                    prPreviousReviewers.put(prNumber, new ArrayList<String>());
                }
                reviewerList.addAll(reviewerListPerPr);
                //System.out.println(reviewerList.size());
            }

            for(String prNumber : prTestList){
                List<String> reviewersForPullRequest = reviewerPerPullRequest.get(prNumber);
                //int newReviewers = getNewChangedFiles(prPreviousReviewers.get(prNumber),reviewersForPullRequest);
                int newReviewers = getNewChangedFilesUpdate(getSetToList(reviewerTraining.keySet()),reviewersForPullRequest);
                double prChangedReviewers = 100 * (double) newReviewers/reviewersForPullRequest.size();
                
                if(prChangedReviewers > thresholdReviewer){
                    if(!prHasNewReviewers.containsKey(projectName)){
                        prHasNewReviewers.put(projectName, new ArrayList<String>());
                    }
                    prHasNewReviewers.get(projectName).add(prNumber);
                    System.out.println( projectName + " " + "Total Changed Reviewers: " + reviewersForPullRequest.size() + " New Changed Reviewers: " + String.format("%.2f", prChangedReviewers));
                
                }
            }
        }
        for(String modelName : investModels){
            projectModelResult.put(modelName, new HashMap<String,PrScoreModel>());
            for(String projectName : projectList){
                //System.out.println("PROJECT: " + projectName);
                PrScoreModel modelPerformance = new PrScoreModel();
                Map<String,PrScoreModel> modelResult = modelSummary.get(modelName).get(projectName);
                int newReviewers = prHasNewReviewers.get(projectName).size();
                //System.out.println("Project: " + projectName + " New Reviewrs: " + newReviewers);
                for(String prNumber: prHasNewReviewers.get(projectName)){
                    updatePerformanceResult(prNumber, modelPerformance, modelResult);
                }
                modelPerformance.setTotalPRs(newReviewers);
                projectModelResult.get(modelName).put(projectName, modelPerformance);
            }
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/NewReviewerAnalysis/";
        writeResultFull(projectModelResult, thresholdReviewer, path, "result_model_new_reviewers_");

    }


    public void studyNewFilesPR(){
               
        double thresholdFileChange = 50;
        Map<String,List<String>> prHasNewChangedFiles = new HashMap<String,List<String>>();
        Map<String, Map<String,Map<String,PrScoreModel>>> modelSummary = new HashMap<String,Map<String,Map<String,PrScoreModel>>>();

        for(String modelName : investModels){
            modelSummary.put(modelName, readScoreSummaryPerPr(modelName));
        }

        //Map<String,Map<String,PrScoreModel>> kurecModelSummary = readScoreSummaryPerPr("KUREC");
        //Map<String,Map<String,PrScoreModel>> chrevModelSummary = readScoreSummaryPerPr("CHREV");

        Map<String,Map<String,PrScoreModel>> projectModelResult = new HashMap<String,Map<String,PrScoreModel>>();
        Map<String,PrScoreModel> projectKurecResult = new HashMap<String,PrScoreModel>();
        Map<String,PrScoreModel> projectChrevResult = new HashMap<String,PrScoreModel>();


        for(String projectName : projectList){
            String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            
            Map<String,List<String>> prChangedFileList = dataModel.getPrChangedFileList();
            Map<String, PullRequestModel> prModelList = dataModel.getPullRequestList();

            List<String> prList = dataModel.getTrainTestSplits().getFullPullRequestList();
            List<String> prTestList = dataModel.getTrainTestSplits().getTestingPullRequestList();

            String previousPR = null;
            Map<String,List<String>> prPreviousFileChangeTrack = new HashMap<String,List<String>>();
            Set<String> changeFileSet = new HashSet<String>();
            for(String prNumber : prList){
                //PullRequestModel prModel = prModelList.get(prNumber);
                //System.out.println(prModel.getPrCreatedJodaTime());
                List<String> fileChanged = prChangedFileList.get(prNumber);
                if(previousPR != null){
                    prPreviousFileChangeTrack.put(prNumber, new ArrayList<String>());
                    for(String changeFileName : changeFileSet){
                        prPreviousFileChangeTrack.get(prNumber).add(changeFileName);
                    }
                }else{
                    previousPR = prNumber;
                    prPreviousFileChangeTrack.put(prNumber, new ArrayList<String>());
                }
                changeFileSet.addAll(fileChanged);
            }

            
            for(String prNumber : prTestList){
                List<String> fileChanged = prChangedFileList.get(prNumber);
                int newChangedFile = getNewChangedFiles(prPreviousFileChangeTrack.get(prNumber),fileChanged);
                double perChangedFile = 100 * (double) newChangedFile/fileChanged.size();
                //System.out.println("Total Changed File: " + fileChanged.size() + " New Changed File: " + String.format("%.2f", perChangedFile));
                
                if(perChangedFile > thresholdFileChange){
                    if(!prHasNewChangedFiles.containsKey(projectName)){
                        prHasNewChangedFiles.put(projectName, new ArrayList<String>());
                    }
                    prHasNewChangedFiles.get(projectName).add(prNumber);
                }
            }
        }


        for(String modelName : investModels){
            projectModelResult.put(modelName, new HashMap<String,PrScoreModel>());
            for(String projectName : projectList){
                System.out.println("PROJECT: " + projectName);
                PrScoreModel modelPerformance = new PrScoreModel();
                Map<String,PrScoreModel> modelResult = modelSummary.get(modelName).get(projectName);
                int prHasNewFile = prHasNewChangedFiles.get(projectName).size();
                for(String prNumber: prHasNewChangedFiles.get(projectName)){
                    updatePerformanceResult(prNumber, modelPerformance, modelResult);
                }
                modelPerformance.setTotalPRs(prHasNewFile);
                projectModelResult.get(modelName).put(projectName, modelPerformance);
            }
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/NewFileRecomAnalysis/";
         
        writeResultFull(projectModelResult, thresholdFileChange, path, "result_model_invest_new_files_");

    }

    public void writeResultFull(Map<String, Map<String, PrScoreModel>> projectModelResult, double threshold, String path, String name) {

        for (String modelName : investModels) {
            String csvPath = path + name  + modelName + "_th_" + String.format("%.2f", threshold) + ".csv";
        
            Map<String, PrScoreModel> performanceResult = projectModelResult.get(modelName);
            try {
                CsvWriter writer = new CsvWriter(csvPath);
                writer.write("ProjectName");
                writer.write("K=1");
                writer.write("K=2");
                writer.write("K=3");
                writer.write("K=4");
                writer.write("K=5");
                writer.write("TotalCase");
                writer.endRecord();
                for (String projectName : projectList) {
                    writer.write(projectName);
                    writer.write(Integer.toString(performanceResult.get(projectName).getRank1()));
                    writer.write(Integer.toString(performanceResult.get(projectName).getRank2()));
                    writer.write(Integer.toString(performanceResult.get(projectName).getRank3()));
                    writer.write(Integer.toString(performanceResult.get(projectName).getRank4()));
                    writer.write(Integer.toString(performanceResult.get(projectName).getRank5()));
                    writer.write(Integer.toString(performanceResult.get(projectName).getTotalPRs()));
                    writer.endRecord();
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updatePerformanceResult(String prNumber,
            PrScoreModel kurecPerformance, Map<String, PrScoreModel> kurecResult) {

        PrScoreModel prResult = kurecResult.get(prNumber);
        
        kurecPerformance.setRank1(prResult.getRank1() + kurecPerformance.getRank1());
        kurecPerformance.setRank2(prResult.getRank2() + kurecPerformance.getRank2());
        kurecPerformance.setRank3(prResult.getRank3() + kurecPerformance.getRank3());
        kurecPerformance.setRank4(prResult.getRank4() + kurecPerformance.getRank4());
        kurecPerformance.setRank5(prResult.getRank5() + kurecPerformance.getRank5());

    }

    public void newReviewerStatistics(){
        for(String projectName : projectList){
            String prFilePath           =   scratchDataLocBackup    + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath     =   scratchDataLocBackup    + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath    =   scratchDataLocBackup    + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath       =   scratchDataLocBackup    + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            
            Map<String,List<String>> prChangedFileList = dataModel.getPrChangedFileList();
            Map<String, PullRequestModel> prModelList = dataModel.getPullRequestList();

            List<String> prListTraining = dataModel.getTrainTestSplits().getTrainintPullRequestList();
            List<String> prTestList = dataModel.getTrainTestSplits().getTestingPullRequestList();
            Map<String,List<String>> reviewerPerPullRequest = dataModel.getPullRequestReviewerMap();
            
            Map<String,List<String>> reviewerTraining = new HashMap<String,List<String>>();
            for(String prNumber : prListTraining){
                List<String> reviewerListPerPr = reviewerPerPullRequest.get(prNumber);
                for(String rName : reviewerListPerPr){
                    if(!reviewerTraining.containsKey(rName)){
                        reviewerTraining.put(rName, new ArrayList<String>());
                    }
                    reviewerTraining.get(rName).add(prNumber);
                }
            }
            Set<String> newReviewerList = new HashSet<String>();
            for(String prNumber : prTestList){
                List<String> reviewerListPerPr = reviewerPerPullRequest.get(prNumber);
                int newReviewers = getNewChangedFilesUpdate(getSetToList(reviewerTraining.keySet()),reviewerListPerPr);
                double prChangedReviewers = 100 * (double) newReviewers/reviewerListPerPr.size();
                
                for(String revName : reviewerListPerPr){
                    if(!reviewerTraining.containsKey(revName)){
                        newReviewerList.add(revName);
                        System.out.println("Project: " + projectName + " New Reviewer: " + newReviewerList.size() + " Percentage: " + prChangedReviewers);

                    }
                }
               
            }
            //System.out.println("Project: " + projectName + " New Reviewer: " + newReviewerList.size());

            
        }
    }


    /*public void findNewFileList(String prNum, ReviwerRecommendationDataLoader dataModel){
        List<String> javaChangedFiles = dataModel.getPrChangedFileList().get(prNum);
        ArrayList<GitCommitModel> gitCommitList = dataModel.getGitCommitList();
        PullRequestModel prModel = dataModel.getPullRequestList().get(prNum);

        Set<String> prevJavaFile = new HashSet<String>();

        for(GitCommitModel commit : gitCommitList){
            if(commit.getCommitJodaDate().isAfter(prModel.getPrCreatedJodaTime())){
                break;
            }
            List<String> changedFileList = commit.getChangedJavaFileList();
           

        }
    }*/

    public void studyNewFilesInPRTraining() throws Exception{

        CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/NewFileRecomAnalysis/newFilePRTraining.csv");
        writer.write("ProjectName");
        writer.write("TotalTrainingPR");
        writer.write("PRWithMajorityNewFiles");
        writer.write("PercentagePRWithNewFiles");
        writer.endRecord();

        //List<String> projectList = Arrays.asList("apache_activemq");
    
        for(String projectName : projectList){
            String prFilePath           =   scratchDataLocBackup    + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath     =   scratchDataLocBackup    + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath    =   scratchDataLocBackup    + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath       =   scratchDataLocBackup    + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);

            List<String> trainingDataset = dataModel.getTrainTestSplits().getTrainintPullRequestList();

            Set<String> previouslyReviewedFileList = new HashSet<String>();

            Set<String> prContainingMajorityNewFiles = new HashSet<String>();

            for(String prTraining : trainingDataset){
               PullRequestModel prModel = dataModel.getPullRequestList().get(prTraining);
               List<String> javaChangedFilesReview = dataModel.getPrChangedFileList().get(prTraining);

               double newFileForReview = 0.0;

               for(String fileName : javaChangedFilesReview){
                    if(!previouslyReviewedFileList.contains(fileName)){
                        newFileForReview++;
                        previouslyReviewedFileList.add(fileName);
                    }

               }

               double newFileRatio = 100 * (newFileForReview/javaChangedFilesReview.size());

               if(newFileRatio >= 50){
                prContainingMajorityNewFiles.add(prTraining);
               }
            }

            double newFilePRRatio = 100 * prContainingMajorityNewFiles.size()/(double)trainingDataset.size();

            writer.write(projectName);
            writer.write(Integer.toString(trainingDataset.size()));
            writer.write(Integer.toString(prContainingMajorityNewFiles.size()));
            writer.write(String.format("%.3f",newFilePRRatio));
            writer.endRecord();

            System.out.println(String.format("Project: %s TrainingPR: %d  NewFilePR: %d RatioNewFilePR: %.2f", projectName, trainingDataset.size(), prContainingMajorityNewFiles.size(), newFilePRRatio));

        }
        writer.close();
    }

    public void studyNewFilesInPRTesting() throws Exception{

        CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/NewFileRecomAnalysis/newFilePRTesting.csv");
        writer.write("ProjectName");
        writer.write("TotalTrainingPR");
        writer.write("PRWithMajorityNewFiles");
        writer.write("PercentagePRWithNewFiles");
        writer.endRecord();

        //List<String> projectList = Arrays.asList("apache_activemq");
    
        for(String projectName : projectList){
            String prFilePath           =   scratchDataLocBackup    + "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath     =   scratchDataLocBackup    + "/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath    =   scratchDataLocBackup    + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath       =   scratchDataLocBackup    + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);

            List<String> trainingDataset = dataModel.getTrainTestSplits().getTrainintPullRequestList();
            List<String> testingDataset = dataModel.getTrainTestSplits().getTestingPullRequestList();


            Set<String> previouslyReviewedFileList = new HashSet<String>();

            Set<String> prContainingMajorityNewFiles = new HashSet<String>();

            for(String prTraining : trainingDataset){
               List<String> javaChangedFilesReview = dataModel.getPrChangedFileList().get(prTraining);

                for(String fileName : javaChangedFilesReview){
                    if(!previouslyReviewedFileList.contains(fileName)){
                        previouslyReviewedFileList.add(fileName);
                    }
               }
            }

            for(String prTraining : testingDataset){
               List<String> javaChangedFilesReview = dataModel.getPrChangedFileList().get(prTraining);

               double newFileForReview = 0.0;

               for(String fileName : javaChangedFilesReview){
                    if(!previouslyReviewedFileList.contains(fileName)){
                        newFileForReview++;
                        previouslyReviewedFileList.add(fileName);
                    }

               }

               double newFileRatio = 100 * (newFileForReview/javaChangedFilesReview.size());

               if(newFileRatio >= 50){
                prContainingMajorityNewFiles.add(prTraining);
               }
            }

            double newFilePRRatio = 100 * prContainingMajorityNewFiles.size()/(double)trainingDataset.size();

            writer.write(projectName);
            writer.write(Integer.toString(trainingDataset.size()));
            writer.write(Integer.toString(prContainingMajorityNewFiles.size()));
            writer.write(String.format("%.3f",newFilePRRatio));
            writer.endRecord();

            System.out.println(String.format("Project: %s TrainingPR: %d  NewFilePR: %d RatioNewFilePR: %.2f", projectName, testingDataset.size(), prContainingMajorityNewFiles.size(), newFilePRRatio));

        }
        writer.close();
    }

    public void run(){
        this.studyReviewerRankMissing(1, investModelsWithCombined);
        //this.studyReviewerRankMissing(5, investModelsWithCombined);
    }
    public static void main(String[] args) throws Exception{
        FurtherPRAnalysisForRecommenderModelInvestigation ob = new FurtherPRAnalysisForRecommenderModelInvestigation();
        ob.run();
        System.out.println("Program finishes successfully");
    }
}
