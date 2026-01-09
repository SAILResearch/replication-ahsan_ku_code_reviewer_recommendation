package reviewerrecommendation.followupstudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Months;
import org.joda.time.Years;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.replication.model.PullRequestModel;
import com.sail.util.TextUtil;

public class RandomSelectorForManualAnalysis {

    public String scratchDataLocBackup = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/Result";
    String dir = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/";

    List<String> recommenderList = Arrays.asList("KUREC","CHREV","ER","RF","CF");
    List<String> investModelsWithCombined = Arrays.asList("KUREC","CHREV","ER","RF","CF","AD_FREQ", "AD_REC", "AD_HYBRID");

    public void selectRandomPRsFromEveryProject() throws Exception {
        List<String> projectList = Arrays.asList("apache_activemq", "apache_groovy", "apache_lucene",
                "apache_hbase", "apache_hive", "apache_storm", "apache_wicket", "elastic_elasticsearch");

        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/manualstudy/project_test_train_stat.csv";
        CsvWriter writer = new CsvWriter(path);
        writer.write("Project_Name");
        writer.write("Total_PR");
        writer.write("Train_PR");
        writer.write("Test_PR");
        writer.endRecord();

        for (String projectName : projectList) {
            System.out.println("Working [" + projectName + "]");
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/"
                    + projectName + "_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName
                    + "_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName
                    + "_review.csv";

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                    prReviewerPath, prCommentFilePath, prChagneFilePath);

            writer.write(projectName);
            writer.write(Integer.toString(dataModel.getTrainTestSplits().getFullPullRequestList().size()));
            writer.write(Integer.toString(dataModel.getTrainTestSplits().getTrainintPullRequestList().size()));
            writer.write(Integer.toString(dataModel.getTrainTestSplits().getTestingPullRequestList().size()));
            writer.endRecord();
        }

        writer.close();
    }

    public Map<String, ArrayList<String>> readManualStudyPRData(String filePath) {
        Map<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
        try {
            CsvReader reader = new CsvReader(filePath);
            reader.readHeaders();
            while (reader.readRecord()) {
                String projectName = reader.get("ProjectName");
                String prNumber = reader.get("PrNumber");
                if (!data.containsKey(projectName)) {
                    data.put(projectName, new ArrayList<String>());
                }
                data.get(projectName).add(prNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (String projectName : data.keySet()) {
            int total = 0;
            for (String prNumber : data.get(projectName)) {
                ++total;
                System.out.println(String.format("%s %s", projectName, prNumber));
                if (total > 2)
                    break;
            }
        }*/
        System.out.println("Reading Complete");
        return data;
    }

    public Map<String, String> readTopReviewer(String modelName, Map<String, ArrayList<String>> manualStudyData) {
        Map<String, String> topRecommendedReviewer = new HashMap<String, String>();
        String location = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022";

        for (String projectName : manualStudyData.keySet()) {
            for (String prNumber : manualStudyData.get(projectName)) {
                String key = projectName + "-" + prNumber;
                String path = String.format("%s/%s/%s/%s.csv", location, modelName, projectName, key);

                try {
                    CsvReader reader = new CsvReader(path);
                    reader.readHeaders();
                    reader.readRecord();
                    String devName = reader.get("dev_name").trim();
                    topRecommendedReviewer.put(key, devName);

                    //System.out.println(String.format("%s %s %s", projectName, prNumber, devName));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return topRecommendedReviewer;

    }

    public void checkPRFilesInDevelopmentHistory(String recomRevName, String prNumber,
            ReviwerRecommendationDataLoader dataModel) {

            
    }

    public Set<String> checkFileInDevelopmentHistory(String recomRevName, String prNumber,
            ReviwerRecommendationDataLoader dataModel, List<String> listOfChangedFiles, boolean flagTimeConstraint) {
        Set<String> matchedFile = new HashSet<String>();
        PullRequestModel investPR = dataModel.getPullRequestList().get(prNumber);
        
        ArrayList<String> changedFileNameList = new ArrayList<String>();
        Set<String> revCommitHisAccessRevFile = dataModel.getReviewerToPullRequestMap().get(recomRevName);

        for (String changeFile : listOfChangedFiles) {
            String f = changeFile;
            f = f.substring(f.lastIndexOf("/") + 1).trim();
            changedFileNameList.add(f);
        }

        ArrayList<String> commitIdList = dataModel.getDeveloperChangedJavaFilesCommits().get(recomRevName);
        for(String commitId : commitIdList){
            GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
            if(commit.getCommitJodaDate().isAfter(investPR.getPrCreatedJodaTime())){
                continue;
            }

            if(flagTimeConstraint){
                int year = Years.yearsBetween(commit.getCommitJodaDate(), investPR.getPrCreatedJodaTime()).getYears();
                int month = Months.monthsBetween(commit.getCommitJodaDate(), investPR.getPrCreatedJodaTime()).getMonths();
            
                //System.out.println("Commit : YEAR" + year);
                //if (year > 1) continue;
                if (month > 6) continue;
            }

            for(String changeFile : commit.getChangedJavaFileList()){
                String f = changeFile;
                f = f.substring(f.lastIndexOf(".") + 1).trim() + ".java";
                if(changedFileNameList.contains(f)){
                    matchedFile.add(f);
                }
                //System.out.println("GOT " + f);
            }
        }
        return matchedFile;
    }

    public Set<String> checkPRFilesInReviewHistory(String recomRevName, String prNumber,
            ReviwerRecommendationDataLoader dataModel, List<String> listOfChangedFiles,  boolean flagTimeConstraint) {

        Set<String> matchedFile = new HashSet<String>();
        PullRequestModel investPR =  dataModel.getPullRequestList().get(prNumber);
        ArrayList<String> changedFileNameList = new ArrayList<String>();
        Set<String> revHistoryPR = dataModel.getReviewerToPullRequestMap().get(recomRevName);

        for(String changeFile : listOfChangedFiles){
            String f = changeFile;
            f = f.substring(f.lastIndexOf("/") + 1).trim();
            changedFileNameList.add(f);
        }


        for(String prTesting : revHistoryPR){
           PullRequestModel checkPR =  dataModel.getPullRequestList().get(prTesting);
           if(checkPR.getPrCreatedJodaTime().isAfter(investPR.getPrCreatedJodaTime())){
            continue;
           }

           if(flagTimeConstraint){
            int year = Years.yearsBetween(checkPR.getPrCreatedJodaTime(), investPR.getPrCreatedJodaTime()).getYears();
            int month = Months.monthsBetween(checkPR.getPrCreatedJodaTime(), investPR.getPrCreatedJodaTime()).getMonths();
            
            //System.out.println("PR : YEAR" + year);
            //if(year > 1) continue;
            if (month > 6) continue;
            }

           List<String> changedFileInThisPR = dataModel.getPrChangedFileList().get(prTesting);

           for(String f : changedFileInThisPR){
                f = f.substring(f.lastIndexOf("/") + 1).trim();
                if(changedFileNameList.contains(f)){
                    matchedFile.add(f);
                }
           }
        }

        return matchedFile;
    }

    public void analysisReviewerHistoricalInfo(String modelName, int topRank) throws Exception {
         List<String> projectList =
         Arrays.asList("apache_activemq","apache_lucene",
         "apache_hbase","apache_hive", "apache_storm",
         "elastic_elasticsearch");

       //String filePathManualStudy = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/" + modelName + "_manual_study_pr_list.csv";
       //String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/results/filesInPreviewsReviews_" + modelName + ".csv";

       String filePathManualStudy = String.format("%s/InCorrectReviewerRankingResult/%s_FULL_top_rank%s.csv",dir, modelName, topRank);  
                
       //String filePathManualStudy = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/" + modelName + "_FULL.csv";
       
       String outputPath = String.format("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/results/filesInPreviewsReviews_%s_FULL_March_02_2023_experience_last_six_month_top_%d_.csv",modelName, topRank);
       
       //String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/results/filesInPreviewsReviews_" + modelName + "_FULL_Feb_14_2023_experience_last_six_month.csv";


        //List<String> projectList = Arrays.asList("apache_activemq");
        Map<String, ArrayList<String>> manualStudyData = readManualStudyPRData(filePathManualStudy);
        Map<String, String> topRecommendedReviewer = readTopReviewer(modelName, manualStudyData);

        CsvWriter writer = new CsvWriter(outputPath);
        writer.write("ProjectName");
        writer.write("PRNumber");
        writer.write("RecomRevName");
        writer.write("TotalChangeFile");
        writer.write("TotalPrevReviewFile");
        writer.write("TotalPrevDevFile");
        writer.write("TotalFileReviewDev");
        writer.write("PercentageFileReivewDev");
        writer.endRecord();
        
        for (String projectName : projectList) {
            System.out.println("Working [" + projectName + "]");
            String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
            String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/"
                    + projectName + "_files.csv";
            String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName
                    + "_comments_with_discussion.csv";
            String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName
                    + "_review.csv";

            ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                    prReviewerPath, prCommentFilePath, prChagneFilePath);

            ArrayList<String> investigatdPRList = manualStudyData.get(projectName);
            List<String> listOfChangedFiles = null;
            String recommendedReviewerName = "";

            Set<String> changedFilesInTrainingHistory = new HashSet<String>();

            for(String prNumber : dataModel.getTrainTestSplits().getTrainintPullRequestList()){
                List<String> changedFileList = dataModel.getPrChangedFileList().get(prNumber);
                for(String fileName : changedFileList){
                    changedFilesInTrainingHistory.add(fileName);
                }
            }

            for (String prNumber : investigatdPRList) {
                try{
                    List<String> listOfChangedWithoutNew = new ArrayList<String>();
                String key = projectName + "-" + prNumber;
                recommendedReviewerName = topRecommendedReviewer.get(key);
                listOfChangedFiles = dataModel.getPrChangedFileList().get(prNumber);

                //System.out.println("Changed Files: " + listOfChangedFiles.size());

                for(String fileName : listOfChangedFiles){
                    if(changedFilesInTrainingHistory.contains(fileName)){
                        listOfChangedWithoutNew.add(fileName);
                    }
                }

                if (listOfChangedWithoutNew.size() == 0){
                    continue;
                }
                System.out.println(prNumber + " LIST OF CHANGED FILE WIHTOUT NEW: " + listOfChangedWithoutNew.size() +  "/" +  listOfChangedFiles.size());

                Set<String> matchedFileReviewList = checkPRFilesInReviewHistory(recommendedReviewerName,prNumber,dataModel, listOfChangedWithoutNew, true);
                Set<String> matchedFileDevList = checkFileInDevelopmentHistory(recommendedReviewerName,prNumber,dataModel, listOfChangedWithoutNew, true );
                
                Set<String> combinedFileList = new HashSet<String>();
                combinedFileList.addAll(matchedFileReviewList);
                combinedFileList.addAll(matchedFileDevList);

                double percentageFileReviewDev = 100.0 * (combinedFileList.size()/(double) listOfChangedWithoutNew.size());

                writer.write(projectName);
                writer.write(prNumber);
                writer.write(recommendedReviewerName);
                writer.write(Integer.toString(listOfChangedWithoutNew.size()));
                writer.write(Integer.toString(matchedFileReviewList.size()));
                writer.write(Integer.toString(matchedFileDevList.size()));
                writer.write(Integer.toString(combinedFileList.size()));
                writer.write(String.format("%.2f",percentageFileReviewDev));
                writer.endRecord();

                }catch(Exception e){
                    //e.printStackTrace();
                    writer.write(projectName);
                    writer.write(prNumber);
                    writer.write(recommendedReviewerName);
                    writer.write(Integer.toString(listOfChangedFiles.size()));
                    writer.write(Integer.toString(0));
                    writer.write(Integer.toString(0));
                    writer.write(Integer.toString(0));
                    writer.write(Integer.toString(0));
                    writer.endRecord();
                }
                
            }

        }
        writer.close();
    }

    public void manualStudyInvest() {
        String projectName = "elastic_elasticsearch";
        String prFilePath = scratchDataLocBackup + "/pull_request/pr_reports_" + projectName + ".csv";
        String prChagneFilePath = scratchDataLocBackup + "/pull_request_changed_files/pull_request_files_csv/"
                + projectName + "_files.csv";
        String prCommentFilePath = scratchDataLocBackup + "/pull_request_comments/comments_csv_files/" + projectName
                + "_comments_with_discussion.csv";
        String prReviewerPath = scratchDataLocBackup + "/pull_request_reviewer/reviewer_csv_files/" + projectName
                + "_review.csv";

        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
                prReviewerPath, prCommentFilePath, prChagneFilePath);

        
        String prNumber = "77464";
        String recomRevName = "Jim Ferenczi";

        Set<String> commitList = new HashSet<String>();
        Set<String> pullRequestList = new HashSet<String>();

        PullRequestModel investPR =  dataModel.getPullRequestList().get(prNumber);
        List<String> listOfChangedFiles = dataModel.getPrChangedFileList().get(prNumber);
        ArrayList<String> changedFileNameList = new ArrayList<String>();
        Set<String> revHistoryPR = dataModel.getReviewerToPullRequestMap().get(recomRevName);

        for (String changeFile : listOfChangedFiles) {
            String f = changeFile;
            f = f.substring(f.lastIndexOf("/") + 1).trim();
            changedFileNameList.add(f);
        }

        // DEVELOPMENT:
        ArrayList<String> commitIdList = dataModel.getDeveloperChangedJavaFilesCommits().get(recomRevName);
        for(String commitId : commitIdList){
            GitCommitModel commit = dataModel.getGitCommitListMap().get(commitId);
            if(commit.getCommitJodaDate().isAfter(investPR.getPrCreatedJodaTime())){
                continue;
            }
            for(String changeFile : commit.getChangedJavaFileList()){
                String f = changeFile;
                f = f.substring(f.lastIndexOf(".") + 1).trim() + ".java";
                if(changedFileNameList.contains(f)){
                    commitList.add(commitId);
                }
                //System.out.println("GOT " + f);
            }
        }


        // REVIEW
        for(String prTesting : revHistoryPR){
            PullRequestModel checkPR =  dataModel.getPullRequestList().get(prTesting);
            if(checkPR.getPrCreatedJodaTime().isAfter(investPR.getPrCreatedJodaTime())){
             continue;
            }
 
            List<String> changedFileInThisPR = dataModel.getPrChangedFileList().get(prTesting);
 
            for(String f : changedFileInThisPR){
                 f = f.substring(f.lastIndexOf("/") + 1).trim();
                 if(changedFileNameList.contains(f)){
                    pullRequestList.add(prTesting);
                 }
            }
         }

        try{
            CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/Review_Recommendation_Restult/per_dev_score_Dec_9_2022/InCorrectReviewerRankingResult/results/manual_study/" + projectName + "-" + prNumber + "-" + recomRevName + ".csv");
            writer.write("ProjectName");
            writer.write("PrNumber");
            writer.write("RecomRev");
            writer.write("ListOfCommits");
            writer.write("ListOfPRs");
            writer.endRecord();

            writer.write(projectName);
            writer.write(prNumber);
            writer.write(recomRevName);
            writer.write(TextUtil.convertSetToString(commitList, "-"));
            writer.write(TextUtil.convertSetToString(pullRequestList, "-"));
            writer.endRecord();

            writer.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public void startAnalysisReviewerHistoryInfo(){
        for(String recommenderName : investModelsWithCombined){
            try{
                analysisReviewerHistoricalInfo(recommenderName, 1);
                analysisReviewerHistoricalInfo(recommenderName, 5);
                //break;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        RandomSelectorForManualAnalysis ob = new RandomSelectorForManualAnalysis();
        // ob.selectRandomPRsFromEveryProject();
        // ob.readManualStudyPRData();
        
        /*ob.analysisReviewerHistoricalInfo("KUREC");
        ob.analysisReviewerHistoricalInfo("CHREV");
        ob.analysisReviewerHistoricalInfo("CF");
        ob.analysisReviewerHistoricalInfo("RF");
        ob.analysisReviewerHistoricalInfo("ER");*/

        ob.startAnalysisReviewerHistoryInfo();
        //ob.manualStudyInvest();
        System.out.println("Program finishes successsfully");
    }

}
