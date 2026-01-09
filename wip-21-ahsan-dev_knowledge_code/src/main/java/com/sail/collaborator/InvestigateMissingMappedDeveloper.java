package com.sail.collaborator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;

import net.ricecode.similarity.LevenshteinDistanceStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

public class InvestigateMissingMappedDeveloper {
    
    SimilarityStrategy strategy = new LevenshteinDistanceStrategy();
    StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
    public String missingDeveloperFilePath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/missing_developer_list.csv";

    class DevSimScore{
        String developerName = "";
        String matchedName = "";
        Double score = 0.0;
        public String getDeveloperName() {
            return developerName;
        }
        public void setDeveloperName(String developerName) {
            this.developerName = developerName;
        }
        public String getMatchedName() {
            return matchedName;
        }
        public void setMatchedName(String matchedName) {
            this.matchedName = matchedName;
        }
        public Double getScore() {
            return score;
        }
        public void setScore(Double score) {
            this.score = score;
        }
    }

    public String getProjectName(String htmlUrl){
		String pName = htmlUrl.split("/")[3] + "-" + htmlUrl.split("/")[4];
        return pName;
    }

    public Map<String,Set<String>> readMissingDeveloperInfo(){
        Map<String,Set<String>> missingDeveloperList = new HashMap<String,Set<String>>();
        try{
            CsvReader reader = new CsvReader(missingDeveloperFilePath);
            reader.readHeaders();
            while(reader.readRecord()){
                String htmlUrl = reader.get("html_url");
                String projectName = getProjectName(htmlUrl);
                String logName = reader.get("login");
                if(!missingDeveloperList.containsKey(projectName)){
                    missingDeveloperList.put(projectName, new HashSet<String>());
                }
                missingDeveloperList.get(projectName).add(logName);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return missingDeveloperList;
    }

    public void printMissingDevStats(Map<String,Set<String>> missingDeveloperList){
        System.out.println("---------------------------------------");
        for(String projectName : missingDeveloperList.keySet()){
            System.out.println(projectName + " " + missingDeveloperList.get(projectName).size());
        }
        System.out.println("---------------------------------------");
    }

    public DevSimScore getDeveloperNameFromSimilarity(LoadGitCommitData dataModel, String devName){
        
        DevSimScore ob = new DevSimScore();
        Map<String, Double> nameScore = new HashMap<String, Double>();
        ArrayList<String> candNameList = new ArrayList<String>();

        ArrayList<GitCommitModel> commitList = dataModel.getGitCommitList();
        for(int i = 0 ; i < commitList.size() ; i ++){
            GitCommitModel commit = commitList.get(i);
            String authorEmail = commit.getAuthorEmail();
            double scoreValue = service.score(devName, authorEmail);

            if(!candNameList.contains(commit.getAuthorName())){
                candNameList.add(commit.getAuthorName());
            }
            if(!nameScore.containsKey(commit.getAuthorName())){
                nameScore.put(commit.getAuthorName(), 1.0);
            }

            if(nameScore.get(commit.getAuthorName()) > scoreValue){
                nameScore.put(commit.getAuthorName(), scoreValue);
            }
        }

        candNameList.sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(nameScore.get(o1) < nameScore.get(o2)){
                    return -1;
                }else if (nameScore.get(o1) > nameScore.get(o2)){
                    return 1;
                }
                return 0;
            }
            
        });

        /*for(String name : candNameList){
            System.out.println(name + " " + nameScore.get(name));
        }
        System.out.println("Source: " + devName);
        System.out.println("Matched: " + candNameList.get(0) + " " + nameScore.get(candNameList.get(0)));
        */
        ob.setDeveloperName(devName);
        ob.setMatchedName(candNameList.get(0));
        ob.setScore(nameScore.get(candNameList.get(0)));
        
        return ob;
    }



    public void investigateDeveloperList(Map<String,Set<String>> missingDeveloperList){
        Map<String,Map<String,DevSimScore>> result = new HashMap<String,Map<String,DevSimScore>>();
        int total = 0;
        for(String projectName : missingDeveloperList.keySet()){
            ++total;
            String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/" + projectName + "_full_commit_data.csv";
            LoadGitCommitData dataModel = new LoadGitCommitData(projectName,commitPath);
            System.out.println("Project: " + projectName + " " + total);
            result.put(projectName, new HashMap<String,DevSimScore>());
            for(String devName : missingDeveloperList.get(projectName)){
                DevSimScore matchedResult = getDeveloperNameFromSimilarity(dataModel, devName);
                //System.out.println("Source: " + devName);
                //System.out.println("Matched: " + matchedResult.getMatchedName() + " " +matchedResult.getScore());
                result.get(projectName).put(devName, matchedResult);
            }
            
        }

        writeMatchedResult(result);
        
    }

    public void writeMatchedResult(Map<String,Map<String,DevSimScore>> result){
        
        try{
            CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/XinProjectResult/missing_mapped_result.csv");
            writer.write("Project");
            writer.write("login");
            writer.write("matched_name");
            writer.write("Score");
            writer.endRecord();

            for(String projectName : result.keySet()){
                for(String devName : result.get(projectName).keySet()){
                    writer.write(projectName);
                    writer.write(devName);
                    DevSimScore dv = result.get(projectName).get(devName);
                    writer.write(dv.getMatchedName());
                    writer.write(String.format("%.3f", dv.getScore()));
                    writer.endRecord();
                }
            }

            writer.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        ;
    }
    public static void main(String[] args) {
        InvestigateMissingMappedDeveloper ob = new InvestigateMissingMappedDeveloper();
        Map<String,Set<String>> missingDeveloperList = ob.readMissingDeveloperInfo();
        System.out.println("Total projects: " + missingDeveloperList.size());
        ob.printMissingDevStats(missingDeveloperList);
        ob.investigateDeveloperList(missingDeveloperList);
    }
}
