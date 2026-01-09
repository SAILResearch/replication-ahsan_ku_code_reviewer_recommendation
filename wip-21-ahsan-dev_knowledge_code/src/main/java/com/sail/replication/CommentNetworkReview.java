package com.sail.replication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.PullRequestReviewCommentModel;
import com.sail.replication.model.ReviewerRankingModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationEvaluation;
import com.sail.util.RecommendationUtil;

public class CommentNetworkReview {
    
    double EMPIRICAL_FACTOR = 0.80;
    Map<String,ArrayList<ReviewerRankingModel>> perTestDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();

    Map<String,ArrayList<ReviewerRankingModel>> perTrainDevScore = new HashMap<String, ArrayList<ReviewerRankingModel>>();
    List<RecommendationEvaluation> trainingEvaluationList = new ArrayList<RecommendationEvaluation>();
    
    Map<String,List<String>> actualReviewerListPR = new HashMap<String,List<String>>();

    
    public RecommendationEvaluation recommendReviewerWithCommentNetwork(String projectName, String pullRequestPath, 
    String pullReviewPath, String pullCommentPath, String pullFileChangPath){
        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath, pullReviewPath,
                pullCommentPath, pullFileChangPath);

        Map<String, ArrayList<ReviewerRankingModel>> rankingResultList = new HashMap<String, ArrayList<ReviewerRankingModel>>();  
        Map<String, Set<String>> fullDeveloperReviwerListPerPR = RecommendationUtil.getFullDeveloperList(dataModel);
        
        //Map<String, Integer> supportValuePairReviewer = new HashMap<String, Integer>();
        Map<String, Map<String,Double>>supportValuePairReviewer = new HashMap<String, Map<String,Double>>();


        TrainTestDataModel trainTestModel = dataModel.getTrainTestSplits();
        
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> commentNetworkGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        

        String firstPr = trainTestModel.getTrainintPullRequestList().get(0);
        String lastPr =  trainTestModel.getTestingPullRequestList().get(trainTestModel.getTestingPullRequestList().size() - 1);
        
        DateTime startTime = dataModel.getPullRequestList().get(firstPr).getPrCreatedJodaTime();
        DateTime endTime = dataModel.getPullRequestList().get(lastPr).getPrCreatedJodaTime();

        // Training
        for (String prNumber : trainTestModel.getTrainintPullRequestList()) {
            ArrayList<ReviewerRankingModel> rankReviewersTraining = new ArrayList<ReviewerRankingModel>();
            List<String> reviewerList = dataModel.getPullRequestReviewerMap().get(prNumber);
            String trainKey = projectName + "-" + prNumber;

            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            
            String prCreatorName = prModel.getPrCreaterGitLoginName();

            if (!commentNetworkGraph.containsVertex(prCreatorName)){
                commentNetworkGraph.addVertex(prCreatorName);
            }

            if (dataModel.getPrCommentList().containsKey(prNumber)){
                List<PullRequestReviewCommentModel> revComModelList = dataModel.getPrCommentList().get(prNumber);
                
                /*// Added the reviewer as comment
                for (String  r : reviewerList){
                    PullRequestReviewCommentModel pc = new PullRequestReviewCommentModel();
                    pc.setCommenterName(r);
                    pc.setCommentCreatedJodaTime(prModel.getPrCreatedJodaTime());
                    revComModelList.add(pc);
                }*/
                
                Map<String, Integer> commenterCount = new HashMap<String, Integer>();
                Set<String> commenterNameSet = new HashSet<String>();

                for(PullRequestReviewCommentModel cm : revComModelList){
                    String commenterName = cm.getCommenterName();
                    DateTime commentTime = cm.getCommentCreatedJodaTime();

                    if (commenterName.equals(prCreatorName)){
                        continue;
                    }

                    commenterNameSet.add(commenterName);
                    if (!commenterCount.containsKey(commenterName)){
                        commenterCount.put(commenterName, 0);
                    }else{
                        commenterCount.put(commenterName, commenterCount.get(commenterName) + 1);
                    }

                    double nominator = Hours.hoursBetween(startTime, commentTime).getHours();
                    double denominator = Hours.hoursBetween(startTime, endTime).getHours();
                    double timeWeight = nominator/denominator;
                    double edgeWeight = Math.pow(EMPIRICAL_FACTOR, commenterCount.get(commenterName)) * timeWeight;

                    if(!commentNetworkGraph.containsEdge(pullCommentPath, pullFileChangPath)){
                        // Need to add the vertex and edge
                        // check the commenter is already added to the vertex
                        if (!commentNetworkGraph.containsVertex(commenterName)){
                            commentNetworkGraph.addVertex(commenterName);
                        }
                        // set up the edge
                        commentNetworkGraph.addEdge(prCreatorName, commenterName);
                        DefaultWeightedEdge e = commentNetworkGraph.getEdge(prCreatorName, commenterName);
                        commentNetworkGraph.setEdgeWeight(e, edgeWeight);

                    }else{
                        // Need to udpate the weight
                        DefaultWeightedEdge e = commentNetworkGraph.getEdge(prCreatorName, commenterName);
                        double currentEdgeWeight = commentNetworkGraph.getEdgeWeight(e);
                        commentNetworkGraph.setEdgeWeight(e, currentEdgeWeight + edgeWeight);
                    }

                    // Perform pairwaise support
                    List<String> commenterNameList = new ArrayList<String>();
                    for(String name : commenterNameSet){
                        commenterNameList.add(name);
                    }
                    for (int i = 0 ; i < commenterNameList.size() - 1; i ++ ){
                        for (int j = i + 1 ; j < commenterNameList.size() ; j ++){
                            String name1 = commenterNameList.get(i);
                            String name2 = commenterNameList.get(j);

                            if(!supportValuePairReviewer.containsKey(name1)){
                                supportValuePairReviewer.put(name1, new HashMap<String, Double>());
                            }
                            if(!supportValuePairReviewer.get(name1).containsKey(name2)){
                                supportValuePairReviewer.get(name1).put(name2, 0.0);
                            }

                            if(!supportValuePairReviewer.containsKey(name2)){
                                supportValuePairReviewer.put(name2, new HashMap<String, Double>());
                                
                            }
                            if(!supportValuePairReviewer.get(name2).containsKey(name1)){
                                supportValuePairReviewer.get(name2).put(name1, 0.0);
                            }

                            supportValuePairReviewer.get(name1).put(name2, supportValuePairReviewer.get(name1).get(name2) + 1);
                            supportValuePairReviewer.get(name2).put(name1, supportValuePairReviewer.get(name2).get(name1) + 1);

                        }
                    }
                }
            }
        }

        
        for (String prNumber : trainTestModel.getTestingPullRequestList()) {
            ArrayList<ReviewerRankingModel> rankReviewersTesting = new ArrayList<ReviewerRankingModel>();
            List<String> reviewerList = dataModel.getPullRequestReviewerMap().get(prNumber);
            String testKey = projectName + "-" + prNumber;

            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            String prCreatorName = prModel.getPrCreaterGitLoginName();

            Set<String> fullDeveloperReviewerList = fullDeveloperReviwerListPerPR.get(prNumber);
            Set<String> reviewerAlreadyTaken = new HashSet<String>();
            // Check if there is any neighbour of this PR creator

            if (commentNetworkGraph.containsVertex(prCreatorName)){
                List<String> neighbourList = Graphs.neighborListOf(commentNetworkGraph, prCreatorName);
                
                if (neighbourList.size() > 0){
                    Queue<String> queue = new LinkedList<>();
                    Set<String> markedReviewer = new HashSet<String>();
                    queue.add(prCreatorName);

                    while(!queue.isEmpty()){
                        String v = queue.remove();
                        List<String> neighbours = Graphs.neighborListOf(commentNetworkGraph, v);
                        //System.out.println(v + " " + neighbours);
                        for(String n : neighbours){
                            if (!markedReviewer.contains(n)){
                                DefaultWeightedEdge e = commentNetworkGraph.getEdge(v, n);
                                if(commentNetworkGraph.containsEdge(e)){
                                    double edgeWeight = commentNetworkGraph.getEdgeWeight(e);
                                    queue.add(n);
                                    reviewerAlreadyTaken.add(n);
                                    ReviewerRankingModel rm = new ReviewerRankingModel();
                                    rm.setReviewerName(n);
                                    rm.setScore(edgeWeight);
                                    rankReviewersTesting.add(rm);
                                }
                            }
                        }
                        markedReviewer.add(v);
                    }
                }else{
                    // When the vertex has no neighbour
                    if (supportValuePairReviewer.containsKey(prCreatorName)){
                        for(String collaborator: supportValuePairReviewer.get(prCreatorName).keySet()){
                            ReviewerRankingModel rm = new ReviewerRankingModel();
                            rm.setReviewerName(collaborator);
                            rm.setScore(supportValuePairReviewer.get(prCreatorName).get(collaborator));
                            rankReviewersTesting.add(rm);
                        }
                    }
                }
            }else{
                // When the vertex is not in the graph
                ConnectivityInspector ci = new ConnectivityInspector(commentNetworkGraph);
                List<Set<String>> connectedVertex = ci.connectedSets();
                for(Set <String> conVerList : connectedVertex){
                    if(conVerList.size() > 2){
                        for (String collaborator : conVerList){
                            double indegreeValue = commentNetworkGraph.inDegreeOf(collaborator);
                            ReviewerRankingModel rm = new ReviewerRankingModel();
                            rm.setReviewerName(collaborator);
                            rm.setScore(indegreeValue);
                            rankReviewersTesting.add(rm);
                        }
                    }
                }
            }
            for(String devName : fullDeveloperReviewerList){
                if (!reviewerAlreadyTaken.contains(devName)){
                    ReviewerRankingModel rm = new ReviewerRankingModel();
                    rm.setReviewerName(devName);
                    rm.setScore((double)-1.0);
                    rankReviewersTesting.add(rm);
                }
            }
            rankReviewersTesting.sort(new Comparator<ReviewerRankingModel>() {
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

               rankingResultList.put(prNumber, rankReviewersTesting);
               perTestDevScore.put(testKey, rankReviewersTesting);
        }
        
        RecommendationEvaluation evaluationRest = new RecommendationEvaluation(projectName);
        evaluationRest.calculateAccuracyAndMeanAveragePrecisionII(dataModel, 
        dataModel.getTrainTestSplits().getTestingPullRequestList(), 
        null, rankingResultList,
        ConstantUtil.REVIEW_COMMENT_NETWORK);
        
        System.out.println("Project: " + projectName + " Testing");

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

        List<RecommendationEvaluation> recommendationResultList = new ArrayList<RecommendationEvaluation>();

        for(String projectName : projectList){
            System.out.println("Working ["+ projectName +"]");
            String prFilePath = ConstantUtil.scratchDataLocBackup +  "/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = ConstantUtil.scratchDataLocBackup +"/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = ConstantUtil.scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = ConstantUtil.scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            RecommendationEvaluation recRes = recommendReviewerWithCommentNetwork(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
            recommendationResultList.add(recRes);
        }

        String resultPath = RecommendationUtil.RESULT_DIR + "CN.csv";
        RecommendationUtil.writeRecResult(resultPath, recommendationResultList);
        //writePerTestDevScore();

        //RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TRAIN_RESULT_PATH, perTrainDevScore, "CHREV",actualReviewerListPR);
        RecommendationUtil.writePerTestDevScore(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore, "CN",actualReviewerListPR);
        RecommendationUtil.writeAccuracyResulDetailed(ConstantUtil.RECOMMENDATION_PER_TEST_RESULT_PATH, perTestDevScore,"CN", actualReviewerListPR);
        
        //String pathTrainingResult = ConstantUtil.RECOMMENDATION_RESULT_TRAINING_PATH + "CN_Training.csv";
        //RecommendationUtil.writeRecResult(pathTrainingResult,trainingEvaluationList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time ["+(totalTime/1000)/60 + " minutes]");
    }

    public static void main(String[] args) {
        CommentNetworkReview ob = new CommentNetworkReview();
        ob.startRecoommendation();
        System.out.println("Program finishes successfully");
    }
}
