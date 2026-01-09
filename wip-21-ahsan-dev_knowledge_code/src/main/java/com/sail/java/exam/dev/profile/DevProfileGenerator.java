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

public class DevProfileGenerator {

	public String devProfileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_August_14_2021/Dev_Profile/";
	public String devDepenPofileDir = "/scratch/ahsan/Java_Exam_Work/Result/Result_August_14_2021/Dev_Profile_Dependency/";
	public String devProfileSuffix = "_dev_profile";
	public String devProfileDepSuffix = "_dev_profile_dependency";

	// <Dev, <KU, <File, Double>>>
	Map<String, Map<String, Map<String, Double>>> developerKnolwedgeProfile = new HashMap<String, Map<String, Map<String, Double>>>();
	Map<String, Map<String, Map<String, Double>>> developerKnolwedgeProfileWithDependency = new HashMap<String, Map<String, Map<String, Double>>>();

	
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
		
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
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
				if (!developerKnolwedgeProfileWithDependency.containsKey(authorName)) {
					developerKnolwedgeProfileWithDependency.put(authorName, new HashMap<String, Map<String, Double>>());
				}
				// projectJavaFileList.addAll(commit.getChangedJavaFileList());
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						for (String ku : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
							if (!developerKnolwedgeProfileWithDependency.get(authorName).containsKey(ku)) {
								developerKnolwedgeProfileWithDependency.get(authorName).put(ku,
										new HashMap<String, Double>());
							}
							if (!developerKnolwedgeProfileWithDependency.get(authorName).get(ku)
									.containsKey(changeFile)) {
								developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(changeFile, 0.0);
							}
							developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(changeFile,
									developerKnolwedgeProfileWithDependency.get(authorName).get(ku).get(changeFile)
											+ 1);
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
												new HashMap<String, Double>());
									}
									if (!developerKnolwedgeProfileWithDependency.get(authorName).get(ku)
											.containsKey(key)) {
										developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(key,
												0.0);
									}
									developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(key,
											developerKnolwedgeProfileWithDependency.get(authorName).get(ku)
													.get(key) + 1);
									//System.out.println("Invoked Method Dependency.");
								}
							}
						}

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
												new HashMap<String, Double>());
									}
									if (!developerKnolwedgeProfileWithDependency.get(authorName).get(ku)
											.containsKey(key)) {
										developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(key,
												0.0);
									}
									developerKnolwedgeProfileWithDependency.get(authorName).get(ku).put(key,
											developerKnolwedgeProfileWithDependency.get(authorName).get(ku).get(key) + 1);
									//System.out.println("Parent CLass Dependency.");
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

		String writingPath = devDepenPofileDir + projectFullName + devProfileDepSuffix + ".csv";
		writeDeveloperKUProfile(writingPath, developerKnolwedgeProfileWithDependency, projectFullName,
				projectJavaFileList);
		System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void createDevprofile(String projectFullName) {
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		Set<String> projectJavaFileList = new HashSet<String>();

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
					developerKnolwedgeProfile.put(authorName, new HashMap<String, Map<String, Double>>());
				}
				// projectJavaFileList.addAll(commit.getChangedJavaFileList());
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						for (String ku : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
							if (!developerKnolwedgeProfile.get(authorName).containsKey(ku)) {
								developerKnolwedgeProfile.get(authorName).put(ku, new HashMap<String, Double>());
							}
							if (!developerKnolwedgeProfile.get(authorName).get(ku).containsKey(changeFile)) {
								developerKnolwedgeProfile.get(authorName).get(ku).put(changeFile, 0.0);
							}
							developerKnolwedgeProfile.get(authorName).get(ku).put(changeFile,
									developerKnolwedgeProfile.get(authorName).get(ku).get(changeFile) + 1);
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
			}

		}
		
		System.out.println("[Finish creating dev ku profile]");
		String writingPath = devProfileDir + projectFullName + devProfileSuffix + ".csv";
		writeDeveloperKUProfile(writingPath, developerKnolwedgeProfile, projectFullName, projectJavaFileList);
		System.out.println("Total Java Files: " + projectJavaFileList.size());

	}

	public void writeDeveloperKUProfile(String path,
			Map<String, Map<String, Map<String, Double>>> developerKnolwedgeProfile, String projectName,
			Set<String> projectJavaFileList) {
		try {
			CsvWriter writer = new CsvWriter(path);
			writer.write("Project_Name");
			writer.write("Developer_Name");
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
				writer.write(topic);
			}
			writer.endRecord();

			for (String devName : developerKnolwedgeProfile.keySet()) {
				writer.write(projectName);
				writer.write(devName);
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					if (developerKnolwedgeProfile.get(devName).containsKey(topic)) {
						double v = developerKnolwedgeProfile.get(devName).get(topic).size();
						v = v / projectJavaFileList.size();
						writer.write(Double.toString(v));
					} else {
						writer.write("0");
					}
				}
				writer.endRecord();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DevProfileGenerator ob = new DevProfileGenerator();
		// ob.createDevprofile("elastic_elasticsearch");
		ob.createDevprofileWithDependencyAnalysis("elastic_elasticsearch");
		System.out.println("Program finishes successfully.");
	}

}
