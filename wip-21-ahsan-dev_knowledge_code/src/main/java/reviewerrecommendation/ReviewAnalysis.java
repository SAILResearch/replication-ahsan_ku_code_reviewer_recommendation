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
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.ReviewAnalysisModel;
import com.sail.replication.model.ReviewerInfo;
import com.sail.util.ConstantUtil;
import com.sail.util.RecommendationUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

public class ReviewAnalysis {

    public double calculateReviewerChangeFileAuthorRatio(ReviwerRecommendationDataLoader dataModel,
            Set<String> reviewerPRs, ArrayList<String> reviewerCommitList) {

        ArrayList<String> prList = new ArrayList<String>();
        for (String pr : reviewerPRs) {
            prList.add(pr);
        }
        // Ascending date wise sorting
        reviewerCommitList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (dataModel.getGitCommitListMap().get(o1).getCommitJodaDate()
                        .compareTo(dataModel.getGitCommitListMap().get(o2).getCommitJodaDate()));
            }
        });
        prList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (dataModel.getPullRequestList().get(o1).getPrCreatedJodaTime()
                        .compareTo(dataModel.getPullRequestList().get(o2).getPrCreatedJodaTime()));
            }
        });

        Set<String> fileChangedPRsByReviewer = new HashSet<String>();
        for (String prNumber : prList) {
            List<String> prFileChange = dataModel.getPrChangedFileList().get(prNumber);
            PullRequestModel prModel = dataModel.getPullRequestList().get(prNumber);
            DateTime prCreatedTime = prModel.getPrCreatedJodaTime();
            for (String commitId : reviewerCommitList) {
                GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
                DateTime commitJodaTime = commit.getCommitJodaDate();
                if (commitJodaTime.isBefore(prCreatedTime)) {
                    // commit.getChangedJavaFileList()
                    for (String fileName : prFileChange) {
                        fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                        fileName = fileName.replace("/", ".").trim();
                        if (commit.getChangedJavaFileList().contains(fileName)) {
                            fileChangedPRsByReviewer.add(prNumber);
                        }
                    }
                }
            }
        }
        double ratio = 100.0 * (double) (fileChangedPRsByReviewer.size()) / prList.size();
        return ratio;
    }

    public void analysisPRDevelopementCharacteristics(String projectName, String pullRequestPath, String pullReviewPath,
            String pullCommentPath, String pullFileChangPath) {

        ReviewerRecommendationKnowledgeUnit recoModel = new ReviewerRecommendationKnowledgeUnit();

        Map<String, Map<String, KUFileModel>> savedKnowledgeUnits = new HashMap<String, Map<String, KUFileModel>>();

        KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor = new KnowledgeUnitExtractorDevStudy();
        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath,
                pullReviewPath, pullCommentPath, pullFileChangPath);

        ArrayList<String> studiedPrList = dataModel.getPullRequestForReviewRecommendation();
        // Full PR List
        if (studiedPrList.size() == 0) {
            System.err.println("NO PR INFORMATION : " + projectName);
            return;
        }

        String lastStudiedPRNumber = studiedPrList.get(studiedPrList.size() - 1);
        PullRequestModel lastStudiedPRModel = dataModel.getPullRequestList().get(lastStudiedPRNumber);
        DateTime lastStudiedPRDate = lastStudiedPRModel.getPrCreatedJodaTime();

        KnonwledgeUnitsExpertiseFromDevelopment developmentKnowledgeUnits = new KnonwledgeUnitsExpertiseFromDevelopment(
                projectName, false, dataModel);
        developmentKnowledgeUnits.extractKnowledgeUnits();

        System.out.println("[Finish] Development history extraction [" + projectName + "]");

        Map<String, ArrayList<Map<String, Double>>> reviwerPreviousExpertise = recoModel
                .trainingWithDevelopmentHistory(projectName, lastStudiedPRDate, developmentKnowledgeUnits);

        System.out.println("[Finish] Training development history [" + projectName + "]");

        Map<String, Double> normalizedTrainKUFactor = RecommendationUtil
                .getNormalizedKUFactorFromDevelopementHistory(developmentKnowledgeUnits, lastStudiedPRDate);

        System.out.println("[Finish] Normalized calculation [" + projectName + "]");

        Map<String, Map<String, Double>> reviewerPreviousNormalizedExpertise = RecommendationUtil
                .calculateReviewExpertise(reviwerPreviousExpertise, normalizedTrainKUFactor);

        System.out.println("[Finish] calculate normalized developement expertise [" + projectName + "]");

        writePRDevelopmentHistoryProfile(projectName, dataModel, reviewerPreviousNormalizedExpertise);

        System.out.println("[Finish] Writing Result [" + projectName + "]");

        System.out.println("Total Reviewer: [" + dataModel.getReviewerToPullRequestMap().size() + "]");
    }

    public void writePRDevelopmentHistoryProfile(String projectName, ReviwerRecommendationDataLoader dataModel,
            Map<String, Map<String, Double>> reviewerPreviousNormalizedExpertise) {
        try {
            String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/reviewer_developement_history/";
            CsvWriter writer = new CsvWriter(path + projectName + "_reviewer_dev_expertise.csv");
            CsvWriter writerReviewerDev = new CsvWriter(path + projectName + "_reviewer_developer.csv");

            writer.write("Proj_Name");
            writer.write("Name");
            for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                String kuName = ConstantUtil.majorTopicList.get(i);
                writer.write(kuName);
            }
            writer.write("is_reviewer");
            writer.write("no_reviews");
            writer.endRecord();

            for (String name : reviewerPreviousNormalizedExpertise.keySet()) {
                writer.write(projectName);
                writer.write(name);
                for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                    String kuName = ConstantUtil.majorTopicList.get(i);
                    if (reviewerPreviousNormalizedExpertise.get(name).containsKey(kuName)) {
                        writer.write(Double.toString(reviewerPreviousNormalizedExpertise.get(name).get(kuName)));
                    } else {
                        writer.write("0");
                    }
                }
                if (dataModel.getReviewerToPullRequestMap().containsKey(name)) {
                    writer.write("1");
                    writer.write(Integer.toString(dataModel.getReviewerToPullRequestMap().get(name).size()));
                } else {
                    writer.write("0");
                    writer.write("0");
                }
                writer.endRecord();
            }
            writer.close();

            writerReviewerDev.write("Name");
            writerReviewerDev.write("Developer");
            writerReviewerDev.write("Reviewer");

            Set<String> flag = new HashSet<String>();
            for (String name : reviewerPreviousNormalizedExpertise.keySet()) {
                writerReviewerDev.write(name);
                writerReviewerDev.write("Y");
                if (dataModel.getReviewerToPullRequestMap().containsKey(name)) {
                    writerReviewerDev.write("Y");
                } else {
                    writerReviewerDev.write("N");
                }
                writerReviewerDev.endRecord();
                flag.add(name);
            }

            for (String name : dataModel.getReviewerToPullRequestMap().keySet()) {
                if (flag.contains(name)) {
                    continue;
                }
                writerReviewerDev.write(name);
                if (reviewerPreviousNormalizedExpertise.containsKey(name)) {
                    writerReviewerDev.write("Y");
                } else {
                    writerReviewerDev.write("N");
                }
                writerReviewerDev.write("Y");
                writerReviewerDev.endRecord();
                flag.add(name);
            }
            writerReviewerDev.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analyzeReviewrDevelopementHistory() {

        for (String project : ConstantUtil.studiedProject) {
            try {
                System.out.println("PROJECT: " + project);
                String projectName = project;
                String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName
                        + ".csv";
                String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/"
                        + projectName + "_files.csv";
                String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/"
                        + projectName + "_comments.csv";
                String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/"
                        + projectName + "_review.csv";

                this.analysisPRDevelopementCharacteristics(projectName, prFilePath, prReviewerPath, prCommentFilePath,
                        prChagneFilePath);
            } catch (Exception e) {
                System.out.println("PROBLEM PROJECT " + project);
                e.printStackTrace();
                return;
            }
        }
    }

    public String scratchDataLocBackup = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/Result";


    public void reviewerReviewStats(){
        List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
        "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_groovy",
        "elastic_elasticsearch");

        Map<String,Map<String,Set<String>>> fullReviewerList = new HashMap<String, Map<String, Set<String>>>();
        Map<String, Double> totalPRs = new HashMap<String,Double>();
        //List<String> projectList = Arrays.asList("apache_hbase", "apache_hive", "apache_storm","elastic_elasticsearch");
        for (String project : projectList) {
            System.out.println("PROJECT: " + project);
            String projectName = project;
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/"
                    + projectName + "_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName
                    + "_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName
                    + "_review.csv";

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                    prReviewerPath, prCommentFilePath, prChagneFilePath);

           Map<String,Set<String>> reviewerReviewList = dataModel.getReviewerToPullRequestMap();
           totalPRs.put(project, (double)dataModel.getPullRequestReviewerMap().size());
           fullReviewerList.put(project, reviewerReviewList);
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Reviewer_Review_Stats/reviewer_review_stats_new_Feb_8_2023.csv";
            
        writeReviewerReviewStats(fullReviewerList, totalPRs, path);
    }


    public void reviewerReviewStatsUniqueReviews(){
        List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
        "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_groovy",
        "elastic_elasticsearch");

        Map<String,Map<String,Set<String>>> fullReviewerList = new HashMap<String, Map<String, Set<String>>>();
        Map<String, Double> totalPRs = new HashMap<String,Double>();
        //List<String> projectList = Arrays.asList("apache_hbase");
        for (String project : projectList) {
            System.out.println("PROJECT: " + project);
            String projectName = project;
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/"
                    + projectName + "_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName
                    + "_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName
                    + "_review.csv";

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                    prReviewerPath, prCommentFilePath, prChagneFilePath);

           Map<String,Set<String>> reviewerReviewList = dataModel.getReviewerToPullRequestMap();
           totalPRs.put(project, (double)dataModel.getPullRequestReviewerMap().size());
           fullReviewerList.put(project, reviewerReviewList);
        }
        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Reviewer_Review_Stats/reviewer_review_stats_unique_reviews_Feb_8_2023.csv";
            
        Map<String,Map<String,Set<String>>> fullReviewerListUnique = new HashMap<String, Map<String, Set<String>>>();
        for (String projectName : projectList){
            Map<String,Set<String>> reviewerMapReviews = fullReviewerList.get(projectName);
            Set<String> reviewsFlag = new HashSet<String>();
            ArrayList<String> reviewerNames = new ArrayList<String>();
            Map<String,Set<String>> reviewerReviewList = new HashMap<String,Set<String>>();
            for(String reName : reviewerMapReviews.keySet()){
                reviewerNames.add(reName);
            }
            reviewerNames.sort(new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    int v1 = reviewerMapReviews.get(o1).size();
                    int v2 = reviewerMapReviews.get(o2).size();

                    if(v1 >= v2) return -1;
                    else if (v1 < v2) return 1;
                    return 0;
                }
                
            });

            for(int i = 0 ; i < reviewerNames.size() ; i ++){
               String revName = reviewerNames.get(i);
               Set<String> reviewList = reviewerMapReviews.get(revName);
               reviewList.removeAll(reviewsFlag);
               reviewsFlag.addAll(reviewerMapReviews.get(revName));

               reviewerReviewList.put(revName, reviewList);
            }

            fullReviewerListUnique.put(projectName, reviewerReviewList);
        }


        writeReviewerReviewStats(fullReviewerListUnique, totalPRs, path);
    }

    public void writeReviewerReviewStats(Map<String,Map<String,Set<String>>> fullReviewerList,
    Map<String, Double> totalPRs, String path){
        try{
            CsvWriter writer = new CsvWriter(path);
            writer.write("Project_Name");
            writer.write("Reviewer_Name");
            writer.write("Total_PRs");
            writer.write("Number_Reviews");
            writer.write("Percentage_Reviews");
            
            writer.endRecord();

            for(String project : fullReviewerList.keySet()){
                for(String revName : fullReviewerList.get(project).keySet()){
                    writer.write(project);
                    writer.write(revName);
                    writer.write(Integer.toString(fullReviewerList.get(project).get(revName).size()));
                    double per = 100.0 * fullReviewerList.get(project).get(revName).size()/totalPRs.get(project);
                    writer.write(String.format("%.0f", totalPRs.get(project)));
                    writer.write(String.format("%.04f", per));
                    writer.endRecord();
                }
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void revieweAnalysis() {
        ArrayList<ReviewerInfo> reviewrInfoList = new ArrayList<ReviewerInfo>();
        ArrayList<ReviewAnalysisModel> reviewAnalysisList = new ArrayList<ReviewAnalysisModel>();

        List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_groovy",
                "elastic_elasticsearch");

        // List<String> projectList = Arrays.asList("apache_hbase");
        for (String project : projectList) {
            /*
             * if(project.compareTo("elastic_elasticsearch") != 0){ continue; }
             */
            try {
                System.out.println("PROJECT: " + project);
                String projectName = project;
                String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName
                        + ".csv";
                String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/"
                        + projectName + "_files.csv";
                String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/"
                        + projectName + "_comments.csv";
                String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/"
                        + projectName + "_review.csv";

                ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                        prReviewerPath, prCommentFilePath, prChagneFilePath);
                DescriptiveStatistics statPR = new DescriptiveStatistics();

                for (String reviewerName : dataModel.getReviewerToPullRequestMap().keySet()) {
                    statPR.addValue(dataModel.getReviewerToPullRequestMap().get(reviewerName).size());
                }

                ArrayList<String> prRecomData = dataModel.getPullRequestForReviewRecommendation();
                ArrayList<String> prReviewerAndDevelopers = new ArrayList<String>();

                DescriptiveStatistics reviewerFileCommitStat = new DescriptiveStatistics();
                for (String reviewerName : dataModel.getReviewerToPullRequestMap().keySet()) {
                    if (dataModel.getDeveloperChangedJavaFilesCommits().containsKey(reviewerName)) {
                        prReviewerAndDevelopers.add(reviewerName);
                    }

                    Set<String> reviewerPRList = dataModel.getReviewerToPullRequestMap().get(reviewerName);
                    if (dataModel.getDeveloperChangedJavaFilesCommits().containsKey(reviewerName)) {
                        ArrayList<String> reviewerCommitList = dataModel.getAuthorCommitList().get(reviewerName);
                        double ratio = calculateReviewerChangeFileAuthorRatio(dataModel, reviewerPRList,
                                reviewerCommitList);
                        reviewerFileCommitStat.addValue(ratio);
                    }

                    ReviewerInfo rv = new ReviewerInfo();
                    rv.setProjectName(projectName);
                    rv.setReviewerName(reviewerName);
                    rv.setPrListss(dataModel.getReviewerToPullRequestMap().get(reviewerName));
                    rv.setAuthorCommitList(dataModel.getDeveloperChangedJavaFilesCommits().get(reviewerName));
                    reviewrInfoList.add(rv);
                }

                ReviewAnalysisModel revAnalysisOb = new ReviewAnalysisModel();

                revAnalysisOb.setProjectName(projectName);
                revAnalysisOb.setTotalReviewer(dataModel.getReviewerToPullRequestMap().size());
                revAnalysisOb.setTotalPR(dataModel.getPullRequestForReviewRecommendation().size());
                revAnalysisOb.setMedianPR(statPR.getPercentile(50));
                revAnalysisOb.setMeanPR(statPR.getMean());
                revAnalysisOb.setMaxPR(statPR.getMax());
                revAnalysisOb.setMinPR(statPR.getMin());
                revAnalysisOb.setQ1(statPR.getPercentile(25));
                revAnalysisOb.setQ3(statPR.getPercentile(75));
                revAnalysisOb.setTotalJavaFileChaangedCommits(dataModel.getJavaFileChangeGitCommitList().size());
                revAnalysisOb.setTotalFullCommits(dataModel.getGitCommitList().size());
                revAnalysisOb.setTotalDevelopers(dataModel.getDeveloperChangedJavaFilesCommits().size());
                revAnalysisOb.setPrReviewerAndDeveloper(prReviewerAndDevelopers.size());

                reviewAnalysisList.add(revAnalysisOb);

            } catch (Exception e) {
                System.err.println("PROBLEM");
                e.printStackTrace();
                return;
            }
        }
        writeProjectReviewAnalysis(reviewAnalysisList);
    }

    public void writeReviewerInfo(ArrayList<ReviewerInfo> reviewrInfoList) {
        try {
            CsvWriter writerReviewerInfo = new CsvWriter(
                    "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/reviewer_developement_history/reviewer_details.csv");
            writerReviewerInfo.write("Project");
            writerReviewerInfo.write("Reviewer_Name");
            writerReviewerInfo.write("PR_Reviews");
            writerReviewerInfo.write("Author_Commits");
            writerReviewerInfo.endRecord();

            for (ReviewerInfo reviewerInfo : reviewrInfoList) {
                writerReviewerInfo.write(reviewerInfo.getProjectName());
                writerReviewerInfo.write(reviewerInfo.getReviewerName());
                writerReviewerInfo.write(Integer.toString(reviewerInfo.getPrListss().size()));
                writerReviewerInfo.write(Integer.toString(reviewerInfo.getAuthorCommitList().size()));
                writerReviewerInfo.endRecord();
            }

            writerReviewerInfo.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeProjectReviewAnalysis(ArrayList<ReviewAnalysisModel> reviewAnalysisList) {
        try {
            CsvWriter writer = new CsvWriter(
                    "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/reviewer_developement_history/project_dev_rev_stat_new.csv");
            writer.write("Project");
            writer.write("Full_Commits");
            writer.write("Java_File_Changed_Commits");
            writer.write("Developers");
            writer.write("PR_Reviewer");
            writer.write("PR_Reviewer_Developer");
            writer.write("Percentage_PR_Reviewer_Developer");
            writer.write("Total_PRs");
            writer.write("Median_PR_Reviews");
            writer.write("Mean_PR_Reviews");
            writer.write("Min_PR_Reviews");
            writer.write("Q1_PR_Reviews");
            writer.write("Q3_PR_Reviews");
            writer.write("Max_PR_Reviews");
            writer.endRecord();

            for (ReviewAnalysisModel ob : reviewAnalysisList) {
                writer.write(ob.getProjectName());
                writer.write(Integer.toString(ob.getTotalFullCommits()));
                writer.write(Integer.toString(ob.getTotalJavaFileChaangedCommits()));
                writer.write(Integer.toString(ob.getTotalDevelopers()));
                writer.write(Integer.toString(ob.getTotalReviewer()));
                writer.write(Integer.toString(ob.getPrReviewerAndDeveloper()));
                writer.write(
                        Double.toString(100.0 * ob.getPrReviewerAndDeveloper() / Math.max(1, ob.getTotalReviewer())));
                writer.write(Integer.toString(ob.getTotalPR()));
                writer.write(Double.toString(ob.getMedianPR()));
                writer.write(Double.toString(ob.getMeanPR()));
                writer.write(Double.toString(ob.getMinPR()));
                writer.write(Double.toString(ob.getQ1()));
                writer.write(Double.toString(ob.getQ3()));
                writer.write(Double.toString(ob.getMaxPR()));
                writer.endRecord();
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAnalyzingPRReviews(){
        List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_stratos", "apache_groovy",
                "elastic_elasticsearch");

        for(String projectName : projectList){
            String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName +".csv";
            String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
            String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
            String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
            analysisPRDevelopementCharacteristics(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
        }
    }

    public static void main(String[] args) {
        ReviewAnalysis ob = new ReviewAnalysis();
        //ob.startAnalyzingPRReviews();
        //ob.revieweAnalysis();
        //ob.reviewerReviewStats();
        ob.reviewerReviewStatsUniqueReviews();
        System.out.println("Program Finishes Successfully");
    }
}
