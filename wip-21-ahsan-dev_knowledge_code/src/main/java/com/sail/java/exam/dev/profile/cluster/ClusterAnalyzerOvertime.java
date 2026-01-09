package com.sail.java.exam.dev.profile.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.TextUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ClusterAnalyzerOvertime {


    String overtimeDataPath  = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/overtime_result/cluster_full_data_normalized_overtime__dependency_OFF_0.9_cl_100.csv";


    public Map<String, Map<String, ArrayList<DevProfileWithClusterModel>>> readOvertimeClusterData(String path){
        Map<String, Map<String, ArrayList<DevProfileWithClusterModel>>> data = new HashMap<String, Map<String, ArrayList<DevProfileWithClusterModel>>>();
        
        try{
            CsvReader reader = new CsvReader(path);
            reader.readHeaders();
            while(reader.readRecord()){
                String projectName = reader.get("Project_Name");
                String developerName = reader.get("Developer_Name");
                String developerYearName = reader.get("Developer_Year_Name");
                System.out.println(projectName);
                int totalJavaCommits = (int) Double.parseDouble(reader.get("Total_Commits_Java_File"));
                int totalCommits = (int) Double.parseDouble(reader.get("total_commit"));
                int year = Integer.parseInt(reader.get("year"));
                boolean isCoreDev = reader.get("core_developer").compareTo("Y") == 0 ? true : false;
                
                Map<String, Double> kuValues = new HashMap<String, Double>();

                for(int i = 0 ; i < ConstantUtil.majorTopicList.size() ; i ++){
                    String topicName = ConstantUtil.majorTopicList.get(i);
                    Double value = Double.parseDouble(reader.get(topicName));
                    kuValues.put(topicName, value);
                }

                int pcaSelectedElements = Integer.parseInt(reader.get("PCA_selected_elements_95"));
                Map<String, Double> pcaValues = new HashMap<String, Double>();
                for(int i = 0 ; i < pcaSelectedElements ; i ++){
                    String pcaName = "PCA" + (i + 1);
                    Double value = Double.parseDouble(reader.get(pcaName));
                    pcaValues.put(pcaName, value);
                }
                String clusterLebel = reader.get("kmeans_cluster_label");

                DevProfileWithClusterModel ob = new DevProfileWithClusterModel();
                ob.setProjectName(projectName);
                ob.setDeveloperName(developerName);
                ob.setTotalCommits(totalCommits);
                ob.setTotalJavaCommits(totalJavaCommits);
                ob.setYear(year);
                ob.setCoreDev(isCoreDev);
                ob.setDeveloperYearName(developerYearName);
                ob.setKuValues(kuValues);
                ob.setPcaSelectedElements(pcaSelectedElements);
                ob.setPcaValues(pcaValues);
                ob.setClusterLebel(clusterLebel);

                if(!data.containsKey(projectName)){
                    data.put(projectName, new HashMap<String, ArrayList<DevProfileWithClusterModel>>());
                }
                if(!data.get(projectName).containsKey(developerName)){
                    data.get(projectName).put(developerName, new ArrayList<DevProfileWithClusterModel>());
                }
                data.get(projectName).get(developerName).add(ob);

            }

            System.out.println("Data Load Complete.");
            for(String projectName : data.keySet()){
                for(String devName : data.get(projectName).keySet()){
                    data.get(projectName).get(devName).sort(new Comparator<DevProfileWithClusterModel>() {

                        @Override
                        public int compare(DevProfileWithClusterModel o1, DevProfileWithClusterModel o2) {
                            
                            if (o1.getYear() < o2.getYear()){
                                return -1;
                            }
                            if (o1.getYear() > o2.getYear()){
                                return 1;
                            }
                            return 0;
                        }
                        
                    });
                }
            }
            System.out.println("Loaded Data Sort Complete");


        }catch(Exception e){
            e.printStackTrace();
        }
        //checkSorting(data);
        return data;
    }


    public void checkSorting( Map<String, Map<String, ArrayList<DevProfileWithClusterModel>>> data){
        boolean flag = false;
        for(String projectName : data.keySet()){
            for(String devName : data.get(projectName).keySet()){
                if(data.get(projectName).get(devName).size() > 4){
                    for(DevProfileWithClusterModel ob : data.get(projectName).get(devName)){
                        System.out.println(ob.getDeveloperYearName());
                    }
                    flag = true;
                    break;
                }
            }
            if(flag){
                break;
            }
        }
    }

    public ArrayList<Map<String,Integer>> getDeveloperHistory(String projectName) {
		List<String> yearStringList = Arrays.asList("< 2016", "2017", "2018", "2019", "2020", "2021", "2022");
		Map<String, Integer> lastCommitAuthorMap = new HashMap<String, Integer>();
        Map<String, Integer> firstCommitAuthorMap = new HashMap<String, Integer>();
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		for (int i = 0; i < gitCommitList.size(); i++) {
			GitCommitModel commit = gitCommitList.get(i);
            if(commit.getChangedJavaFileList().size() <= 0) continue;
			String devName = commit.getAuthorName();
			int yearFirstCommitAuthor = commit.getCommitAuthorJodaDate().getYear();
			if (!firstCommitAuthorMap.containsKey(devName)) {
				firstCommitAuthorMap.put(devName, yearFirstCommitAuthor);
                lastCommitAuthorMap.put(devName, yearFirstCommitAuthor);
			}
            if(lastCommitAuthorMap.containsKey(devName)){
                if(lastCommitAuthorMap.get(devName) < yearFirstCommitAuthor){
                    lastCommitAuthorMap.put(devName, yearFirstCommitAuthor);
                }
            }
            
			// System.out.println( "Commit: " + i + " " + commit.getCommitAuthorJodaDate());
		}
        ArrayList<Map<String,Integer>> firstLastCommit = new ArrayList<Map<String,Integer>>();
        firstLastCommit.add(firstCommitAuthorMap);
        firstLastCommit.add(lastCommitAuthorMap);
		Map<String, Set<String>> devFreqYear = new HashMap<String, Set<String>>();
		for (String devName : firstCommitAuthorMap.keySet()) {
			int yearValue = firstCommitAuthorMap.get(devName);
			if (yearValue < 2016) {
				if (!devFreqYear.containsKey("< 2016")) {
					devFreqYear.put("< 2016", new HashSet<String>());
				}
				devFreqYear.get("< 2016").add(devName);
			} else {
				if (!devFreqYear.containsKey(Integer.toString(yearValue))) {
					devFreqYear.put(Integer.toString(yearValue), new HashSet<String>());
				}
				devFreqYear.get(Integer.toString(yearValue)).add(devName);
			}
		}
		for (String yearString : yearStringList) {
			if (devFreqYear.containsKey(yearString)) {
				System.out.println(yearString + " " + devFreqYear.get(yearString).size());
			} else {
				System.out.println(yearString + " " + 0);
			}
		}

		for (String devName : firstCommitAuthorMap.keySet()) {
			int yearValue = firstCommitAuthorMap.get(devName);
			System.out.println(devName + " " + yearValue);
		}
		return firstLastCommit;
	}

    public void analyzeOvertimeCluster(){
        Map<String, Map<String, ArrayList<DevProfileWithClusterModel>>> data = readOvertimeClusterData(this.overtimeDataPath);
        Set<String> developerList = new HashSet<String>();
        Set<String> findChangedClusterLabel = new HashSet<String>();
        Set<String> coreDeveloperClusterLabelChange = new HashSet<String>();
        Map<String, ArrayList<String>> freqChanggeClusterLabel = new HashMap<String, ArrayList<String>>();
        Map<String, Set<String>> clusterSize = new HashMap<String,Set<String>>();

       
        ArrayList<ClusterMovementModel> clusterMovInstances = new ArrayList<ClusterMovementModel>();
        Map<String, ArrayList<ClusterMovementModel>> devleoperClusterMovement = new HashMap<String,  ArrayList<ClusterMovementModel>>();
        DescriptiveStatistics nonMovementCommits = new DescriptiveStatistics();
        DescriptiveStatistics movementCommits = new DescriptiveStatistics();
        DescriptiveStatistics nonMovementDevCommitActivity = new DescriptiveStatistics();
        DescriptiveStatistics movementDevCommitActivity = new DescriptiveStatistics();
        for(String projectName : data.keySet()){
            for(String devName : data.get(projectName).keySet()){
                String key = projectName + "-" + devName;
                ArrayList<DevProfileWithClusterModel> devHistory = data.get(projectName).get(devName);
                for(int i = 0 ; i < devHistory.size() ; i ++ ){
                    DevProfileWithClusterModel pres = devHistory.get(i);
                    if(!clusterSize.containsKey(pres.getClusterLebel())){
                        clusterSize.put(pres.clusterLebel, new HashSet<String>());
                    }
                    clusterSize.get(pres.getClusterLebel()).add(key);
                }
            }
        }
        for(String projectName : data.keySet()){
            ArrayList<Map<String,Integer>> firstLastCommit = getDeveloperHistory(projectName);
            Map<String, Integer> firstCommitAuthorMap = firstLastCommit.get(0);
            Map<String, Integer> lastCommitAuthorMap = firstLastCommit.get(1);

            for(String devName : data.get(projectName).keySet()){
                String key = projectName + "-" + devName;
                ArrayList<DevProfileWithClusterModel> devHistory = data.get(projectName).get(devName);
                boolean movement = false;
                int lastCommitYear = 2016;
                for(int i = 1 ; i < devHistory.size() ; i ++ ){
                    DevProfileWithClusterModel old = devHistory.get(i-1);
                    DevProfileWithClusterModel pres = devHistory.get(i);
                    
                    System.out.println(old.getTotalCommits() + " " + pres.getTotalCommits());
                    
                    if(old.getTotalCommits() != pres.getTotalCommits()){
                        lastCommitYear = pres.getYear();
                    }

                    if(old.getClusterLebel().compareTo(pres.clusterLebel) != 0){
                        movement = true;
                        findChangedClusterLabel.add(key);
                        if(pres.isCoreDev()){
                            coreDeveloperClusterLabelChange.add(key);
                        }
                        if(!freqChanggeClusterLabel.containsKey(key)){
                            freqChanggeClusterLabel.put(key, new ArrayList<String>());
                        }
                        freqChanggeClusterLabel.get(key).add(Integer.toString(pres.getYear()) + "-"  + pres.getClusterLebel());
                   
                        ClusterMovementModel ob = new ClusterMovementModel();
                        ob.setDeveloperName(pres.getDeveloperName());
                        ob.setProjectName(pres.getProjectName());
                        ob.setFromLabel(old.getClusterLebel());
                        ob.setToLabel(pres.getClusterLebel());
                        ob.setFromYear(old.getYear());
                        ob.setToYear(pres.getYear());
                        ob.setFromClusterSize(clusterSize.get(old.getClusterLebel()).size());
                        ob.setToClusterSize(clusterSize.get(pres.getClusterLebel()).size());
                        ob.setFromPoint(old);
                        ob.setToPoint(pres);
                        clusterMovInstances.add(ob);
                       
                        if(!devleoperClusterMovement.containsKey(key)){
                            devleoperClusterMovement.put(key, new ArrayList<ClusterMovementModel>());
                        }
                        devleoperClusterMovement.get(key).add(ob);
                    }
                }
                if(movement == false){
                    nonMovementCommits.addValue(devHistory.get(devHistory.size()-1).getTotalCommits());
                    int firstCommitYear = firstCommitAuthorMap.get(devName);
                    int lastCommityear  =  lastCommitAuthorMap.get(devName);
                    nonMovementDevCommitActivity.addValue(lastCommityear - firstCommitYear);
                   
                    // devHistory.get(0).first;
                }else{
                    movementCommits.addValue(devHistory.get(devHistory.size()-1).getTotalCommits());
                    int firstCommitYear = firstCommitAuthorMap.get(devName);
                    int lastCommityear  =  lastCommitAuthorMap.get(devName);
                    movementDevCommitActivity.addValue(lastCommityear - firstCommitYear);
                   
                }
                developerList.add(key);
            }
        }
    
        for(String devName : findChangedClusterLabel){
            System.out.println("Core: " + coreDeveloperClusterLabelChange.contains(devName) + " Name: " + devName + " Freq: " + freqChanggeClusterLabel.get(devName).size());
        }

        String path = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/Result_June_26_2022/DataAnalysisP2/";
        /*writeDeveloperClusterLabelChangeResult(findChangedClusterLabel,coreDeveloperClusterLabelChange,freqChanggeClusterLabel,clusterSize,path);
        writeClusterMovementInstances(clusterMovInstances,path);
        writeClusterMovementFirstToLast(devleoperClusterMovement,path);*/

        System.out.println("Changed cluster label: " + findChangedClusterLabel.size() + "/" + developerList.size() + " Core Dev: " + coreDeveloperClusterLabelChange.size());
        System.out.println("Non movement developers commit stats");
        System.out.println("Total nonmovement commit developers: " + nonMovementCommits.getValues().length);
        System.out.println("Min: " + nonMovementCommits.getMin());
        System.out.println("Median: " + nonMovementCommits.getPercentile(50));
        System.out.println("Max: " + nonMovementCommits.getMax());

        System.out.println("Movement developers commit stats");
        System.out.println("Total movement commit developers: " + movementCommits.getValues().length);
        System.out.println("Min: " + movementCommits.getMin());
        System.out.println("Median: " + movementCommits.getPercentile(50));
        System.out.println("Max: " + movementCommits.getMax());

        System.out.println("Commit Activity NOnmovement");
        System.out.println("Min: " + nonMovementDevCommitActivity.getMin());
        System.out.println("Median: " + nonMovementDevCommitActivity.getPercentile(50));
        System.out.println("Q3: " + nonMovementDevCommitActivity.getPercentile(75));
        System.out.println("Max: " + nonMovementDevCommitActivity.getMax());

        System.out.println("Commit Activity Movement");
        System.out.println("Min: " + movementDevCommitActivity.getMin());
        System.out.println("Q1: " + movementDevCommitActivity.getPercentile(25));
        System.out.println("Median: " + movementDevCommitActivity.getPercentile(50));
        System.out.println("Q3: " + movementDevCommitActivity.getPercentile(75));
        System.out.println("Max: " + movementDevCommitActivity.getMax());
    
        Map<String,Double> KuChangesValues = new HashMap<String,Double>();
        List<String> topicList = ConstantUtil.majorTopicList;
        for(int i = 0 ; i < ConstantUtil.majorTopicList.size() ; i ++ ){
            String topicName = ConstantUtil.majorTopicList.get(i);
            KuChangesValues.put(topicName, 0.0);
        }

        for(ClusterMovementModel mv : clusterMovInstances){
           DevProfileWithClusterModel old =  mv.getFromPoint();
           DevProfileWithClusterModel pres =  mv.getToPoint();
        
           for(int i = 0 ; i < ConstantUtil.majorTopicList.size() ; i ++ ){
               String topicName = ConstantUtil.majorTopicList.get(i);
               System.out.println(topicName);
               Double changeValue = Math.abs(pres.getKuValues().get(topicName) - old.getKuValues().get(topicName));
               Double updatedValue = (changeValue > 0 ? 1.0 : 0.0);
               KuChangesValues.put(topicName, KuChangesValues.get(topicName) + changeValue);
            }

        }

        topicList.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				double v1 = KuChangesValues.get(o1);
                double v2 = KuChangesValues.get(o2);

                if(v1 > v2) return -1;
                else if (v1 < v2) return 1;
				return 0;
			}
            
        });

        for(String topicName : topicList){
            System.out.println(topicName + " " + KuChangesValues.get(topicName));
        }


    }

    public void writeClusterMovementFirstToLast(Map<String, ArrayList<ClusterMovementModel>> devleoperClusterMovement,
    String path){
        CsvWriter writer = new CsvWriter(path + "movement_instances_per_developer.csv");
        try{
            writer.write("Project_Name");
            writer.write("Developer_Name");
            writer.write("From_Year");
            writer.write("To_Year");
            writer.write("From_Cluster_Label");
            writer.write("To_Cluster_Label");
            writer.write("From_Cluster_Size");
            writer.write("To_Cluster_Size");
            writer.endRecord();

            for(String key : devleoperClusterMovement.keySet()){
                ClusterMovementModel first = devleoperClusterMovement.get(key).get(0);
                ClusterMovementModel last = devleoperClusterMovement.get(key).get(devleoperClusterMovement.get(key).size()-1);
                writer.write(first.getProjectName());
                writer.write(first.getDeveloperName());
                writer.write(Integer.toString(first.getFromYear()));
                writer.write(Integer.toString(last.getToYear()));
                writer.write(first.getFromLabel());
                writer.write(last.getToLabel());
                writer.write(Integer.toString(first.getFromClusterSize()));
                writer.write(Integer.toString(last.getToClusterSize()));
                writer.endRecord();
            }

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public void writeClusterMovementInstances(ArrayList<ClusterMovementModel> clusterMovInstances,
    String path){
        CsvWriter writer = new CsvWriter(path + "movement_instances.csv");
        try{
            writer.write("Project_Name");
            writer.write("Developer_Name");
            writer.write("From_Year");
            writer.write("To_Year");
            writer.write("From_Cluster_Label");
            writer.write("To_Cluster_Label");
            writer.write("From_Cluster_Size");
            writer.write("To_Cluster_Size");
            writer.endRecord();

            for(ClusterMovementModel ob : clusterMovInstances){
                writer.write(ob.getProjectName());
                writer.write(ob.getDeveloperName());
                writer.write(Integer.toString(ob.getFromYear()));
                writer.write(Integer.toString(ob.getToYear()));
                writer.write(ob.getFromLabel());
                writer.write(ob.getToLabel());
                writer.write(Integer.toString(ob.getFromClusterSize()));
                writer.write(Integer.toString(ob.getToClusterSize()));
                writer.endRecord();
            }

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public void writeDeveloperClusterLabelChangeResult(Set<String> findChangedClusterLabel,
    Set<String> coreDeveloperClusterLabelChange,
    Map<String, ArrayList<String>> freqChanggeClusterLabel,
    Map<String, Set<String>> clusterSize,
    String path
    ){
        
        CsvWriter writer =  new CsvWriter(path + "dev_change_cluster.csv");

        try{
            writer.write("Project_Name");
            writer.write("Developer_Name");
            writer.write("Is_Core_Developer");
            writer.write("Frequencey_Of_Change");
            writer.write("Change_String");
            writer.endRecord();

            for(String key : findChangedClusterLabel){
                String projectName = key.split("-")[0];
                String devName = key.split("-")[1];
                String isCore = coreDeveloperClusterLabelChange.contains(key) ? "Y" : "N";
                String frequencyChange = Integer.toString(freqChanggeClusterLabel.get(key).size());
                
                ArrayList<String> clusterLabelSize = new ArrayList<String>();
                for(String year : freqChanggeClusterLabel.get(key)){
                    String res = year + "-" + clusterSize.get(year.split("-")[1]).size();
                    clusterLabelSize.add(res);
                }
                String changeString = TextUtil.convertListToString(clusterLabelSize, ">");
                writer.write(projectName);
                writer.write(devName);
                writer.write(isCore);
                writer.write(frequencyChange);
                writer.write(changeString);
                writer.endRecord();
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClusterAnalyzerOvertime ob = new ClusterAnalyzerOvertime();
        ob.analyzeOvertimeCluster();
        //ob.readOvertimeClusterData(ob.overtimeDataPath);
        System.out.println("Program finishes successfully.");
    }
}
