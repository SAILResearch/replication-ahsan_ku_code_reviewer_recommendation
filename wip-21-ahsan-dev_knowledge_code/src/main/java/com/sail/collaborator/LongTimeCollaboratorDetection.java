package com.sail.collaborator;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Years;

public class LongTimeCollaboratorDetection {

    Map<String, String> firstCommitDevelopers = new HashMap<String, String>();

    public String labelledLTCOutputLocation = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/ku_feature_dev_profile/";

    int DAYS_WINDOW = 30;

    public String LTC_YEAR_ONE = "Year_1";
    public String LTC_YEAR_TWO = "Year_2";
    public String LTC_YEAR_THREE = "Year_3";

    List<String> LTC_YEARS = Arrays.asList(LTC_YEAR_THREE, LTC_YEAR_TWO, LTC_YEAR_ONE);

    public ReviwerRecommendationDataLoader getDataModel(String projectName,
            String pullRequestPath, String pullReviewPath, String pullCommentPath, String pullFileChangPath) {
        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, pullRequestPath,
                pullReviewPath, pullCommentPath, pullFileChangPath);
        return dataModel;
    }

    public ArrayList<String> getTimeWindowCommitListForDeveloper(ArrayList<String> javaFileChangeCommitList, int days,
    LoadGitCommitData dataModel) {
        ArrayList<String> commitList = new ArrayList<String>();
        GitCommitModel firstJavaCommit = dataModel.getGitCommitListMap().get(javaFileChangeCommitList.get(0));
        commitList.add(javaFileChangeCommitList.get(0));
        for (int i = 1; i < javaFileChangeCommitList.size(); i++) {
            GitCommitModel gitCommit = dataModel.getGitCommitListMap().get(javaFileChangeCommitList.get(i));
            int daysBetween = Days.daysBetween(firstJavaCommit.getCommitJodaDate(), gitCommit.getCommitJodaDate())
                    .getDays();
            if (daysBetween <= days) {
                commitList.add(javaFileChangeCommitList.get(i));
            }
        }
        return commitList;
    }

    public ArrayList<String> getTimeWindowCommitListFromAllCommits(ArrayList<GitCommitModel> javaFileChangeCommitList,
            String firstCommitId, String lastCommitId,
            LoadGitCommitData dataModel) {
        ArrayList<String> commitList = new ArrayList<String>();
        DateTime startDate = dataModel.getGitCommitListMap().get(firstCommitId).getCommitJodaDate();
        DateTime endDate = dataModel.getGitCommitListMap().get(lastCommitId).getCommitJodaDate();
        for (int i = 0; i < javaFileChangeCommitList.size(); i++) {
            GitCommitModel gitCommit = javaFileChangeCommitList.get(i);
            DateTime commitCreationDate = gitCommit.getCommitJodaDate();
            if (commitCreationDate.isAfter(startDate.minusDays(1)) & commitCreationDate.isBefore(endDate.plusDays(1))) {
                commitList.add(javaFileChangeCommitList.get(i).getCommitId());
            }
        }
        return commitList;
    }

    public ArrayList<String> getAllCommitUpToTheLastCommitOfDeveloper(ArrayList<GitCommitModel> javaFileChangeCommitList,
            String firstCommitId, String lastCommitId,
            LoadGitCommitData dataModel) {
        ArrayList<String> resultCommitIdList = new ArrayList<String>();
        ArrayList<GitCommitModel> commitList = dataModel.getGitCommitList();
        DateTime endDate = dataModel.getGitCommitListMap().get(lastCommitId).getCommitAuthorJodaDate();

        for(int i = 0 ; i < commitList.size() ; i ++){
            GitCommitModel commit = commitList.get(i);
            String commitId = commit.getCommitId();
            DateTime commitAuthorDate = commit.getCommitAuthorJodaDate();
            if(commitAuthorDate.isAfter(endDate)){
                break;
            }
            resultCommitIdList.add(commit.getCommitId());
        }
        return resultCommitIdList;
    }

    // public void getTotalKnowledgeUnit

    public Map<String, Double> getDevKnowledgeUnitAccessExpertise(ArrayList<String> commitList,
    LoadGitCommitData dataModel,
            Map<String, Map<String, KUFileModel>> gitCommitExtractedKU) {
        Map<String, Double> devKUContributionWithinTimeWindow = new HashMap<String, Double>();
        for (int k = 0; k < ConstantUtil.majorTopicList.size(); k++) {
            devKUContributionWithinTimeWindow.put(ConstantUtil.majorTopicList.get(k), 0.0);
        }
        for (int i = 0; i < commitList.size(); i++) {
            String commitId = commitList.get(i);
            GitCommitModel commitModel = dataModel.getGitCommitListMap().get(commitId);
            List<String> changedJavaFiles = commitModel.getChangedJavaFileList();
            if (gitCommitExtractedKU.containsKey(commitId)) {
                Map<String, KUFileModel> knowledgeUnitPerFile = gitCommitExtractedKU.get(commitId);
                for (String fileName : knowledgeUnitPerFile.keySet()) {
                    // System.out.println(fileName);
                    for (int k = 0; k < ConstantUtil.majorTopicList.size(); k++) {
                        String topicName = ConstantUtil.majorTopicList.get(k);
                        Double kuValue = 0.0;
                        if (knowledgeUnitPerFile.get(fileName).getKnowledgeUnitPerFile().containsKey(topicName)) {
                            kuValue = knowledgeUnitPerFile.get(fileName).getKnowledgeUnitPerFile().get(topicName);
                        }
                        devKUContributionWithinTimeWindow.put(topicName,
                                devKUContributionWithinTimeWindow.get(topicName) + kuValue);
                        // System.out.println(topicName + " " +
                        // knowledgeUnitPerFile.get(fileName).getKnowledgeUnitPerFile().get(topicName));
                    }
                }
            }
        }
        return devKUContributionWithinTimeWindow;
    }

    public Map<String, Map<String, Double>> getNormalizedKUVectors(
            Map<String, Map<String, Double>> kuContributionWithinTimeWindowPerDevList,
            Map<String, Map<String, Double>> kuContributionWithinTimeWindowListPerDevWithAllDevList) {
        Map<String, Map<String, Double>> normalizedKUVectorPerDevList = new HashMap<String, Map<String, Double>>();

        for (String devName : kuContributionWithinTimeWindowPerDevList.keySet()) {
            Map<String, Double> devKUVector = new HashMap<String, Double>();
            for (String topicName : ConstantUtil.majorTopicList) {
                double devKUValue = kuContributionWithinTimeWindowPerDevList.get(devName).get(topicName);
                double normalizerValue = kuContributionWithinTimeWindowListPerDevWithAllDevList.get(devName)
                        .get(topicName);
                double normalizedKUValue = 0.0;
                if (normalizerValue > 0) {
                    normalizedKUValue = devKUValue / normalizerValue;
                }
                devKUVector.put(topicName, normalizedKUValue);
            }
            normalizedKUVectorPerDevList.put(devName, devKUVector);
        }
        return normalizedKUVectorPerDevList;
    }

    /**
     * 
     * @param projectName
     * @param devloperList
     * @param dataModel
     * @param gitCommitExtractedKU
     *                             Missing topic value is assigned null.
     */
    public Map<String, Map<String, Double>> createDevKUContribution(String projectName,
            List<String> devloperList,
            LoadGitCommitData dataModel,
            Map<String, Map<String, KUFileModel>> gitCommitExtractedKU, boolean isNormalized) {
        Map<String, ArrayList<String>> devJavaFileChangedCommitList = dataModel.getDeveloperChangedJavaFilesCommits();
        Map<String, Map<String, Double>> kuContributionWithinTimeWindowPerDevList = new HashMap<String, Map<String, Double>>();
        Map<String, Map<String, Double>> kuContributionWithinTimeWindowListPerDevWithAllDevList = new HashMap<String, Map<String, Double>>();
        int complete_dev = 0;
        for (String devName : devloperList) {
            System.out.println("Working " + (++complete_dev) + "/" + devloperList.size());
            ArrayList<String> javaFileChangeCommitList = devJavaFileChangedCommitList.get(devName);
            ArrayList<String> devCommitListWithinTimeWindow = getTimeWindowCommitListForDeveloper(
                    javaFileChangeCommitList, DAYS_WINDOW, dataModel);

            String devFirstCommitKey = projectName + "-" + devName;
            firstCommitDevelopers.put(devFirstCommitKey, dataModel.getGitCommitListMap().get(devCommitListWithinTimeWindow.get(0)).getCommitAuthorDate());

            /*ArrayList<String> allCommitListWithinTimeWindow = getTimeWindowCommitListFromAllCommits(
                    dataModel.getJavaFileChangeGitCommitList(), devCommitListWithinTimeWindow.get(0),
                    devCommitListWithinTimeWindow.get(devCommitListWithinTimeWindow.size() - 1), dataModel);*/
                    
            ArrayList<String> allCommitListWithinTimeWindow = getAllCommitUpToTheLastCommitOfDeveloper(
                    dataModel.getJavaFileChangeGitCommitList(), devCommitListWithinTimeWindow.get(0),
                    devCommitListWithinTimeWindow.get(devCommitListWithinTimeWindow.size() - 1), dataModel);

            Map<String, Double> devKUContributionWithinTimeWindow = getDevKnowledgeUnitAccessExpertise(
                    devCommitListWithinTimeWindow, dataModel, gitCommitExtractedKU);
            Map<String, Double> allKUContributionWithinTimeWindow = getDevKnowledgeUnitAccessExpertise(
                    allCommitListWithinTimeWindow, dataModel, gitCommitExtractedKU);

            /*for (String topicName : ConstantUtil.majorTopicList) {
                System.out.println(topicName + " Dev Value: " + devKUContributionWithinTimeWindow.get(topicName)
                        + " All Value: " + allKUContributionWithinTimeWindow.get(topicName));
            }*/

            kuContributionWithinTimeWindowPerDevList.put(devName, devKUContributionWithinTimeWindow);
            kuContributionWithinTimeWindowListPerDevWithAllDevList.put(devName, allKUContributionWithinTimeWindow);
        }

        if (isNormalized) {
            Map<String, Map<String, Double>> normalizedKUVectorPerDevList = getNormalizedKUVectors(
                    kuContributionWithinTimeWindowPerDevList,
                    kuContributionWithinTimeWindowListPerDevWithAllDevList);
            return normalizedKUVectorPerDevList;
        }

        return kuContributionWithinTimeWindowPerDevList;

    }

    public boolean isSelectedCommit(ArrayList<GitCommitModel> selectedGitCommits, GitCommitModel commit) {
        boolean result = false;
        for (GitCommitModel selectCommit : selectedGitCommits) {
            if (selectCommit.getCommitId().equals(commit.getCommitId())) {
                return true;
            }
        }
        return result;
    }

    public Map<String, Map<String, KUFileModel>> getExtractedKnowledgeUnitPerCommit(String projectFullName,
        LoadGitCommitData dataModel) {
        ArrayList<GitCommitModel> gitCommitList = dataModel.getGitCommitList();
        ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);

        KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
        Map<String, KUFileModel> knowledgeUnitData = null;
        // Get the first element
        String fileTagFirst = projectFullName + "-" + selectedGitCommits.get(0).getCommitId() + ".csv";
        String fileLocationFirst = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTagFirst;
        knowledgeUnitData = ob.extractKnowledgeUnits(fileLocationFirst, projectFullName);

        Map<String, Map<String, KUFileModel>> gitCommitExtractedKU = new HashMap<String, Map<String, KUFileModel>>();

        int totalCommits = gitCommitList.size();

        for (int i = 0; i < gitCommitList.size(); i++) {
            if (i % 500 == 0) {
                System.out.println("Done working with commits : " + (i + 1) + "/" + totalCommits);
            }
            GitCommitModel commit = gitCommitList.get(i);
            String authorName = commit.getAuthorName();

            if (isSelectedCommit(selectedGitCommits, commit)) {
                String fileTag = projectFullName + "-" + commit.getCommitId() + ".csv";
                String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
                knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
            }
            gitCommitExtractedKU.put(commit.getCommitId(), knowledgeUnitData);
        }
        System.out.println("Done working with commits : " + totalCommits + "/" + totalCommits);
        System.out.println("Knowledge Unit size: " + gitCommitExtractedKU.size());

        return gitCommitExtractedKU;
    }


    public Map<String, List<String>> detectLongTimeCollaboratorPerProject(String projectName) {
        Map<String, List<String>> longTimeCollaboratorList = new HashMap<String, List<String>>();
        for (String yearString : LTC_YEARS) {
            longTimeCollaboratorList.put(yearString, new ArrayList<String>());
        }
        String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/"
                + projectName + "_full_commit_data.csv";
        LoadGitCommitData dataModel = new LoadGitCommitData(projectName, commitPath);

        ArrayList<GitCommitModel> gitCommitList = dataModel.getGitCommitList();

        Set<String> developerAllCommits = new HashSet<String>();
        Set<String> developerJavaCommit = new HashSet<String>();

        for (GitCommitModel gitCommit : gitCommitList) {
            developerAllCommits.add(gitCommit.getAuthorName());
            if (gitCommit.getChangedJavaFileList().size() > 0) {
                developerJavaCommit.add(gitCommit.getAuthorName());
            }
        }

        Map<String, ArrayList<String>> devJavaFileChangedCommitList = dataModel
                .getDeveloperChangedJavaFilesCommits();

        for (String devName : devJavaFileChangedCommitList.keySet()) {

            ArrayList<String> javaFileChangeCommitList = devJavaFileChangedCommitList.get(devName);
            GitCommitModel firstJavaCommit = dataModel.getGitCommitListMap().get(javaFileChangeCommitList.get(0));
            GitCommitModel lastJavaCommit = dataModel.getGitCommitListMap()
                    .get(javaFileChangeCommitList.get(javaFileChangeCommitList.size() - 1));
            int yeardiff = Years.yearsBetween(firstJavaCommit.getCommitAuthorJodaDate(),
                    lastJavaCommit.getCommitAuthorJodaDate()).getYears();

            if (yeardiff > 3) {
                longTimeCollaboratorList.get(LTC_YEAR_THREE).add(devName);
            }
            if (yeardiff > 2) {
                longTimeCollaboratorList.get(LTC_YEAR_TWO).add(devName);
            }
            if (yeardiff > 1) {
                longTimeCollaboratorList.get(LTC_YEAR_ONE).add(devName);
            }
        }

        return longTimeCollaboratorList;
    }

    public Map<String, Map<String, List<String>>> detectLongTimeCollaborator() {

        long startTime = System.currentTimeMillis();

        /*List<String> projectList = Arrays.asList(
			"apache_lucene","apache_wicket","apache_activemq",
		"jruby_jruby","caskdata_cdap","apache_hbase",
		"apache_hive","apache_storm","elastic_elasticsearch","apache_groovy");*/

        ArrayList<String> projectList = FileUtil.readAnalyzedXinProjectName(ConstantUtil.xinProjectPath);
        //List<String> projectList = Arrays.asList("apache_activemq");
        Map<String, Map<String, List<String>>> LTCDeveloperList = new HashMap<String, Map<String, List<String>>>();
        Map<String, Integer> totalJavaDeveloper = new HashMap<String, Integer>();

        int total = 0;
        for (String projectName : projectList) {
            ++total;
            System.out.println("LTC Detection [" + projectName +"] Total [" + total + "]");
            LTCDeveloperList.put(projectName, new HashMap<String, List<String>>());
            for (String yearString : LTC_YEARS) {
                LTCDeveloperList.get(projectName).put(yearString, new ArrayList<String>());
            }

            String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/" + projectName + "_full_commit_data.csv";
        
            LoadGitCommitData dataModel = new LoadGitCommitData(projectName,commitPath);

            ArrayList<GitCommitModel> gitCommitList = dataModel.getGitCommitList();
            System.out.println("Total Commits [" + projectName + "] [" + gitCommitList.size() + "]");
            Set<String> developerAllCommits = new HashSet<String>();
            Set<String> developerJavaCommit = new HashSet<String>();

            for (GitCommitModel gitCommit : gitCommitList) {
                developerAllCommits.add(gitCommit.getAuthorName());
                if (gitCommit.getChangedJavaFileList().size() > 0) {
                    developerJavaCommit.add(gitCommit.getAuthorName());
                }
            }

            System.out.println("All developers [" + developerAllCommits.size() + "]");
            System.out.println("New Developers commit java files [" + developerJavaCommit.size() + "]");
            System.out.println("OLD Developers commit java files ["
                    + dataModel.getDeveloperChangedJavaFilesCommits().size() + "]");

            Map<String, ArrayList<String>> devJavaFileChangedCommitList = dataModel
                    .getDeveloperChangedJavaFilesCommits();
            totalJavaDeveloper.put(projectName, devJavaFileChangedCommitList.size());

            for (String devName : devJavaFileChangedCommitList.keySet()) {
        

                ArrayList<String> javaFileChangeCommitList = devJavaFileChangedCommitList.get(devName);
                GitCommitModel firstJavaCommit = dataModel.getGitCommitListMap().get(javaFileChangeCommitList.get(0));
                GitCommitModel lastJavaCommit = dataModel.getGitCommitListMap()
                        .get(javaFileChangeCommitList.get(javaFileChangeCommitList.size() - 1));
                int yeardiff = Years.yearsBetween(firstJavaCommit.getCommitAuthorJodaDate(),
                        lastJavaCommit.getCommitAuthorJodaDate()).getYears();
                //System.out.println("Year Diff: " + yeardiff + " " + firstJavaCommit.getCommitJodaDate() + " "
                //        + lastJavaCommit.getCommitJodaDate());

                if (yeardiff > 3) {
                    LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).add(devName);
                }
                if (yeardiff > 2) {
                    LTCDeveloperList.get(projectName).get(LTC_YEAR_TWO).add(devName);
                }
                if (yeardiff > 1) {
                    LTCDeveloperList.get(projectName).get(LTC_YEAR_ONE).add(devName);
                }

                if(devName.equals("JakeWharton")){
                    System.out.println("DevName: " + devName);
                    System.out.println("Year Diff: " + yeardiff);
                    System.out.println("Java changed commits; " + devJavaFileChangedCommitList.get(devName).size());
                    System.out.println(javaFileChangeCommitList.get(javaFileChangeCommitList.size() - 1));
                }
            }

            /*
             * System.out.println("LTC YEAR 3 [" +
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).size()+"] Per: ["
             * +String.format("%.4f", (100.0 *
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).size()/
             * devJavaFileChangedCommitList.size()))+"] Non LTC Ratio: [" +
             * String.format("%.4f", ((devJavaFileChangedCommitList.size() -
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).size())/(double)
             * devJavaFileChangedCommitList.size())) +"]");
             * System.out.println("LTC YEAR 2 [" +
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_TWO).size()+"] Per: ["+String.
             * format("%.4f", (100.0 *
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_TWO).size()/
             * devJavaFileChangedCommitList.size()))+"] Non LTC Ratio: [" +
             * String.format("%.4f", ((devJavaFileChangedCommitList.size() -
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_TWO).size())/(double)
             * devJavaFileChangedCommitList.size())) +"]");
             * System.out.println("LTC YEAR 1 [" +
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_ONE).size()+"] Per: ["+String.
             * format("%.4f", (100.0 *
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_ONE).size()/
             * devJavaFileChangedCommitList.size()))+"] Non LTC Ratio: [" +
             * String.format("%.4f", ((devJavaFileChangedCommitList.size() -
             * LTCDeveloperList.get(projectName).get(LTC_YEAR_ONE).size())/(double)
             * devJavaFileChangedCommitList.size())) +"]");
             */

        }
        String statFilePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/stat_ltc.csv";
        // writeStatsLTCDeveloperList(LTCDeveloperList,totalJavaDeveloper,projectList,statFilePath);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time [" + (totalTime / 1000) + "]");

        return LTCDeveloperList;
    }

    public void writeStatsLTCDeveloperList(Map<String, Map<String, List<String>>> LTCDeveloperList,
            Map<String, Integer> totalJavaDeveloper,
            List<String> projectList, String filePath) {
        try {
            CsvWriter writer = new CsvWriter(filePath);
            writer.write("Project_Name");
            writer.write("LTC_Year");
            writer.write("LTC_Developer");
            writer.write("Total_Developer");
            writer.endRecord();
            for (String projectName : projectList) {
                for (String yearString : LTC_YEARS) {
                    writer.write(projectName);
                    writer.write(yearString);
                    writer.write(String.format("%d", LTCDeveloperList.get(projectName).get(yearString).size()));
                    writer.write(String.format("%d", totalJavaDeveloper.get(projectName)));
                    writer.endRecord();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testing() {
        String projectName = "apache_activemq";
        System.out.println("Working [" + projectName + "]");
        String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/" + projectName + "_full_commit_data.csv";
        LoadGitCommitData dataModel = new LoadGitCommitData(projectName,commitPath);

        Map<String, Map<String, KUFileModel>> gitCommitExtractedKU = getExtractedKnowledgeUnitPerCommit(projectName,
                dataModel);

        Set<String> developersChangedJavaFiles = dataModel.getDeveloperChangedJavaFilesCommits().keySet();
        List<String> developerChangedJavaFileList = new ArrayList<String>();
        for (String devName : developersChangedJavaFiles) {
            developerChangedJavaFileList.add(devName);
        }
        Map<String, Map<String, List<String>>> LTCDeveloperList = detectLongTimeCollaborator();
        List<String> devList = LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).subList(0, 1);
        createDevKUContribution(projectName, devList, dataModel, gitCommitExtractedKU, false);

        /*
         * for(String commitId : gitCommitExtractedKU.keySet()){
         * for(String fileName : gitCommitExtractedKU.get(commitId).keySet()){
         * System.out.println("commit: " + commitId + " FileName: " + fileName);
         * }
         * }
         */
    }

    public void createDeveloperKUFeatures() {

        long startTime = System.currentTimeMillis();

         /*List<String> projectList = Arrays.asList(
			"apache_lucene","apache_wicket","apache_activemq",
		"jruby_jruby","caskdata_cdap","apache_hbase",
		"apache_hive","apache_storm","elastic_elasticsearch","apache_groovy");*/

        ArrayList<String> projectList = FileUtil.readAnalyzedXinProjectName(ConstantUtil.xinProjectPath);
        Map<String, Map<String, List<String>>> LTCDeveloperList = detectLongTimeCollaborator();

        int total = 0;
        for (String projectName : projectList) {
            ++total;

            //if(total > 10) break;

            System.out.println("Working [" + projectName + "] Total " + total);
          
            
            String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/" + projectName + "_full_commit_data.csv";
            LoadGitCommitData dataModel = new LoadGitCommitData(projectName,commitPath);

            Map<String, Map<String, KUFileModel>> gitCommitExtractedKU = getExtractedKnowledgeUnitPerCommit(projectName,
                    dataModel);
    
            Set<String> developersChangedJavaFiles = dataModel.getDeveloperChangedJavaFilesCommits().keySet();
            
            List<String> developerChangedJavaFileList = new ArrayList<String>();
            for (String devName : developersChangedJavaFiles) {
                developerChangedJavaFileList.add(devName);
            }
            
            //List<String> devList = LTCDeveloperList.get(projectName).get(LTC_YEAR_THREE).subList(0, 1);
            Map<String, Map<String, Double>> devKUProfile = createDevKUContribution(projectName, developerChangedJavaFileList, dataModel,gitCommitExtractedKU, false);
            
            String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/ku_feature_dev_profile_June_21_2022/withoutNormalized/" + projectName + "_dev_ku_LTC.csv";
            writeLTCLabelledDevProfile (projectName, devKUProfile, LTCDeveloperList.get(projectName), path);

            Map<String, Map<String, Double>> devNormalizedKUProfile = createDevKUContribution(projectName, developerChangedJavaFileList,
                   dataModel, gitCommitExtractedKU, true);
    
            path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/ku_feature_dev_profile_June_21_2022/normalized/" + projectName + "_dev_normalized_ku_LTC.csv";
            writeLTCLabelledDevProfile (projectName, devNormalizedKUProfile, LTCDeveloperList.get(projectName), path);
    
            System.out.println("[Done] " + projectName);
        }
       
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Executation Time [" + (totalTime / 1000) + "]");

    }

    public void writeLTCLabelledDevProfile(String projectName, Map<String, Map<String, Double>> devKUProfile,
            Map<String, List<String>> LTCDeveloperList, String path) {
        try {
            String filePath = path;
            CsvWriter writer = new CsvWriter(filePath);
            writer.write("Project_Name");
            writer.write("Developer_Name");
            writer.write("FirstJavaCommit");
            for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                writer.write(ConstantUtil.majorTopicList.get(i));
            }
            writer.write("LTC_Developer_Cat_Year_One");
            writer.write("LTC_Developer_Cat_Year_Two");
            writer.write("LTC_Developer_Cat_Year_Three");
            writer.endRecord();

            
            for (String devName : devKUProfile.keySet()) {
                writer.write(projectName);
                writer.write(devName);
                String firstCommitKey = projectName + "-" + devName;
                writer.write(firstCommitDevelopers.get(firstCommitKey));

                Map<String, Double> kuProfileVector = devKUProfile.get(devName);
                for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
                    String topicName = ConstantUtil.majorTopicList.get(i);
                    writer.write(String.format("%.4f", kuProfileVector.get(topicName)));
                }
                if (LTCDeveloperList.get(LTC_YEAR_ONE).contains(devName)) {
                    writer.write("1");
                } else {
                    writer.write("0");
                }

                if (LTCDeveloperList.get(LTC_YEAR_TWO).contains(devName)) {
                    writer.write("1");
                } else {
                    writer.write("0");
                }

                if (LTCDeveloperList.get(LTC_YEAR_THREE).contains(devName)) {
                    writer.write("1");
                } else {
                    writer.write("0");
                }
                writer.endRecord();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LongTimeCollaboratorDetection ob = new LongTimeCollaboratorDetection();
        //ob.detectLongTimeCollaborator();
        //ob.testing();
        ob.createDeveloperKUFeatures();
        System.out.println("Program finishes successfully");
    }

}
