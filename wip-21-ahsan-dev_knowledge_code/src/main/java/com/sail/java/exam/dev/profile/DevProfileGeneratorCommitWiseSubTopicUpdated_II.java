package com.sail.java.exam.dev.profile;

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

public class DevProfileGeneratorCommitWiseSubTopicUpdated_II {

	public String devProfileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile/";
	public String devDepenPofileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile_Dependency/";
	public String devProfileSuffix = "_commit_dev_profile_subtopic";
	public String devProfileDepSuffix = "_commit_dev_profile_dependency_subtopic";

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


	public void createDevprofile(String projectFullName, boolean isDependency, String writingPath) {

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
		knowledgeUnitData = ob.extractSubKnowledgeUnits(fileLocationFirst, projectFullName);

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
				knowledgeUnitData = ob.extractSubKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
				dpedendencyMemoization = new HashMap<String, String>();

				//System.out.println("Update: " + commit.getCommitAuthorDate());
			}

			// update developer's profile
			if (commit.getNoChangedFiles() > 0) {
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						updateDevKnowledgeUnitProfile(developerKnolwedgeProfile,knowledgeOb, authorName, commit.getCommitId());
						
						if (isDependency) {
							// Dependency update
							for (String f : knowledgeOb.getDependencyList()) {
								// System.out.println("F " + f + " " + knowledgeUnitData.containsKey(f));
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateDevKnowledgeUnitProfile(developerKnolwedgeProfile, kuOb,
											authorName, commit.getCommitId());
								}
							}
							// Parent class Dependency update
							for (String f : knowledgeOb.getParentClassList()) {
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateDevKnowledgeUnitProfile(developerKnolwedgeProfile, kuOb,
											authorName, commit.getCommitId());
								}
							}
						}

						// Total number of commit update KU
						if (!developerKnolwedgeProfile.get(authorName).containsKey("COMMIT")) {
							developerKnolwedgeProfile.get(authorName).put("COMMIT",
									new HashSet<String>());
						}
						developerKnolwedgeProfile.get(authorName).get("COMMIT").add(commit.getCommitId());

					}
				}
			}
		}
		System.out.println("[Finish creating dev ku profile]");

		//String writingPath = devDepenPofileDir + projectFullName + suffix + ".csv";
		writeDeveloperKUProfile(writingPath, developerKnolwedgeProfile, projectFullName,
				projectJavaFileList, gitCommitList.size());

		System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void writeDeveloperKUProfile(String path, Map<String, Map<String, Set<String>>> developerKnolwedgeProfile,
			String projectName, Set<String> projectJavaFileList, double totalCommits) {
		try {
			CsvWriter writer = new CsvWriter(path);
			writer.write("Project_Name");
			writer.write("Developer_Name");
			for (String topic : ConstantUtil.topicList) {
				//String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
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
				for (String topic : ConstantUtil.topicList) {
					//String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
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
			System.out.println("Starts working project [ " + project + " ]");
			String writingPath = devProfileDir + project + devProfileSuffix + ".csv";
			this.createDevprofile(project, false, writingPath);
		}
	}

	public void generateDeveloperProfileSelectedProjectsWithDependency() {
		
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList) {
			System.out.println("Starts working project [ " + project + " ]");
			String writingPath = devDepenPofileDir + project + devProfileDepSuffix + ".csv";
			this.createDevprofile(project, true, writingPath);
		}
	}

	public static void main(String[] args) {
		DevProfileGeneratorCommitWiseSubTopicUpdated_II ob = new DevProfileGeneratorCommitWiseSubTopicUpdated_II();
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
