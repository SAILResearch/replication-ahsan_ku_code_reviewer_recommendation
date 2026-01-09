package com.sail.java.exam.dev.profile;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.github.model.KUFileModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

import org.apache.commons.math3.analysis.function.Constant;

public class DevProfileGeneratorCommitWiseUpdated_II {

	public String devProfileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile/";
	public String devDepenPofileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile_Dependency/";
	public String devProfileSuffix = "_commit_dev_profile_instance_approach_percentage";
	public String devProfileDepSuffix = "_commit_dev_profile_dependency_instance_approach_percentage";
	public String COMMIT_STRING = "COMMIT";

	public String getKnowledgeUnitDataKey(Map<String, KUFileModel> knowledgeUnitData, String fileName,
			Map<String, String> dpedendencyMemoization) {
		String result = null;
		if (dpedendencyMemoization.containsKey(fileName)) {
			return dpedendencyMemoization.get(fileName);
		}
		Set<String> keyList = knowledgeUnitData.keySet();
		for (String key : keyList) {
			if (key.contains(fileName)) {
				dpedendencyMemoization.put(fileName, key);
				return key;
			}
		}

		return result;
	}

	public boolean isSelectedCommit(ArrayList<GitCommitModel> selectedGitCommits, GitCommitModel commit){
		boolean result = false;
		for(GitCommitModel selectCommit : selectedGitCommits){
			if (selectCommit.getCommitId().equals(commit.getCommitId())){
				return true;
			}
		}
		return result;
	}

	public void updateDevKnowledgeUnitProfile(
		Map<String, Map<String, Set<String>>> profileMap,
		KUFileModel knowledgeOb,
		String authorName, 
		String commitId){
						
		if (!profileMap.containsKey(authorName)) {
			profileMap.put(authorName, new HashMap<String, Set<String>>());
		}
		for (String ku : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
			if (!profileMap.get(authorName).containsKey(ku)) {
				profileMap.get(authorName).put(ku, new HashSet<String>());
			}
			profileMap.get(authorName).get(ku).add(commitId);
		}
	}


	public void updateFileKnowledgeUnitMatrix(Map<String,Map<String,Double>> fileKuMatrix, 
		String fileName, String knowledgeUnit, double value){
		// create instance for the file
		if(!fileKuMatrix.containsKey(fileName)){
			fileKuMatrix.put(fileName, new HashMap<String,Double>());
		}
		// create instance for the knowledge unit of the file
		if(!fileKuMatrix.get(fileName).containsKey(knowledgeUnit)){
			fileKuMatrix.get(fileName).put(knowledgeUnit, 0.0);
		}
		// update with the maximum value
		if (fileKuMatrix.get(fileName).get(knowledgeUnit) < value){
			fileKuMatrix.get(fileName).put(knowledgeUnit, value);
		}
	}

	public void updateDevKnoweldgeUnitMatrix(Map<String, Map<String, Map<String,Double>>> developerfileKnowledgeUnitMatrix,
		String developerName, String fileName, String knowledgeUnit, double value){
			if(!developerfileKnowledgeUnitMatrix.containsKey(developerName)){
				developerfileKnowledgeUnitMatrix.put(developerName, new HashMap<String, Map <String, Double>>());
			}
			if(!developerfileKnowledgeUnitMatrix.get(developerName).containsKey(fileName)){
				developerfileKnowledgeUnitMatrix.get(developerName).put(fileName, new HashMap<String, Double>());
			}
			if (!developerfileKnowledgeUnitMatrix.get(developerName).get(fileName).containsKey(knowledgeUnit)){
				developerfileKnowledgeUnitMatrix.get(developerName).get(fileName).put(knowledgeUnit, 0.0);
			}
			// update with the maximum value
			if(developerfileKnowledgeUnitMatrix.get(developerName).get(fileName).get(knowledgeUnit) < value){
				developerfileKnowledgeUnitMatrix.get(developerName).get(fileName).put(knowledgeUnit, value);
			}
	}

	public void updateKnowledgeUnitCalculationMatrixInCommits(Map<String,Map<String,Double>> fileKuMatrix,
	Map<String, Map<String, Map<String,Double>>> developerfileKnowledgeUnitMatrix, KUFileModel knowledgeOb,
	String developerName, String fileName){
		for(String knowledgeUnit : knowledgeOb.getKnowledgeUnitPerFile().keySet()){
			Double value = knowledgeOb.getKnowledgeUnitPerFile().get(knowledgeUnit);
			updateFileKnowledgeUnitMatrix(fileKuMatrix, fileName, knowledgeUnit, value);
			updateDevKnoweldgeUnitMatrix(developerfileKnowledgeUnitMatrix, developerName, fileName, knowledgeUnit, value);
		}
	}

	public void updateCommitCount(Map<String, Map<String, Map<String,Double>>> developerfileKnowledgeUnitMatrix,
	String developerName){
		if(!developerfileKnowledgeUnitMatrix.get(developerName).containsKey(COMMIT_STRING)){
			developerfileKnowledgeUnitMatrix.get(developerName).put(COMMIT_STRING, new HashMap<String,Double>());
			developerfileKnowledgeUnitMatrix.get(developerName).get(COMMIT_STRING).put(COMMIT_STRING, 0.0);
		}
		developerfileKnowledgeUnitMatrix.get(developerName).get(COMMIT_STRING).put(COMMIT_STRING, developerfileKnowledgeUnitMatrix.get(developerName).get(COMMIT_STRING).get(COMMIT_STRING) + 1);
	}

	public void createDevprofile(String projectFullName, boolean isDependency, String writingPath) {

		Map<String,Map<String,Double>> fileKnowledgeUnitOccuranceMatrix = new HashMap<String,Map<String,Double>>();
		Map<String, Map<String, Map<String,Double>>> developerfileKnowledgeUnitMatrix = new HashMap<String, Map<String, Map< String, Double>>>();

		Map<String, Map<String, Set<String>>> developerKnolwedgeProfile = new HashMap<String, Map<String, Set<String>>>();
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		Set<String> projectJavaFileList = new HashSet<String>();
		Map<String, String> dpedendencyMemoization = new HashMap<String, String>();

		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
		Map<String, KUFileModel> knowledgeUnitData = null;

		// Get the first element
		String fileTagFirst = projectFullName + "-" + selectedGitCommits.get(0).getCommitId() + ".csv";
		String fileLocationFirst = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTagFirst;
		knowledgeUnitData = ob.extractKnowledgeUnits(fileLocationFirst, projectFullName);

		System.out.println("[Start] Devloper KU Profile...");

		for (int i = 0; i < gitCommitList.size(); i++) {

			if (i % 500 == 0) {
				System.out.println("Done working with commits : " + (i + 1));
			}

			GitCommitModel commit = gitCommitList.get(i);
			String authorName = commit.getAuthorName();

			if(isSelectedCommit(selectedGitCommits, commit)){
				String fileTag = projectFullName + "-" + commit.getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
				dpedendencyMemoization = new HashMap<String, String>();
				//System.out.println("Update: " + commit.getCommitAuthorDate());
			}

			// update developer's profile
			if (commit.getNoChangedFiles() > 0) {
				boolean changeInKnolwedgeUnit = false;
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						updateDevKnowledgeUnitProfile(developerKnolwedgeProfile,knowledgeOb, authorName, commit.getCommitId());
						updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
						developerfileKnowledgeUnitMatrix, knowledgeOb, authorName, changeFile);
						changeInKnolwedgeUnit = true;
						if (isDependency) {
							// Dependency update
							for (String f : knowledgeOb.getDependencyList()) {
								// System.out.println("F " + f + " " + knowledgeUnitData.containsKey(f));
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateDevKnowledgeUnitProfile(developerKnolwedgeProfile, kuOb,
											authorName, commit.getCommitId());
									updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, changeFile);
								}
							}
							// Parent class Dependency update
							for (String f : knowledgeOb.getParentClassList()) {
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateDevKnowledgeUnitProfile(developerKnolwedgeProfile, kuOb,
											authorName, commit.getCommitId());
									updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, changeFile);
								}
							}
						}
					}
				}
				if(changeInKnolwedgeUnit){
					updateCommitCount(developerfileKnowledgeUnitMatrix, authorName);
				}
			}
		}
		System.out.println("[Finish creating dev ku profile]");

		//writeDeveloperKUProfile(writingPath, developerKnolwedgeProfile, projectFullName,
		//		projectJavaFileList, gitCommitList.size());
		writeDeveloperKUProfileUpdated(writingPath, fileKnowledgeUnitOccuranceMatrix, developerfileKnowledgeUnitMatrix,
		projectFullName);

		String fileKnowledgePath = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/DebugResult/debug_max_ku_" + projectFullName + ".csv";	
		writeMaxKnowledgeUnitPerFile(fileKnowledgePath, fileKnowledgeUnitOccuranceMatrix);

		System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void writeMaxKnowledgeUnitPerFile(String path, Map<String,Map<String,Double>> fileKnowledgeUnitOccuranceMatrix){
		try{
			CsvWriter writer = new CsvWriter(path);
			writer.write("File_Name");
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));	
				writer.write(topic);
			}
			writer.endRecord();
			for(String fileName : fileKnowledgeUnitOccuranceMatrix.keySet()){
				writer.write(fileName);
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));	
					if(fileKnowledgeUnitOccuranceMatrix.get(fileName).containsKey(topic)){
						writer.write(Double.toString (fileKnowledgeUnitOccuranceMatrix.get(fileName).get(topic)));
					}else{
						writer.write("0");
					}
				}	
				writer.endRecord();
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public Map<String, Double> getMaxKnowledgeUnitValue(Map<String,Map<String,Double>> fileKnowledgeUnitOccuranceMatrix){
		Map<String, Double> maxKnowledgeUnitMatrix = new HashMap<String, Double>();
		for (String topicId : ConstantUtil.topicCategoryList) {
			String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
			maxKnowledgeUnitMatrix.put(topic, 0.0);
		}
		for (String fileName : fileKnowledgeUnitOccuranceMatrix.keySet()){
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));	
				double topicValue = 0.0;
				if (fileKnowledgeUnitOccuranceMatrix.get(fileName).containsKey(topic)){
					topicValue = fileKnowledgeUnitOccuranceMatrix.get(fileName).get(topic);
				}
				maxKnowledgeUnitMatrix.put(topic, maxKnowledgeUnitMatrix.get(topic) + topicValue);
			}
		}
		return maxKnowledgeUnitMatrix;
	}
	
	public void writeDeveloperKUProfileUpdated(String path, Map<String,Map<String,Double>> fileKnowledgeUnitOccuranceMatrix,
	Map<String, Map<String, Map<String,Double>>> developerfileKnowledgeUnitMatrix, 
	String projectName) {
		Map<String, Double> maxKnowledgeUnitMatrix  = getMaxKnowledgeUnitValue(fileKnowledgeUnitOccuranceMatrix);
		try {
			CsvWriter writer = new CsvWriter(path);
			writer.write("Project_Name");
			writer.write("Developer_Name");
			//writer.write("Normalized_Value");
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
				writer.write(topic);
			}
			writer.write("Total_Commits_Java_File");
			writer.endRecord();

			for (String devName : developerfileKnowledgeUnitMatrix.keySet()) {
				Double developerCommitCountChangeJavaFile = developerfileKnowledgeUnitMatrix.get(devName).get(COMMIT_STRING).get(COMMIT_STRING);
				/*if (developerCommitCountChangeJavaFile < 5) {
					continue;
				}*/

				writer.write(projectName);
				writer.write(devName);

				Map<String, Double> maxDevKnowledgeUnitMatrix  = getMaxKnowledgeUnitValue(developerfileKnowledgeUnitMatrix.get(devName));
		
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					double v = maxDevKnowledgeUnitMatrix.get(topic);
					double normalizedValue = Math.max(1, maxKnowledgeUnitMatrix.get(topic));
					v = (v * 100) / normalizedValue;
					if (normalizedValue == 0){
						writer.write("0");
					}else {
						writer.write(Double.toString(v));
					}
				}
				writer.write(Double.toString(developerCommitCountChangeJavaFile));
				writer.endRecord();
			}

			writer.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void writeDeveloperKUProfile(String path, Map<String, Map<String, Set<String>>> developerKnolwedgeProfile,
			String projectName, Set<String> projectJavaFileList, double totalCommits) {
		try {
			CsvWriter writer = new CsvWriter(path);
			writer.write("Project_Name");
			writer.write("Developer_Name");
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
				writer.write(topic);
			}
			writer.write("Total_Commits_Java_File");
			writer.endRecord();

			for (String devName : developerKnolwedgeProfile.keySet()) {
				if (developerKnolwedgeProfile.get(devName).get("COMMIT").size() < 5) {
					continue;
				}
				writer.write(projectName);
				writer.write(devName);
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					if (developerKnolwedgeProfile.get(devName).containsKey(topic)) {
						double v = developerKnolwedgeProfile.get(devName).get(topic).size();
						v = v / developerKnolwedgeProfile.get(devName).get("COMMIT").size();
						writer.write(Double.toString(v));
					} else {
						writer.write("0");
					}
				}
				writer.write(Integer.toString(developerKnolwedgeProfile.get(devName).get("COMMIT").size()));
				writer.endRecord();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateDeveloperProfileSelectedProjectsWithotDependency() {
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList) {
			/*if(!project.equals("apache_hbase")){
				continue;
			}*/
			System.out.println("Starts working project [ " + project + " ]");
			String writingPath = devProfileDir + project + devProfileSuffix + ".csv";
			this.createDevprofile(project, false, writingPath);
		}
	}

	public void generateDeveloperProfileSelectedProjectsWithDependency() {
		
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList) {
			/*if(!project.equals("apache_hbase")){	
				continue;
			}*/
			System.out.println("Starts working project [ " + project + " ]");
			String writingPath = devDepenPofileDir + project + devProfileDepSuffix + ".csv";
			this.createDevprofile(project, true, writingPath);
		}
	}

	public static void main(String[] args) {
		DevProfileGeneratorCommitWiseUpdated_II ob = new DevProfileGeneratorCommitWiseUpdated_II();
		// apache_lucene
		// jruby_jruby
		// apache_hbase
		//String project = "apache_hbase";

		//String writingPath = ob.devDepenPofileDir + project + ob.devProfileDepSuffix + ".csv";
		//ob.createDevprofile(project, true, writingPath);

		//String writingPath = ob.devProfileDir + project + ob.devProfileSuffix + ".csv";
		//ob.createDevprofile(project, false, writingPath);
		// ob.createDevprofileWithDependencyAnalysis("apache_hbase");

		ob.generateDeveloperProfileSelectedProjectsWithotDependency();
		ob.generateDeveloperProfileSelectedProjectsWithDependency();

		System.out.println("Program finishes successfully.");
	}
}
