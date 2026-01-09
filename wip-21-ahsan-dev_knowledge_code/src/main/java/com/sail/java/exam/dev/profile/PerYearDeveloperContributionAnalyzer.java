package com.sail.java.exam.dev.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.model.DeveloperKUProfile;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;

public class PerYearDeveloperContributionAnalyzer {

   
    // 2016 (5), 2017(4), 2018(3), 2019 (2), 2020 (1), 2021 (0)

    List<String> analyzedHistoryYearList = Arrays.asList("2021","2020","2019","2018","2017","2016");

    public String OVERTIME_ANALYSIS = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/overtime_ku_profiles/";

    public Map<String, Integer> getDeveloperHistory(String projectName){
        List<String> yearStringList = Arrays.asList("< 2016", "2017", "2018", "2019", "2020", "2021", "2022");
        Map<String, Integer> firstCommitAuthorMap = new HashMap<String, Integer>(); 
        String path = ConstantUtil.COMMIT_HISTORY_DIR + projectName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
        for(int i = 0 ;  i < gitCommitList.size() ; i ++){
            GitCommitModel commit = gitCommitList.get(i);
            String devName = commit.getAuthorName();
            int yearFirstCommitAuthor = commit.getCommitAuthorJodaDate().getYear();
            if(!firstCommitAuthorMap.containsKey(devName)){
                firstCommitAuthorMap.put(devName, yearFirstCommitAuthor);
            }
            //System.out.println( "Commit: " + i + " " + commit.getCommitAuthorJodaDate());
        }
        Map<String, Set<String>> devFreqYear = new HashMap<String, Set<String>>();
        for(String devName : firstCommitAuthorMap.keySet()){
            int yearValue = firstCommitAuthorMap.get(devName);
            if(yearValue < 2016){
                if(!devFreqYear.containsKey("< 2016")){
                    devFreqYear.put("< 2016", new HashSet<String>());
                }
                devFreqYear.get("< 2016").add(devName);
            }else{
                if(!devFreqYear.containsKey(Integer.toString(yearValue))){
                    devFreqYear.put(Integer.toString(yearValue), new HashSet<String>());
                }
                devFreqYear.get(Integer.toString(yearValue)).add(devName);
            }
        }
        for(String yearString  : yearStringList){
            if(devFreqYear.containsKey(yearString)){
                System.out.println(yearString + " " + devFreqYear.get(yearString).size());
            }else{
                System.out.println(yearString + " " + 0);
            }
        }

        for(String devName : firstCommitAuthorMap.keySet()){
            int yearValue = firstCommitAuthorMap.get(devName);
            System.out.println(devName + " " + yearValue);
        }
        return firstCommitAuthorMap;
    }

    public  Map<String, Map<String, DeveloperKUProfile>> readOvertimeYearData(String projectName){
         //  Developer, Year, KU contribution
        Map<String, Map<String, DeveloperKUProfile>> developerHistoryKUProfiles = new HashMap<String, Map<String, DeveloperKUProfile>>();
        Map<String, Integer> firstCommitAuthorMap = getDeveloperHistory(projectName);
        
        ArrayList<DeveloperKUProfile> loadedDeveloperKUProfile = new ArrayList<DeveloperKUProfile>();
        String dir = OVERTIME_ANALYSIS + projectName + "/";
        for(int i = 0 ; i < analyzedHistoryYearList.size() ; i ++){
            String filePath = dir  + projectName + "_up_to_last_" + i + "_years"+ ".csv";
            try{
                CsvReader reader = new CsvReader(filePath);
                reader.readHeaders();
                while(reader.readRecord()){
                    DeveloperKUProfile devProf = new DeveloperKUProfile();
                    String devName = reader.get("Developer_Name");
                    devProf.setDeveloperName(devName);
                    double totalCommits = Double.parseDouble(reader.get("Total_Commits_Java_File"));
                    String year = analyzedHistoryYearList.get(i);
                    for(int k = 0 ; k < ConstantUtil.majorTopicList.size() ; k ++){
                        String topicName = ConstantUtil.majorTopicList.get(k);
                        Double topicValue = Double.parseDouble(reader.get(topicName));
                        devProf.getDevKUprofileMap().put(topicName, topicValue);
                    }
                    devProf.setTotalCommits((int)totalCommits);
                    devProf.setYearOfFirstContrib(firstCommitAuthorMap.get(devName));
                    devProf.setCommitContributionYear(Integer.parseInt(analyzedHistoryYearList.get(i)));
                    loadedDeveloperKUProfile.add(devProf);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        for(DeveloperKUProfile devProfile : loadedDeveloperKUProfile){
            String devName = devProfile.getDeveloperName();
            String yearCommitContributor = Integer.toString(devProfile.getCommitContributionYear());
            if(!developerHistoryKUProfiles.containsKey(devName)){
                developerHistoryKUProfiles.put(devName, new HashMap<String, DeveloperKUProfile>());
            }
            developerHistoryKUProfiles.get(devName).put(yearCommitContributor, devProfile);
        }
        /*for(String devName : developerHistoryKUProfiles.keySet()){
            List<String> yearContributList = new ArrayList<String>();
            Map<String, DeveloperKUProfile> devProfileList = developerHistoryKUProfiles.get(devName);
            for(String year : devProfileList.keySet()){
                yearContributList.add(year);
            }
            System.out.println("Dev Name: " + devName + " " + developerHistoryKUProfiles.get(devName).size() + " " + yearContributList);
        }*/
        return developerHistoryKUProfiles;
    }

    public DeveloperKUProfile profDiffHelper(DeveloperKUProfile prevProfile, DeveloperKUProfile presProfile){

        Map<String, Double> prevKuList = prevProfile.getDevKUprofileMap();
        Map<String, Double> presKuList = presProfile.getDevKUprofileMap();

        Map<String, Double> diffKuList = new HashMap<String, Double>();

        DeveloperKUProfile devDiffProfile = new DeveloperKUProfile();
        String yearDiff = String.format("%d-%d", presProfile.getCommitContributionYear(), prevProfile.getCommitContributionYear());
        
        devDiffProfile.setDiff(true);
        devDiffProfile.setDiffYearString(yearDiff);

        for(int k = 0 ; k < ConstantUtil.majorTopicList.size() ; k ++){
            String topicName = ConstantUtil.majorTopicList.get(k);
            double prevKuValue = 0.0;
            double presKuValue = 0.0;
            if(prevKuList.containsKey(topicName)){
                prevKuValue = prevKuList.get(topicName);
            }
            if(presKuList.containsKey(topicName)){
                presKuValue = presKuList.get(topicName);
            }
            double diffKuValue = presKuValue - prevKuValue;
            diffKuList.put(topicName, diffKuValue);
        }
        int diffCommitValue = presProfile.getTotalCommits() -  prevProfile.getTotalCommits();
        devDiffProfile.setDevKUprofileMap(diffKuList);
        devDiffProfile.setCommitDiffs(diffCommitValue);
        devDiffProfile.setYearExp(presProfile.getCommitContributionYear() - presProfile.getYearOfFirstContrib());
        devDiffProfile.setYearOfFirstContrib(presProfile.getYearOfFirstContrib());

        return devDiffProfile;

    }

    public void calculateDevDiffProfile(String projectName){
        Map<String, Map<String, DeveloperKUProfile>>  devDiffProfileHistory = new HashMap<String, Map<String, DeveloperKUProfile>>();
        Map<String, Map<String, DeveloperKUProfile>> developerHistoryKUProfiles = readOvertimeYearData(projectName);
        for(String devName : developerHistoryKUProfiles.keySet()){
            Map<String, DeveloperKUProfile> devYearHistory = developerHistoryKUProfiles.get(devName);
            if(!devDiffProfileHistory.containsKey(devName)){
                devDiffProfileHistory.put(devName, new HashMap<String, DeveloperKUProfile>());
            }
            for(int i = analyzedHistoryYearList.size() - 1 ; i > 0 ; i --){
                if(!devYearHistory.containsKey(analyzedHistoryYearList.get(i))){
                    continue;
                }
                DeveloperKUProfile prevProfile = devYearHistory.get(analyzedHistoryYearList.get(i));
                DeveloperKUProfile presProfile = devYearHistory.get(analyzedHistoryYearList.get(i - 1));
                String yearDiff = String.format("%d-%d", presProfile.getCommitContributionYear(), prevProfile.getCommitContributionYear());
                DeveloperKUProfile devDiffProfile = profDiffHelper(prevProfile, presProfile);
                devDiffProfileHistory.get(devName).put(analyzedHistoryYearList.get(i - 1), devDiffProfile);
            }
        }
        writeDevDiffHistory(devDiffProfileHistory, projectName);
    }
    public void writeDevDiffHistory(Map<String, Map<String, DeveloperKUProfile>>  devDiffProfileHistory,
                    String projectName){
        String dir = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/overtime_ku_profiles_diff/";
         
        try{
            Map<String, CsvWriter> writerList= new HashMap<String,CsvWriter>();
            for(int i = analyzedHistoryYearList.size() - 1 ; i > 0 ; i--){
                String yearKey = analyzedHistoryYearList.get(i - 1);
                String yearDiff = analyzedHistoryYearList.get(i - 1) + "-" + analyzedHistoryYearList.get(i);
                String outPath = dir + projectName + "/" + projectName + "_dev_profile_diff_" + yearKey + ".csv";
                CsvWriter writer = new CsvWriter(outPath);
                writer.write("Project_Name");
                writer.write("Year_Diff_String");
                writer.write("DevName");
                writer.write("First_Contrib");
                writer.write("Commit_Diff");
                writer.write("Exp_Diff");
                for(int j = 0 ; j < ConstantUtil.majorTopicList.size() ; j ++){
                    writer.write(ConstantUtil.majorTopicList.get(j));
                }
                writer.endRecord();
                writerList.put(analyzedHistoryYearList.get(i-1), writer);
            }
            for(String devName : devDiffProfileHistory.keySet()){
                for(String year : devDiffProfileHistory.get(devName).keySet()){
                    //System.out.println("Year: " + year);
                    DeveloperKUProfile devDiffProfile = devDiffProfileHistory.get(devName).get(year);
                    CsvWriter writer = writerList.get(year);
                    writer.write(projectName);
                    writer.write(devDiffProfile.getDiffYearString());
                    writer.write(devName);
                    writer.write(Integer.toString(devDiffProfile.getYearOfFirstContrib()));
                    writer.write(Integer.toString(devDiffProfile.getCommitDiffs()));
                    writer.write(Integer.toString(devDiffProfile.getYearExp()));
                    for(int i = 0 ; i < ConstantUtil.majorTopicList.size() ; i ++){
                        String topicName = ConstantUtil.majorTopicList.get(i);
                        writer.write(Double.toString(devDiffProfile.getDevKUprofileMap().get(topicName)));
                    }
                    writer.endRecord();
                }
            }

            for(String yearKey : writerList.keySet()){
                writerList.get(yearKey).close();
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PerYearDeveloperContributionAnalyzer ob = new PerYearDeveloperContributionAnalyzer();
        ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for(String projectName : projectList){
            ob.calculateDevDiffProfile(projectName);
            System.out.println("------ Finish ["+ projectName +"] ---------");
        }
        
        System.out.println("Program finishes successfully");
    }
    
}
