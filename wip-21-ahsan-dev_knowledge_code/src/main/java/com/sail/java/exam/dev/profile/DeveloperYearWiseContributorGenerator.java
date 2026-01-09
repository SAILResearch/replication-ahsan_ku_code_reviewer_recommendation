package com.sail.java.exam.dev.profile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.sail.model.DataStatisticsModel;
import com.sail.model.DeveloperKUProfile;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class DeveloperYearWiseContributorGenerator {

	public final int OVERTIME_YEARS = 6;

	public String devProfileDir = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/dev_profile_wihtout_dependency/";
	public String devDepenPofileDir = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/dev_profile_with_dependency/";

	// public String devProfileDir =
	// "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile/";
	// public String devDepenPofileDir =
	// "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Dev_Profile_Dependency/";
	public String devProfileSuffix = "_commit_dev_profile_instance_approach_percentage_snapshot_commit_skip";
	public String devProfileDepSuffix = "_commit_dev_profile_dependency_instance_approach_percentage_snapshot_commit_skip";
	public String COMMIT_STRING = "COMMIT";
	public String COMMIT_FILE_CHANGE_DIS_DATA = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/data/file_change_distribution.csv";

	// public String OVERTIME_ANALYSIS =
	// "/scratch/ahsan/Java_Exam_Work/Result/Result_Nov_18_2021/Overtime_KU_Profiles/";
	public String OVERTIME_ANALYSIS = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/overtime_ku_profiles/";
	public String YEAAR_WISE_CONTRIB = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/year_wise_contrib/";
    List<String> analyzedHistoryYearList = Arrays.asList("2016","2017","2018","2019","2020","2021", "2022");

	Map<String, DataStatisticsModel> projectFileDistribution = FileUtil
			.readCommitFileChangeDistribution(COMMIT_FILE_CHANGE_DIS_DATA);

	public Map<String, Integer> getDeveloperHistory(String projectName) {
		List<String> yearStringList = Arrays.asList("< 2016", "2017", "2018", "2019", "2020", "2021", "2022");
		Map<String, Integer> firstCommitAuthorMap = new HashMap<String, Integer>();
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		for (int i = 0; i < gitCommitList.size(); i++) {
			GitCommitModel commit = gitCommitList.get(i);
			String devName = commit.getAuthorName();
			int yearFirstCommitAuthor = commit.getCommitAuthorJodaDate().getYear();
			if (!firstCommitAuthorMap.containsKey(devName)) {
				firstCommitAuthorMap.put(devName, yearFirstCommitAuthor);
			}
			// System.out.println( "Commit: " + i + " " + commit.getCommitAuthorJodaDate());
		}
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
		return firstCommitAuthorMap;
	}

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

	public boolean isSelectedCommit(ArrayList<GitCommitModel> selectedGitCommits, GitCommitModel commit) {
		boolean result = false;
		for (GitCommitModel selectCommit : selectedGitCommits) {
			if (selectCommit.getCommitId().equals(commit.getCommitId())) {
				return true;
			}
		}
		return result;
	}

	public void updateDevKnowledgeUnitProfile(
			Map<String, Map<String, Set<String>>> profileMap,
			KUFileModel knowledgeOb,
			String authorName,
			String commitId) {

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

	public void updateFileKnowledgeUnitMatrix(Map<String, Map<String, Map<String, Double>>> fileKuMatrix,
			String commitId, String fileName, String knowledgeUnit, double value) {
		// create instance for the file
		if (!fileKuMatrix.containsKey(commitId)) {
			fileKuMatrix.put(commitId, new HashMap<String, Map<String, Double>>());
		}

		// create instance for the file
		if (!fileKuMatrix.get(commitId).containsKey(fileName)) {
			fileKuMatrix.get(commitId).put(fileName, new HashMap<String, Double>());
		}
		// update with the maximum value
		fileKuMatrix.get(commitId).get(fileName).put(knowledgeUnit, value);
	}

	public void updateDevKnoweldgeUnitMatrix(
			Map<String, Map<String, Map<String, Map<String, Double>>>> developerfileKnowledgeUnitMatrix,
			String developerName, String commitId, String fileName, String knowledgeUnit, double value) {
		if (!developerfileKnowledgeUnitMatrix.containsKey(developerName)) {
			developerfileKnowledgeUnitMatrix.put(developerName,
					new HashMap<String, Map<String, Map<String, Double>>>());
		}
		if (!developerfileKnowledgeUnitMatrix.get(developerName).containsKey(commitId)) {
			developerfileKnowledgeUnitMatrix.get(developerName).put(commitId,
					new HashMap<String, Map<String, Double>>());
		}
		if (!developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).containsKey(fileName)) {
			developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).put(fileName,
					new HashMap<String, Double>());
		}
		// update with the maximum value
		developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).get(fileName).put(knowledgeUnit, value);
	}

	public void updateKnowledgeUnitCalculationMatrixInCommits(
			Map<String, Map<String, Map<String, Double>>> fileKuMatrix,
			Map<String, Map<String, Map<String, Map<String, Double>>>> developerfileKnowledgeUnitMatrix,
			KUFileModel knowledgeOb,
			String developerName, String commitId, String fileName) {
		for (String knowledgeUnit : knowledgeOb.getKnowledgeUnitPerFile().keySet()) {
			Double value = knowledgeOb.getKnowledgeUnitPerFile().get(knowledgeUnit);
			updateFileKnowledgeUnitMatrix(fileKuMatrix, commitId, fileName, knowledgeUnit, value);
			updateDevKnoweldgeUnitMatrix(developerfileKnowledgeUnitMatrix, developerName, commitId, fileName,
					knowledgeUnit, value);
		}
	}

	public void createDevprofile(String projectFullName, boolean isDependency, String writingPath, boolean commitFilter,
			int cutOfDate, Map<String, Map<String, DeveloperKUProfile>> devDiffProfileHistory, String diffYearString) {

		Map<String, Map<String, Map<String, Double>>> fileKnowledgeUnitOccuranceMatrix = new HashMap<String, Map<String, Map<String, Double>>>();
		Map<String, Map<String, Map<String, Map<String, Double>>>> developerfileKnowledgeUnitMatrix = new HashMap<String, Map<String, Map<String, Map<String, Double>>>>();

		Map<String, Map<String, Set<String>>> developerKnolwedgeProfile = new HashMap<String, Map<String, Set<String>>>();
		String path = ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "_full_commit_data.csv";
		ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
		ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
		Set<String> projectJavaFileList = new HashSet<String>();
		Map<String, String> dpedendencyMemoization = new HashMap<String, String>();

		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
		Map<String, KUFileModel> knowledgeUnitData = null;
		int countSkipCommit = 0;
		int commitsWithJavaFileChange = 0;
		// Get the first element
		String fileTagFirst = projectFullName + "-" + selectedGitCommits.get(0).getCommitId() + ".csv";
		String fileLocationFirst = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTagFirst;
		knowledgeUnitData = ob.extractKnowledgeUnits(fileLocationFirst, projectFullName);

		System.out.println("[Start] Devloper KU Profile...");
		int totalCommitInThisYear = 0;

		for (int i = 0; i < gitCommitList.size(); i++) {

			GitCommitModel commit = gitCommitList.get(i);
			String authorName = commit.getAuthorName();

			if (commit.getCommitAuthorJodaDate().getYear() != cutOfDate) {
				continue;
			}

			++totalCommitInThisYear;

			if (isSelectedCommit(selectedGitCommits, commit)) {
				String fileTag = projectFullName + "-" + commit.getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectFullName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, projectFullName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
				dpedendencyMemoization = new HashMap<String, String>();
				// System.out.println("Update: " + commit.getCommitAuthorDate());
			}

			if (commitFilter) {
				// Many file changes so cross-link presents. Need to skip this commit from our
				// analysis.
				if (commit.getNoChangedFiles() > projectFileDistribution.get(projectFullName).getPercentile95()) {
					countSkipCommit = countSkipCommit + 1;
					continue;
				}
			}

			if (commit.getNoChangedFiles() > 0) {
				commitsWithJavaFileChange++;
			}

			// update developer's profile
			if (commit.getNoChangedFiles() > 0) {
				boolean changeInKnolwedgeUnit = false;
				for (String changeFile : commit.getChangedJavaFileList()) {
					if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
								developerfileKnowledgeUnitMatrix, knowledgeOb, authorName, commit.getCommitId(),
								changeFile);
						changeInKnolwedgeUnit = true;
						if (isDependency) {
							// Dependency update
							for (String f : knowledgeOb.getDependencyList()) {
								// System.out.println("F " + f + " " + knowledgeUnitData.containsKey(f));
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, commit.getCommitId(),
											changeFile);
								}
							}
							// Parent class Dependency update
							for (String f : knowledgeOb.getParentClassList()) {
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, commit.getCommitId(),
											changeFile);
								}
							}
						}
					}
				}
			}
		}
		System.out.println("[Finish creating dev ku profile]");

		// writeDeveloperKUProfile(writingPath, developerKnolwedgeProfile,
		// projectFullName,
		// projectJavaFileList, gitCommitList.size());

		Map<String, Integer> firstCommitAuthorMap = getDeveloperHistory(projectFullName);
		Map<String, Double> maxKnowledgeUnitMatrix = getMaxKnowledgeUnitValue(fileKnowledgeUnitOccuranceMatrix);
		for (String devName : developerfileKnowledgeUnitMatrix.keySet()) {
			int developerCommitCountChangeJavaFile = developerfileKnowledgeUnitMatrix.get(devName).keySet().size();
			Map<String, Double> maxDevKnowledgeUnitMatrix = getMaxKnowledgeUnitValue(
					developerfileKnowledgeUnitMatrix.get(devName));
			if (!devDiffProfileHistory.containsKey(devName)) {
				devDiffProfileHistory.put(devName, new HashMap<String, DeveloperKUProfile>());
			}
			DeveloperKUProfile devProf = new DeveloperKUProfile();
			devProf.setDeveloperName(devName);
			devProf.setCommitDiffs(developerCommitCountChangeJavaFile);
			devProf.setDiff(true);
			devProf.setDiffYearString(diffYearString);
			devProf.setYearExp(cutOfDate - firstCommitAuthorMap.get(devName) + 1);
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
				double v = maxDevKnowledgeUnitMatrix.get(topic);
				double normalizedValue = Math.max(1, maxKnowledgeUnitMatrix.get(topic));
				v = (v * 100) / normalizedValue;
				if (normalizedValue == 0) {
					devProf.getDevKUprofileMap().put(topic, 0.0);
				} else {
					devProf.getDevKUprofileMap().put(topic, v);
				}
			}
			devDiffProfileHistory.get(devName).put(Integer.toString(cutOfDate), devProf);
			devProf.setYearOfFirstContrib(firstCommitAuthorMap.get(devName));
		}

		//writeDeveloperKUProfileUpdated(writingPath, fileKnowledgeUnitOccuranceMatrix, developerfileKnowledgeUnitMatrix,
		//		projectFullName);

		System.out.println("Total Java Files: " + projectJavaFileList.size());
		System.out.println("No commits skip: [" + countSkipCommit + "] Total Commit with Java File changes ["
				+ commitsWithJavaFileChange + "]");
	}

	public void writeDevDiffHistory(Map<String, Map<String, DeveloperKUProfile>> devDiffProfileHistory,
			String projectName) {
		String dir = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/results/year_wise_contrib/";

		try {
			Map<String, CsvWriter> writerList = new HashMap<String, CsvWriter>();
			for (int i = 0; i < analyzedHistoryYearList.size(); i++) {
				String yearKey = analyzedHistoryYearList.get(i);
				String outPath = dir + projectName + "/" + projectName + "_dev_profile_diff_" + yearKey + ".csv";
				CsvWriter writer = new CsvWriter(outPath);
				writer.write("Project_Name");
				writer.write("Year_Diff_String");
				writer.write("DevName");
				writer.write("First_Contrib");
				writer.write("Commit_Diff");
				writer.write("Exp_Diff");
				for (int j = 0; j < ConstantUtil.majorTopicList.size(); j++) {
					writer.write(ConstantUtil.majorTopicList.get(j));
				}
				writer.endRecord();
				writerList.put(analyzedHistoryYearList.get(i), writer);
			}
			for (String devName : devDiffProfileHistory.keySet()) {
				for (String year : devDiffProfileHistory.get(devName).keySet()) {
					System.out.println("Year: " + year);
					DeveloperKUProfile devDiffProfile = devDiffProfileHistory.get(devName).get(year);
					CsvWriter writer = writerList.get(year);
					writer.write(projectName);
					writer.write(devDiffProfile.getDiffYearString());
					writer.write(devName);
					writer.write(Integer.toString(devDiffProfile.getYearOfFirstContrib()));
					writer.write(Integer.toString(devDiffProfile.getCommitDiffs()));
					writer.write(Integer.toString(devDiffProfile.getYearExp()));
					for (int i = 0; i < ConstantUtil.majorTopicList.size(); i++) {
						String topicName = ConstantUtil.majorTopicList.get(i);
						writer.write(Double.toString(devDiffProfile.getDevKUprofileMap().get(topicName)));
					}
					writer.endRecord();
				}
			}
			for (String yearKey : writerList.keySet()){
				writerList.get(yearKey).close();
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	public Map<String, Double> getMaxKnowledgeUnitValue(
			Map<String, Map<String, Map<String, Double>>> fileKnowledgeUnitOccuranceMatrix) {
		Map<String, Double> maxKnowledgeUnitMatrix = new HashMap<String, Double>();
		for (String topicId : ConstantUtil.topicCategoryList) {
			String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
			maxKnowledgeUnitMatrix.put(topic, 0.0);
		}
		for (String commitId : fileKnowledgeUnitOccuranceMatrix.keySet()) {
			for (String fileName : fileKnowledgeUnitOccuranceMatrix.get(commitId).keySet()) {
				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					double topicValue = 0.0;
					if (fileKnowledgeUnitOccuranceMatrix.get(commitId).get(fileName).containsKey(topic)) {
						topicValue = fileKnowledgeUnitOccuranceMatrix.get(commitId).get(fileName).get(topic);
					}
					maxKnowledgeUnitMatrix.put(topic, maxKnowledgeUnitMatrix.get(topic) + topicValue);
				}
			}
		}
		return maxKnowledgeUnitMatrix;
	}

	public void writeDeveloperKUProfileUpdated(String path,
			Map<String, Map<String, Map<String, Double>>> fileKnowledgeUnitOccuranceMatrix,
			Map<String, Map<String, Map<String, Map<String, Double>>>> developerfileKnowledgeUnitMatrix,
			String projectName) {
		Map<String, Double> maxKnowledgeUnitMatrix = getMaxKnowledgeUnitValue(fileKnowledgeUnitOccuranceMatrix);
		try {
			CsvWriter writer = new CsvWriter(path);
			writer.write("Project_Name");
			writer.write("Developer_Name");
			// writer.write("Normalized_Value");
			for (String topicId : ConstantUtil.topicCategoryList) {
				String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
				writer.write(topic);
			}
			writer.write("Total_Commits_Java_File");
			writer.endRecord();

			for (String devName : developerfileKnowledgeUnitMatrix.keySet()) {
				int developerCommitCountChangeJavaFile = developerfileKnowledgeUnitMatrix.get(devName).keySet().size();
				/*
				 * if (developerCommitCountChangeJavaFile < 5) {
				 * continue;
				 * }
				 */

				writer.write(projectName);
				writer.write(devName);

				Map<String, Double> maxDevKnowledgeUnitMatrix = getMaxKnowledgeUnitValue(
						developerfileKnowledgeUnitMatrix.get(devName));

				for (String topicId : ConstantUtil.topicCategoryList) {
					String topic = ConstantUtil.majorTopicList.get(ConstantUtil.topicCategoryList.indexOf(topicId));
					double v = maxDevKnowledgeUnitMatrix.get(topic);
					double normalizedValue = Math.max(1, maxKnowledgeUnitMatrix.get(topic));
					v = (v * 100) / normalizedValue;
					if (normalizedValue == 0) {
						writer.write("0");
					} else {
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

	public void generateOvertimeKUProfile() {
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		// List<String> projectList = Arrays.asList("apache_hbase");
		for (String project : projectList) {
			System.out.println("[PROJECT] " + project);
			String commitPpath = ConstantUtil.COMMIT_HISTORY_DIR + project + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(commitPpath);
			ArrayList<DateTime> fiveYearPointList = getHistoricalYears(gitCommitList, OVERTIME_YEARS);
			Map<String, Map<String, DeveloperKUProfile>>  devDiffProfileHistory = new HashMap<String, Map<String, DeveloperKUProfile>>();
			for (int i = 0; i < analyzedHistoryYearList.size(); i++) {
				int cutOffYear = Integer.parseInt(analyzedHistoryYearList.get(i));
				System.out.println("Up to Last Years: " + i + " " + cutOffYear);
				String outputDir = YEAAR_WISE_CONTRIB + project + "/";
				String writingPath = outputDir + project + "year_" + cutOffYear + ".csv";
				String diffYearString = analyzedHistoryYearList.get(i) + "-" + analyzedHistoryYearList.get(i);
				try {
					Path pathInfo = Paths.get(outputDir);
					if (!Files.isDirectory(pathInfo)) {
						String commandDirMaking[] = { "mkdir", outputDir };
						String outputCheckout = ShellUtil.runCommand(commandDirMaking);
						System.out.println("Directory is created. [" + outputDir + "]");
					} else {
						System.out.println("Directory is already created");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.createDevprofile(project, false, writingPath, true, cutOffYear, devDiffProfileHistory,diffYearString);
			}
			writeDevDiffHistory(devDiffProfileHistory, project);
		}
	}

	public ArrayList<DateTime> getHistoricalYears(ArrayList<GitCommitModel> gitCommitList, int years) {
		ArrayList<DateTime> fiveYearPointList = new ArrayList<DateTime>();

		DateTime lastCommitDate = gitCommitList.get(gitCommitList.size() - 1).getCommitJodaDate();

		fiveYearPointList.add(lastCommitDate);

		for (int k = 1; k < years; k++) {
			fiveYearPointList.add(lastCommitDate.minusYears(k));
		}
		return fiveYearPointList;
	}

	public void printCommitHistoryData() {
		ArrayList<String> projectList = FileUtil.getProjectByCommitOrder(ConstantUtil.STUDIED_PROJECT_COMMITS_FILE);
		for (String project : projectList) {
			String path = ConstantUtil.COMMIT_HISTORY_DIR + project + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(path);
			ArrayList<DateTime> fiveYearPointList = getHistoricalYears(gitCommitList, OVERTIME_YEARS);

			int diffYear = Years.yearsBetween(gitCommitList.get(gitCommitList.size() - 1).getCommitJodaDate(),
					gitCommitList.get(0).getCommitJodaDate()).getYears();
			System.out.println("Project: " + project + " Last Commit Date: "
					+ gitCommitList.get(0).getCommitCommitterDate() + " "
					+ gitCommitList.get(gitCommitList.size() - 1).getCommitCommitterDate() + " Diff Year: " + diffYear);

			for (int i = 0; i < fiveYearPointList.size(); i++) {
				System.out.println(fiveYearPointList.get(i));
			}
		}
	}

	public static void main(String[] args) {
		DeveloperYearWiseContributorGenerator ob = new DeveloperYearWiseContributorGenerator();
		ob.generateOvertimeKUProfile();
		System.out.println("Program finishes successfully.");
	}
}
