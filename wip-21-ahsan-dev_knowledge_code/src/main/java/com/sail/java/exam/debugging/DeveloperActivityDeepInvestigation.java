package com.sail.java.exam.debugging;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.github.model.KUFileModel;
import com.sail.java.exam.dev.profile.DevProfileGeneratorCommitWise;
import com.sail.java.exam.dev.profile.KnowledgeUnitExtractorDevStudy;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

public class DeveloperActivityDeepInvestigation {

	public String devProfileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_4_2021/Dev_Profile/";
	public String devDepenPofileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_4_2021/Dev_Profile_Dependency/";
	public String devProfileSuffix = "_commit_dev_profile";
	public String devProfileDepSuffix = "_commit_dev_profile_dependency";

	
	public String getKnowledgeUnitDataKey(
			Map<String, KUFileModel> knowledgeUnitData,
			String fileName,
			Map<String,String> dpedendencyMemoization
			) {
		String result = null;
		if(dpedendencyMemoization.containsKey(fileName)) {
			return dpedendencyMemoization.get(fileName);
		}
		Set<String> keyList = knowledgeUnitData.keySet();
		for(String key : keyList) {
			if(key.contains(fileName)) {
				dpedendencyMemoization.put(fileName, key);
				return key;
			}
		}
		
		return result;
	}
	
	public int getChangeKUFile(int left, ArrayList<GitCommitModel> selectedGitCommits, GitCommitModel commit) {
		int result = -1;
		for (int i = left + 1; i < selectedGitCommits.size(); i++) {
			GitCommitModel selectedCommit = selectedGitCommits.get(i);
			if (commit.getCommitJodaDate().isAfter(selectedCommit.getCommitJodaDate())) {
				return i;
			}
			if (commit.getCommitJodaDate().isEqual(selectedCommit.getCommitJodaDate())) {
				return i;
			}
		}
		return result;
	}

	public void createDevprofileWithDependencyAnalysis(String projectFullName) {
		
        Map<String, Map<String, Set<String>>> developerKnolwedgeProfileWithDependency = new HashMap<String, Map<String, Set<String>>>();

	
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		Set<String> projectJavaFileList = new HashSet<String>();
		Map<String,String> dpedendencyMemoization = new HashMap<String,String>();
		
		/*
		 * for (GitCommitModel commit : selectedGitCommits) { System.out.println("S: " +
		 * commit.getCommitCommitterDate()); }
		 */

		int leftIndex = 0;

		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
		Map<String, KUFileModel> knowledgeUnitData = null;

		System.out.println("[Start] Devloper KU Profile...");

		for (int i = 0; i < gitCommitList.size(); i++) {

			if (i % 500 == 0) {
				System.out.println("Done working with commits : " + (i + 1));
			}

			//if(i > 10000) break;
			
			GitCommitModel commit = gitCommitList.get(i);

			if (knowledgeUnitData == null) {
				leftIndex = 0;
				String fileTag = projectFullName + "-" + selectedGitCommits.get(leftIndex).getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
			}
			
			//projectJavaFileList.addAll(knowledgeUnitData.keySet());
			
			if (commit.getNoChangedFiles() > 0) {
				
				// update developer's profile
				String authorName = commit.getAuthorName();
				
				// projectJavaFileList.addAll(commit.getChangedJavaFileList());
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {

						if (!developerKnolwedgeProfileWithDependency.containsKey(authorName)) {
							developerKnolwedgeProfileWithDependency.put(authorName, new HashMap<String,Set<String>>());
						}
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						for (String ku : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
							if (!developerKnolwedgeProfileWithDependency.get(authorName).containsKey(ku)) {
								developerKnolwedgeProfileWithDependency.get(authorName).put(ku,
										new HashSet<String>());
							}
							
							developerKnolwedgeProfileWithDependency.get(authorName).get(ku).add(commit.getCommitId());
						}

						// Dependency update
						for (String f : knowledgeOb.getDependencyList()) {
							//System.out.println("F " + f + " " + knowledgeUnitData.containsKey(f));
							String key = getKnowledgeUnitDataKey(knowledgeUnitData,f, dpedendencyMemoization);
							if (key != null) {
								//projectJavaFileList.add(key);
								KUFileModel kuOb = knowledgeUnitData.get(key);
								for (String ku : kuOb.getKnowledgeUnitPerFile().keySet()) {
									if (!developerKnolwedgeProfileWithDependency.get(authorName).containsKey(ku)) {
										developerKnolwedgeProfileWithDependency.get(authorName).put(ku,
												new HashSet<String>());
									}
									
									developerKnolwedgeProfileWithDependency.get(authorName).get(ku).add(commit.getCommitId());
									//System.out.println("Invoked Method Dependency.");
								}
							}
						}

						if(!developerKnolwedgeProfileWithDependency.get(authorName).containsKey("COMMIT")) {
							developerKnolwedgeProfileWithDependency.get(authorName).put("COMMIT", new HashSet<String>());
				
						}
						developerKnolwedgeProfileWithDependency.get(authorName).get("COMMIT").add(commit.getCommitId());
						
						// Parent class
						// Dependency update
						for (String f : knowledgeOb.getParentClassList()) {
							String key = getKnowledgeUnitDataKey(knowledgeUnitData,f, dpedendencyMemoization);
							if (key != null) {
								//projectJavaFileList.add(key);
								KUFileModel kuOb = knowledgeUnitData.get(key);
								for (String ku : kuOb.getKnowledgeUnitPerFile().keySet()) {
									if (!developerKnolwedgeProfileWithDependency.get(authorName).containsKey(ku)) {
										developerKnolwedgeProfileWithDependency.get(authorName).put(ku,
												new HashSet<String>());
									}
									
									developerKnolwedgeProfileWithDependency.get(authorName).get(ku).add(commit.getCommitId());

									if(!developerKnolwedgeProfileWithDependency.get(authorName).containsKey("COMMIT")) {
										developerKnolwedgeProfileWithDependency.get(authorName).put("COMMIT", new HashSet<String>());
							
									}
									developerKnolwedgeProfileWithDependency.get(authorName).get("COMMIT").add(commit.getCommitId());


								}
							}
						}
					}
				}
			}
			

			int changeIndex = getChangeKUFile(leftIndex, selectedGitCommits, commit);
			if (changeIndex != -1) {
				leftIndex = changeIndex;
				String fileTag = projectFullName + "-" + selectedGitCommits.get(leftIndex).getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
				dpedendencyMemoization = new HashMap<String,String>();
			}

		}
		System.out.println("[Finish creating dev ku profile]");
        /*
		String writingPath = devDepenPofileDir + projectFullName + devProfileDepSuffix + ".csv";
		writeDeveloperKUProfile(writingPath, developerKnolwedgeProfileWithDependency, projectFullName,
				projectJavaFileList, gitCommitList.size());
                */
		System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void createDevprofile(String projectFullName) {
        // <Dev, <KU, <File, Double>>>
	    Map<String, Map<String, Set<String>>> developerKnolwedgeProfile = new HashMap<String, Map<String, Set<String>>>();
	
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		Set<String> projectJavaFileList = new HashSet<String>();

		int leftIndex = 0;



		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
		Map<String, KUFileModel> knowledgeUnitData = null;

		System.out.println("[Start] Devloper KU Profile...");

		for (int i = 0; i < gitCommitList.size(); i++) {

			if (i % 500 == 0) {
				System.out.println("Done working with commits : " + (i + 1));
			}

			GitCommitModel commit = gitCommitList.get(i);

			if (knowledgeUnitData == null) {
				leftIndex = 0;
				String fileTag = projectFullName + "-" + selectedGitCommits.get(leftIndex).getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());

			}

			if (commit.getNoChangedFiles() > 0) {
				// update developer's profile
				String authorName = commit.getAuthorName();
				if (!developerKnolwedgeProfile.containsKey(authorName)) {
					developerKnolwedgeProfile.put(authorName, new HashMap<String,Set<String>>());
				}
				// projectJavaFileList.addAll(commit.getChangedJavaFileList());
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						for (String ku : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
							if (!developerKnolwedgeProfile.get(authorName).containsKey(ku)) {
								developerKnolwedgeProfile.get(authorName).put(ku, new HashSet<String>());
							}
							
							developerKnolwedgeProfile.get(authorName).get(ku).add(commit.getCommitId());


						}
					}
				}
				
				if(!developerKnolwedgeProfile.get(authorName).containsKey("COMMIT")) {
					developerKnolwedgeProfile.get(authorName).put("COMMIT", new HashSet<String>());
		
				}
				developerKnolwedgeProfile.get(authorName).get("COMMIT").add(commit.getCommitId());
			}

			int changeIndex = getChangeKUFile(leftIndex, selectedGitCommits, commit);
			
			if (changeIndex != -1) {
				leftIndex = changeIndex;
				String fileTag = projectFullName + "-" + selectedGitCommits.get(leftIndex).getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
			}

		}
		
		System.out.println("[Finish creating dev ku profile]");

        /*String writingPath = devProfileDir + projectFullName + devProfileSuffix + ".csv";
		writeDeveloperKUProfile(writingPath, developerKnolwedgeProfile, projectFullName, projectJavaFileList, gitCommitList.size());
		*/

        System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void writeFileMapToInteger(Map<String, Integer> fileMapToInteger, String projectName){
		try{
			String dir = "/scratch/ahsan/Java_Exam_Work/Result//scratch/ahsan/Java_Exam_Work/Result/Result_Nov_4_2021/DeepInvestigationDevActivity/DeepInvestigationDevActivity/";
			String fileLocation = dir + projectName + "_file_map_int.csv";
			CsvWriter writer = new CsvWriter(fileLocation);
			writer.write("File");
			writer.write("Index");
			writer.endRecord();

			for (String fileName : fileMapToInteger.keySet()){
				writer.write(fileName);
				writer.write(Integer.toString(fileMapToInteger.get(fileName)));
				writer.endRecord();
			}

		}catch (Exception e ){
			e.printStackTrace();
		}
	}
    

	public void writeDeveloperKUProfile(String path,
			Map<String, Map<String, Set<String>>> developerKnolwedgeProfile, String projectName,
			Set<String> projectJavaFileList, double totalCommits) {
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
				if(developerKnolwedgeProfile.get(devName).get("COMMIT").size() < 5) {
					continue;
				}
				writer.write(projectName);
				writer.write(devName);
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					if (developerKnolwedgeProfile.get(devName).containsKey(topic)) {
						double v = developerKnolwedgeProfile.get(devName).get(topic).size();
						v = v / totalCommits;
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

	public Map<String, Integer> createIndexingFileChanges(ArrayList<GitCommitModel> gitCommitList){
		Map<String, Integer> fileMapToInteger = new HashMap<String, Integer>();
		int fileIndex = 0;
		for (int i = 0; i < gitCommitList.size(); i++) {
			GitCommitModel commit = gitCommitList.get(i);
			for (String fileName : commit.getChangedJavaFileList()){
				if (!fileMapToInteger.containsKey(fileName)){
					fileMapToInteger.put(fileName, fileIndex);
					fileIndex = fileIndex + 1;
				}
			}
		}
		return fileMapToInteger;
	}

	public void generateDeveloperProfileSelectedProjectsWithotDependency(){
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList){
			if (!project.contains("elastic")){
				continue;
			}
			System.out.println("Starts working project [ " + project + " ]");
			this.createDevprofile(project);
			/*String path = ConstantUtil.COMMIT_HISTORY_DIR + project + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
			ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
			System.out.println("Project [" + project +"] Selected Commits: [" +selectedGitCommits.size()+ "]");*/
		}
	}

	public void generateDeveloperProfileSelectedProjectsWithDependency(){
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList){
			if (!project.contains("elastic")){
				continue;
			}

			this.createDevprofileWithDependencyAnalysis(project);
		}
	}

	public static void main(String[] args) {
		DevProfileGeneratorCommitWise ob = new DevProfileGeneratorCommitWise();
		// apache_lucene
		// jruby_jruby
		// apache_hbase
		
		//ob.createDevprofile("caskdata_cdap");
		//ob.createDevprofileWithDependencyAnalysis("elastic_elasticsearch");

		//ob.generateDeveloperProfileSelectedProjectsWithotDependency();
		ob.generateDeveloperProfileSelectedProjectsWithDependency();

		System.out.println("Program finishes successfully.");
	}

}
