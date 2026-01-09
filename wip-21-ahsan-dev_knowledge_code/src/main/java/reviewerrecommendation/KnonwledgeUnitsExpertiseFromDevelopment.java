package reviewerrecommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sail.github.model.GitCommitModel;
import com.sail.github.model.KUFileModel;
import com.sail.java.exam.dev.profile.KnowledgeUnitExtractorDevStudy;
import com.sail.model.DataStatisticsModel;
import com.sail.model.ReviwerRecommendationDataLoader;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

public class KnonwledgeUnitsExpertiseFromDevelopment {
    
    ReviwerRecommendationDataLoader dataModel;
    boolean isDependency = false;
    public String projectName = "";
    public String COMMIT_FILE_CHANGE_DIS_DATA = ConstantUtil.scratchDataLocBackup + "/file_change_distribution.csv";
    ArrayList<GitCommitModel> gitCommitList = null;
    ArrayList<GitCommitModel> selectedGitCommits = null;
    Map<String, GitCommitModel> gitCommitMapWithCommitId = new HashMap<String, GitCommitModel>();

    //<Developer, List of commits>
    Map<String, ArrayList<String>> devleoperCommitList = new HashMap<String, ArrayList<String>>();
    //<Commit, File, Knowledge Unit>
    Map<String, Map<String,Map<String,Double>>> fileKnowledgeUnitOccuranceMatrix = new HashMap<String, Map<String, Map<String,Double>>>();
    //<Developer, Commit, File, Knowledge Units>
    Map<String, Map<String, Map<String, Map<String,Double>>>> developerfileKnowledgeUnitMatrix = new HashMap<String, Map<String, Map<String, Map< String, Double>>>>();
	Map<String, DataStatisticsModel> projectFileDistribution = FileUtil.readCommitFileChangeDistribution(COMMIT_FILE_CHANGE_DIS_DATA);
    ArrayList<String> commitWithJavaFileChanges = new ArrayList<String>();

    

    
    public boolean isDependency() {
        return isDependency;
    }

    public void setDependency(boolean isDependency) {
        this.isDependency = isDependency;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCOMMIT_FILE_CHANGE_DIS_DATA() {
        return COMMIT_FILE_CHANGE_DIS_DATA;
    }

    public void setCOMMIT_FILE_CHANGE_DIS_DATA(String cOMMIT_FILE_CHANGE_DIS_DATA) {
        COMMIT_FILE_CHANGE_DIS_DATA = cOMMIT_FILE_CHANGE_DIS_DATA;
    }

    public ArrayList<GitCommitModel> getGitCommitList() {
        return gitCommitList;
    }

    public void setGitCommitList(ArrayList<GitCommitModel> gitCommitList) {
        this.gitCommitList = gitCommitList;
    }

    public ArrayList<GitCommitModel> getSelectedGitCommits() {
        return selectedGitCommits;
    }

    public void setSelectedGitCommits(ArrayList<GitCommitModel> selectedGitCommits) {
        this.selectedGitCommits = selectedGitCommits;
    }

    public Map<String, ArrayList<String>> getDevleoperCommitList() {
        return devleoperCommitList;
    }

    public void setDevleoperCommitList(Map<String, ArrayList<String>> devleoperCommitList) {
        this.devleoperCommitList = devleoperCommitList;
    }

    public Map<String, Map<String, Map<String, Double>>> getFileKnowledgeUnitOccuranceMatrix() {
        return fileKnowledgeUnitOccuranceMatrix;
    }

    public void setFileKnowledgeUnitOccuranceMatrix(
            Map<String, Map<String, Map<String, Double>>> fileKnowledgeUnitOccuranceMatrix) {
        this.fileKnowledgeUnitOccuranceMatrix = fileKnowledgeUnitOccuranceMatrix;
    }

    public Map<String, Map<String, Map<String, Map<String, Double>>>> getDeveloperfileKnowledgeUnitMatrix() {
        return developerfileKnowledgeUnitMatrix;
    }

    public void setDeveloperfileKnowledgeUnitMatrix(
            Map<String, Map<String, Map<String, Map<String, Double>>>> developerfileKnowledgeUnitMatrix) {
        this.developerfileKnowledgeUnitMatrix = developerfileKnowledgeUnitMatrix;
    }

    public Map<String, DataStatisticsModel> getProjectFileDistribution() {
        return projectFileDistribution;
    }

    public void setProjectFileDistribution(Map<String, DataStatisticsModel> projectFileDistribution) {
        this.projectFileDistribution = projectFileDistribution;
    }

    public ArrayList<String> getCommitWithJavaFileChanges() {
        return commitWithJavaFileChanges;
    }

    public void setCommitWithJavaFileChanges(ArrayList<String> commitWithJavaFileChanges) {
        this.commitWithJavaFileChanges = commitWithJavaFileChanges;
    }
    

    public Map<String, GitCommitModel> getGitCommitsMapWithCommit() {
        return gitCommitMapWithCommitId;
    }

    public void setGitCommitsMapWithCommit(Map<String, GitCommitModel> selectedGitCommitsMapWithCommit) {
        this.gitCommitMapWithCommitId = selectedGitCommitsMapWithCommit;
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
    
    public boolean isSelectedCommit(ArrayList<GitCommitModel> selectedGitCommits, GitCommitModel commit){
		boolean result = false;
		for(GitCommitModel selectCommit : selectedGitCommits){
			if (selectCommit.getCommitId().equals(commit.getCommitId())){
				return true;
			}
		}
		return result;
	}

    public KnonwledgeUnitsExpertiseFromDevelopment(String projectName, boolean isDependency,
    ReviwerRecommendationDataLoader dataModel){
        this.projectName = projectName;
        this.isDependency = isDependency;
        this.dataModel = dataModel;
    }

    public void updateFileKnowledgeUnitMatrix(Map<String,Map<String,Map<String,Double>>> fileKuMatrix, 
	String commitId, String fileName, String knowledgeUnit, double value){
		// create instance for the file
		if(!fileKuMatrix.containsKey(commitId)){
			fileKuMatrix.put(commitId, new HashMap<String, Map<String, Double>>());
		}
		// create instance for the file
		if(!fileKuMatrix.get(commitId).containsKey(fileName)){
			fileKuMatrix.get(commitId).put(fileName, new HashMap<String,Double>());
		}
		// update with the maximum value
		fileKuMatrix.get(commitId).get(fileName).put(knowledgeUnit, value);
	}

	public void updateDevKnoweldgeUnitMatrix(Map<String, Map<String, Map<String, Map<String,Double>>>> developerfileKnowledgeUnitMatrix,
		String developerName, String commitId, String fileName, String knowledgeUnit, double value){
			if(!developerfileKnowledgeUnitMatrix.containsKey(developerName)){
				developerfileKnowledgeUnitMatrix.put(developerName, new HashMap<String, Map<String, Map <String, Double>>>());
			}
			if(!developerfileKnowledgeUnitMatrix.get(developerName).containsKey(commitId)){
				developerfileKnowledgeUnitMatrix.get(developerName).put(commitId, new HashMap<String, Map<String,Double>>());
			}
			if(!developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).containsKey(fileName)){
				developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).put(fileName, new HashMap<String, Double>());
			}
			// update with the maximum value
			developerfileKnowledgeUnitMatrix.get(developerName).get(commitId).get(fileName).put(knowledgeUnit, value);
	}

    public void updateKnowledgeUnitCalculationMatrixInCommits(Map<String, Map<String, Map<String,Double>>> fileKuMatrix,
	Map<String, Map<String,Map<String, Map<String,Double>>>> developerfileKnowledgeUnitMatrix, KUFileModel knowledgeOb,
	String developerName, String commitId, String fileName){
		for(String knowledgeUnit : knowledgeOb.getKnowledgeUnitPerFile().keySet()){
			Double value = knowledgeOb.getKnowledgeUnitPerFile().get(knowledgeUnit);
			updateFileKnowledgeUnitMatrix(fileKuMatrix, commitId, fileName, knowledgeUnit, value);
			updateDevKnoweldgeUnitMatrix(developerfileKnowledgeUnitMatrix, developerName, commitId, fileName, knowledgeUnit, value);
		}
	}

    public void extractKnowledgeUnits(){

        long start = System.currentTimeMillis();
        String path = ConstantUtil.COMMIT_HISTORY_DIR + this.projectName + "_full_commit_data.csv";
		this.gitCommitList = this.dataModel.getGitCommitList();
        this.selectedGitCommits = this.dataModel.getSelectedGitCommits();
        for(GitCommitModel commitOb : gitCommitList){
            gitCommitMapWithCommitId.put(commitOb.getCommitId(), commitOb);
        }


        Set<String> projectJavaFileList = new HashSet<String>();
		Map<String, String> dpedendencyMemoization = new HashMap<String, String>();

		KnowledgeUnitExtractorDevStudy ob = new KnowledgeUnitExtractorDevStudy();
        Map<String, KUFileModel> knowledgeUnitData = null;
        // Get the first element
        int commitsWithJavaFileChange = 0;
        int countSkipCommit = 0;
		String fileTagFirst = this.projectName + "-" + selectedGitCommits.get(0).getCommitId() + ".csv";
		String fileLocationFirst = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + this.projectName + "/" + fileTagFirst;
		knowledgeUnitData = ob.extractKnowledgeUnits(fileLocationFirst, this.projectName);
		
        System.out.println("[Start] Devloper KU Profile...");

        for (int i = 0; i < gitCommitList.size(); i++) {
			if (i % 500 == 0) {
				System.out.println("Done working with commits : " + (i + 1));
			}
			GitCommitModel commit = gitCommitList.get(i);
			String authorName = commit.getAuthorName();

			if(isSelectedCommit(selectedGitCommits, commit)){
				String fileTag = this.projectName + "-" + commit.getCommitId() + ".csv";
				String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + this.projectName + "/" + fileTag;
				knowledgeUnitData = ob.extractKnowledgeUnits(fileLocation, this.projectName);
				projectJavaFileList.addAll(knowledgeUnitData.keySet());
				dpedendencyMemoization = new HashMap<String, String>();
				//System.out.println("Update: " + commit.getCommitAuthorDate());
			}

			if (commit.getNoChangedFiles() > 0) {
				commitsWithJavaFileChange ++ ;
			}

            // Many file changes so cross-link presents. Need to skip this commit from our analysis.
			if(commit.getNoChangedFiles() > projectFileDistribution.get(this.projectName).getPercentile95()){
				countSkipCommit = countSkipCommit + 1;
				continue;
			}

            // update developer's profile
			if (commit.getNoChangedFiles() > 0) {
                if(!devleoperCommitList.containsKey(authorName)){
                    devleoperCommitList.put(authorName, new ArrayList<String>());
                }
                devleoperCommitList.get(authorName).add(commit.getCommitId());
                commitWithJavaFileChanges.add(commit.getCommitId());
				boolean changeInKnolwedgeUnit = false;
				for (String changeFile : commit.getChangedJavaFileList()) {
                    if (knowledgeUnitData.containsKey(changeFile)) {
						KUFileModel knowledgeOb = knowledgeUnitData.get(changeFile);
						updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
						developerfileKnowledgeUnitMatrix, knowledgeOb, authorName, commit.getCommitId(), changeFile);
						changeInKnolwedgeUnit = true;
						if (isDependency) {
							// Dependency update
							for (String f : knowledgeOb.getDependencyList()) {
								// System.out.println("F " + f + " " + knowledgeUnitData.containsKey(f));
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
								updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, commit.getCommitId(), changeFile);
								}
							}
							// Parent class Dependency update
							for (String f : knowledgeOb.getParentClassList()) {
								String key = getKnowledgeUnitDataKey(knowledgeUnitData, f, dpedendencyMemoization);
								if (key != null) {
									KUFileModel kuOb = knowledgeUnitData.get(key);
									updateKnowledgeUnitCalculationMatrixInCommits(fileKnowledgeUnitOccuranceMatrix,
											developerfileKnowledgeUnitMatrix, kuOb, authorName, commit.getCommitId(), changeFile);
								}
							}
						}
					}
                }
            }
        }
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("Time taken Knowledge Unit Extraction [" + (elapsedTime/1000) +" seconds]");
        System.out.println("Total developers [" + developerfileKnowledgeUnitMatrix.size() +"]");
        /*for(String dev : developerfileKnowledgeUnitMatrix.keySet()){
            for(String commit: developerfileKnowledgeUnitMatrix.get(dev).keySet()){
                for(String fileName: developerfileKnowledgeUnitMatrix.get(dev).get(commit).keySet()){
                    for(String ku : developerfileKnowledgeUnitMatrix.get(dev).get(commit).get(fileName).keySet()){
                        System.out.println(dev + " " + commit + " " + fileName + " " + ku + " " + developerfileKnowledgeUnitMatrix.get(dev).get(commit).get(fileName).get(ku));
                    }
                }
            }
            break;
        }*/
    }

   

    public static void main(String[] args) {
        String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_apache_hbase.csv";
        String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/apache_hbase_files.csv";
        String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/apache_hbase_comments_with_discussion.csv";
        String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/apache_hbase_review.csv";
        String projectName = "apache_hbase";
        ReviwerRecommendationDataLoader dataModel = new ReviwerRecommendationDataLoader(projectName, prFilePath,
         prReviewerPath, prCommentFilePath,prChagneFilePath);
        KnonwledgeUnitsExpertiseFromDevelopment ob = new KnonwledgeUnitsExpertiseFromDevelopment("apache_hbase", false, dataModel);
        ob.extractKnowledgeUnits();
        System.out.println("Total Commits: " + ob.gitCommitList.size() + " Java File Change Commits: " + ob.commitWithJavaFileChanges.size());
        System.out.println("Program finishes successfully");
    }
}
