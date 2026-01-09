package com.sail.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.websocket.Decoder.Text;

import com.csvreader.CsvReader;
import com.sail.github.model.GitCommitModel;
import com.sail.model.DataStatisticsModel;
import com.sail.replication.model.ImportStatementModel;
import com.sail.replication.model.PRCommitModel;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.PullRequestReviewCommentModel;
import com.sail.replication.model.PullRequestReviewerModel;

public class FileUtil {
	public static String JAVA_FILE_EXTENSION = ".java";
	public static String GRADLE_BUILD_EXTENSION = ".gradle";
	public static String MAVEN_CONF_EXTENSION = "pom.xml";
	
	public static String LINE_SEPERATOR = System.getProperty("line.separator");

	public static List<String> getAllFilesWithExtension(String directory, String extension) {
		List<String> result = null;
		try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

			result = walk.map(x -> x.toString()).filter(f -> f.endsWith(extension)).collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	public static ArrayList<String> readJavaPackageList(String filePath) {
		ArrayList<String> javaPackageList = new ArrayList<String>();
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()) {
				String packageName = reader.get("Package_Name");
				javaPackageList.add(packageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish reading java package list: ["+ javaPackageList.size()+"] packages");
		return javaPackageList;
	}
	
	public static ArrayList<String> readAnalyzedProjectName(String filePath) {
		ArrayList<String> projectName = new ArrayList<String>();
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()) {
				String packageName = reader.get("repository");
				packageName = packageName.replace("/", "_");
				projectName.add(packageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish reading project list: [" + projectName.size() + "] projects");
		return projectName;
	}

	public static ArrayList<String> readAnalyzedXinProjectName(String filePath) {
		ArrayList<String> projectName = new ArrayList<String>();
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()){
				String htmlUrl = reader.get("html_url");
				String pName = htmlUrl.split("/")[3] + "-" + htmlUrl.split("/")[4];
				//if (pName.equals("android-platform_frameworks_base")){
				//	continue;
				//}
				projectName.add(pName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish reading project list: [" + projectName.size() + "] projects");
		return projectName;
	}
	
	public static ArrayList<String> getProjectByCommitOrder(String filePath){
		ArrayList<String> projectNameList = new ArrayList<String>();
		Map<String,Integer> projectCommits = new HashMap<String,Integer>();
		try {
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()) {
				String projectName = reader.get("repository");
				int branches = Integer.parseInt(reader.get("No_Branches"));
				int commits = Integer.parseInt(reader.get("No_Commits"));
				projectCommits.put(projectName, commits);
				projectNameList.add(projectName);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		projectNameList.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(projectCommits.get(o2) > projectCommits.get(o1)) {
					return -1;
				}
				else if (projectCommits.get(o2) < projectCommits.get(o1)) {
					return 1;
				}
				return 0;
			}
		});
		
		for(String proj : projectNameList) {
			System.out.println(proj +" " + projectCommits.get(proj));
		}
		
		return projectNameList;
	}
	
	public static String getCleanDate (String dateString) {
		String result = dateString;
		
		/*
		if (dateString.contains("+")) {
			return  dateString.substring(0, dateString.lastIndexOf("+")).trim();
			
		}
		
		if (dateString.contains("-")){
			return  dateString.substring(0, dateString.lastIndexOf("-")).trim();
		}*/
		
		return result;
	}
	
	public static ArrayList<GitCommitModel> readCommitInformation(String path) {
		ArrayList<GitCommitModel> gitCommitList = new ArrayList<GitCommitModel>();
		try {
			CsvReader reader = new CsvReader(path);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			Set<String> addedCommitIdList = new HashSet<String>();
			while(reader.readRecord()) {
				String projectName = reader.get("project_name");
				String commitId = reader.get("commit_id");
				String authorName = reader.get("commit_author_name");
				String authorEmail = reader.get("commit_author_email");
				String committerName = reader.get("commit_committer_name");
				String committerEmail = reader.get("commit_committer_email");
				String commitAuthorDate = getCleanDate(reader.get("commit_author_date"));
				String commitCommitterDate = getCleanDate(reader.get("commit_committer_date"));
				String isMergeCommit = reader.get("commit_is_merge");
				String commitMessage = reader.get("commit_msg");
				String hasJavaFiles = reader.get("has_java_file");
				String numModifiedJavaFiles = reader.get("no_modified_java_file");
				
				String changedFileList = reader.get("file_change_list");
				
				
				GitCommitModel ob = new GitCommitModel();
				ob.setCommitId(commitId);
				ob.setAuthorName(authorName);
				ob.setAuthorEmail(authorEmail);
				ob.setCommitterName(committerName);
				ob.setCommitterEmail(committerEmail);
				ob.setCommitAuthorDate(commitAuthorDate);
				ob.setCommitCommitterDate(commitCommitterDate);
				ob.setCommitMessage(commitMessage);
				ob.setNoChangedFiles(Integer.parseInt(numModifiedJavaFiles));
				ob.setChangedJavaFileList(TextUtil.convertStringToListPatch(changedFileList, TextUtil.COMMA_SEPARATOR));
				
				if(authorName.compareTo("ywelsch") == 0){
					System.out.println("**** FIND ywelsch Author");
				}
				
				if(addedCommitIdList.contains(ob.getCommitId())){
					if(ob.getNoChangedFiles() == 0) {
						continue;
					}
				}
				addedCommitIdList.add(ob.getCommitId());

				gitCommitList.add(ob);
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish reading commit history data " + gitCommitList.size());
		return gitCommitList;
	}

	public static Map<String,GitCommitModel> readCommitInformationMap(String path) {
		Map<String,GitCommitModel> gitCommitList = new HashMap<String, GitCommitModel>();
		try {
			CsvReader reader = new CsvReader(path);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			Set<String> addedCommitIdList = new HashSet<String>();
			while(reader.readRecord()) {
				String projectName = reader.get("project_name");
				String commitId = reader.get("commit_id");
				String authorName = reader.get("commit_author_name");
				String authorEmail = reader.get("commit_author_email");
				String committerName = reader.get("commit_committer_name");
				String committerEmail = reader.get("commit_committer_email");
				String commitAuthorDate = getCleanDate(reader.get("commit_author_date"));
				String commitCommitterDate = getCleanDate(reader.get("commit_committer_date"));
				String isMergeCommit = reader.get("commit_is_merge");
				String commitMessage = reader.get("commit_msg");
				String hasJavaFiles = reader.get("has_java_file");
				String numModifiedJavaFiles = reader.get("no_modified_java_file");
				
				String changedFileList = reader.get("file_change_list");
				
				
				GitCommitModel ob = new GitCommitModel();
				ob.setCommitId(commitId);
				ob.setAuthorName(authorName);
				ob.setAuthorEmail(authorEmail);
				ob.setCommitterName(committerName);
				ob.setCommitterEmail(committerEmail);
				ob.setCommitAuthorDate(commitAuthorDate);
				ob.setCommitCommitterDate(commitCommitterDate);
				ob.setCommitMessage(commitMessage);
				ob.setNoChangedFiles(Integer.parseInt(numModifiedJavaFiles));
				ob.setChangedJavaFileList(TextUtil.convertStringToListPatch(changedFileList, TextUtil.COMMA_SEPARATOR));
	
				
				if(addedCommitIdList.contains(ob.getCommitId())){
					if(ob.getNoChangedFiles() == 0) {
						continue;
					}
				}
				addedCommitIdList.add(ob.getCommitId());

				gitCommitList.put(ob.getCommitId(), ob);
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish reading commit history data " + gitCommitList.size());
		return gitCommitList;
	}
	
	public static void main(String arg[]) {
		String path = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/apache_hbase_comments_with_discussion.csv";
		//ArrayList<GitCommitModel> commits = readCommitInformation(path);
		
		//Map<String,List<PullRequestReviewCommentModel>> prCommentList = readPullReviewComment(path);
		//System.out.println("Pull 1591 total comments: " + prCommentList.get("1591").size());
		
		List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_groovy",
                "elastic_elasticsearch");

		for(String project : projectList){
			String pathNew = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/clean_result_studied_project/commit_merge_data/" + project + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitListNew = FileUtil.readCommitInformation(pathNew);
			ArrayList<GitCommitModel> selectedNewListNew = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitListNew);

			String pathOld = ConstantUtil.ROOT + "/Data/commit_merge_data/" + project + "_full_commit_data.csv";
			ArrayList<GitCommitModel> gitCommitListOld = FileUtil.readCommitInformation(pathOld);
			ArrayList<GitCommitModel> selectedListOld = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitListOld);

			Set<String> commitIdNew = new HashSet<String>();
			Set<String> commitIdOld = new HashSet<String>();

			for(GitCommitModel commit : selectedNewListNew){
				commitIdNew.add(commit.getCommitId());
			}
			for(GitCommitModel commit : selectedListOld){
				commitIdOld.add(commit.getCommitId());
			}

			Set<String> commonList = new HashSet<String>();
			Set<String> unCommonList = new HashSet<String>();
			Set<String> missingList = new HashSet<String>();
			Set<String> selectedCommitIdList = new HashSet<String>();

			for(String commitId : commitIdOld){
				if(commitIdNew.contains(commitId)){
					commonList.add(commitId);
				}else{
					missingList.add(commitId);
				}
			}

			for (String commit : commitIdNew) {
				if (!commitIdOld.contains(commit)) {
					selectedCommitIdList.add(commit);
				}
			}
	
			ArrayList<GitCommitModel> finalSelectedCommitList = new ArrayList<GitCommitModel>();
			for(GitCommitModel commit : selectedNewListNew){
				if(selectedCommitIdList.contains(commit.getCommitId())){
					finalSelectedCommitList.add(commit);
				}
			}

			/*for(String commitIdd : commitIdNew){

			}*/

			System.out.println("Project: " + project);
			System.out.println("Total Old: " + commitIdOld.size() + " Total New: " + commitIdNew.size());
			System.out.println("Common: " + commonList.size() + " Missing: " + missingList.size() + "KU new " + finalSelectedCommitList.size());
			System.out.println("===========================================");

		}

	
		
		System.out.println("Program finishes successfully");
	}
	
	public static Map<String, DataStatisticsModel> readCommitFileChangeDistribution(String path){
		Map<String, DataStatisticsModel> projectDataStats = new HashMap<String, DataStatisticsModel>();

		try{
			CsvReader reader = new CsvReader(path);
			reader.readHeaders();
			while(reader.readRecord()){
				DataStatisticsModel dataModel = new DataStatisticsModel();
				String projectName = reader.get("Project");
				Double meanValue = Double.parseDouble(reader.get("Mean"));
				Double minValue = Double.parseDouble(reader.get("Min"));
				Double percentile25Value = Double.parseDouble(reader.get("25_Percentile"));
				Double percentile50Value = Double.parseDouble(reader.get("Median"));
				Double percentile75Value = Double.parseDouble(reader.get("75_Percentile"));
				Double percentile90Value = Double.parseDouble(reader.get("90_Percentile"));
				Double percentile95Value = Double.parseDouble(reader.get("95th_Percentile"));
				Double maxValue = Double.parseDouble(reader.get("Max"));

				dataModel.setMax(maxValue);
				dataModel.setMean(meanValue);
				dataModel.setMin(minValue);
				dataModel.setPercentile25(percentile25Value);
				dataModel.setPercentile50(percentile50Value);
				dataModel.setPercentile75(percentile75Value);
				dataModel.setPercentile90(percentile90Value);
				dataModel.setPercentile95(percentile95Value);

				projectDataStats.put(projectName, dataModel);

			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return projectDataStats;
	}

	/*
	Reviewer Recommendation Data Files Reading
	*/

	public static Map<String,PullRequestModel> readPullRequestData(String filePath){
		Map<String,PullRequestModel> pullRequestList = new HashMap<String, PullRequestModel>();
		try{
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("Proj_Name");
				String gitRepoName = reader.get("Git_Repo_Name");
				String gitProjName = reader.get("Git_Proj_Name");
				String prNumber = reader.get("PR_Number");

				
				//System.out.println("PR NUMBER: " + prNumber);

				String prId = reader.get("PR_Id");
				String prUrl = reader.get("PR_Url");
				String prHtmlUrl = reader.get("PR_Html_Url");
				String prTitle = reader.get("PR_Title");
				String prBody = reader.get("PR_Body");
				String prState = reader.get("PR_State");
				String prCreatedAt = reader.get("PR_Created_At");
				String prClosedAt = reader.get("PR_Closed_At");
				String prMergedAt = reader.get("PR_Merged_At");
				String prCreaterLoginName = reader.get("PR_Creator_Login");
				String prMergedCommitId = reader.get("PR_Merge_Commit_SHA");

				

				PullRequestModel prModel = new PullRequestModel();
				prModel.setProjectName(projectName);
				prModel.setGitRepoName(gitRepoName);
				prModel.setGitProjName(gitProjName);
				prModel.setPrUrl(prUrl);
				prModel.setPrNumber(prNumber);
				prModel.setPrState(prState);
				prModel.setPrTitle(prTitle);
				prModel.setPrBody(prBody);
				prModel.setPrCreatedAt(prCreatedAt);
				prModel.setPrClosedAt(prClosedAt);
				prModel.setPrMergedAt(prMergedAt);
				prModel.setPrCreaterGitLoginName(prCreaterLoginName);
				prModel.setPrMergeCommitId(prMergedCommitId);

				if (prModel.getPrCreatedJodaTime().isBefore(DateUtil.studyDate)){
					pullRequestList.put(prModel.getPrNumber(), prModel);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return pullRequestList;
	}

	public static Map<String,List<String>> readPullRequestChangedFiles(String filePath){
		Map<String,List<String>> changedFileList = new HashMap<String, List<String>>();
		Map<String,List<String>> filteredChangedFileList = new HashMap<String, List<String>>();
		try{
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()){
				String prNumber = reader.get("PR_Number");
				String changedFileName = reader.get("change_file_name");
				
				if(FileUtil.isJavaFile(changedFileName)){
					if(!changedFileList.containsKey(prNumber)){
						changedFileList.put(prNumber, new ArrayList<String>());
					}
					changedFileList.get(prNumber).add(changedFileName);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		for(String prNumber : changedFileList.keySet()){
			if(changedFileList.get(prNumber).size() < 30){
				filteredChangedFileList.put(prNumber, changedFileList.get(prNumber));
			}
		}

		return filteredChangedFileList;
	}

	public static Map<String,ArrayList<PullRequestReviewerModel>> readPullRequestReviewerData(String filePath){
		Map<String,ArrayList<PullRequestReviewerModel>> reviewerList = new HashMap<String,ArrayList<PullRequestReviewerModel>>();
		try{
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("Proj_Name");
				String gitRepoName = reader.get("Git_Repo_Name");
				String gitProjectName = reader.get("Git_Proj_Name");
				String prNumber = reader.get("PR_Number");
				String reviewerGitLoginName = reader.get("Reviewer_Login");
				String reviewSubmissionDate = reader.get("Review_Submission");
				String reviewerType = reader.get("Reviewer_Type");
				String reviewerName = reader.get("Reviewer_Name").trim();
				String reviewerUrl = reader.get("Reviewer_Url").trim();
				String reviewerLocatiion = reader.get("Reviewer_Location").trim();

				if(reviewerName.length() == 0){
					//System.out.println("GOT: " + reviewerName + " " + reviewerGitLoginName);
					reviewerName = reviewerGitLoginName;
				}

				PullRequestReviewerModel reviewModel = new PullRequestReviewerModel();
				reviewModel.setProjectName(projectName);
				reviewModel.setGitRepoName(gitRepoName);
				reviewModel.setGitProjectName(gitProjectName);
				reviewModel.setPrNumber(prNumber);
				reviewModel.setReviewerGitLoginName(reviewerGitLoginName);
				reviewModel.setReviewSubmissionDate(reviewSubmissionDate);
				reviewModel.setReviewerType(reviewerType);
				reviewModel.setReviewerName(reviewerName);
				reviewModel.setReviewerUrl(reviewerUrl);
				reviewModel.setReviewerLocatiion(reviewerLocatiion);
				
				if(!reviewerList.containsKey(prNumber)){
					reviewerList.put(prNumber, new ArrayList<PullRequestReviewerModel>());
				}
				reviewerList.get(prNumber).add(reviewModel);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return reviewerList;
	}

	public static Map<String, List<String>> readOnlyPullRequestReviewerInfo(String filePath){
		Map<String, List<String>> reviewersInfoList = new HashMap<String, List<String>>();
		Set<String> reviewFlag = new HashSet<String>();
		try{
			CsvReader reader = new CsvReader(filePath);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("Proj_Name");
				String gitRepoName = reader.get("Git_Repo_Name");
				String gitProjectName = reader.get("Git_Proj_Name");
				String prNumber = reader.get("PR_Number");
				String reviewerGitLoginName = reader.get("Reviewer_Login");
				String reviewSubmissionDate = reader.get("Review_Submission");
				String reviewerType = reader.get("Reviewer_Type");
				String reviewerName = reader.get("Reviewer_Name");
				if(reviewerName.trim().length() <= 0){
					reviewerName = reviewerGitLoginName;
				}

				String reviewKey = prNumber + "-" + reviewerName;

				if(!reviewFlag.contains(reviewKey)){
					if(!reviewersInfoList.containsKey(prNumber)){
						reviewersInfoList.put(prNumber, new ArrayList<String>());
					}
					reviewersInfoList.get(prNumber).add(reviewerName + "!" + reviewSubmissionDate);
				}
				reviewFlag.add(reviewKey);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return reviewersInfoList;
	}

	public static Map<String,List<PullRequestReviewCommentModel>> readPullReviewComment(String filePath){
		Map<String,List<PullRequestReviewCommentModel>> prCommentList = new HashMap<String,List<PullRequestReviewCommentModel>>();

		try{
			CsvReader reader = new CsvReader(filePath);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("Proj_Name");
				String gitRepoName = reader.get("Git_Repo_Name");
				String gitProjectName = reader.get("Git_Proj_Name");
				String prNumber = reader.get("PR_Number");

				String commentBody = reader.get("Comment_Body");
				String commentCreatedAt = reader.get("Comment_Created_At");
				String commentCreatedGitLoginName = reader.get("Commenter_Login");

				String commenterName = reader.get("Commenter_Name").trim();
				String commenterUrl = reader.get("Commenter_Url").trim();
				String commenterLocation = reader.get("Commenter_Location").trim();

				String discussionComment = reader.get("discussion_commennt");
				

				if(commenterName.length() == 0){
					commenterName = commentCreatedGitLoginName;
				}
				//System.out.println(prNumber + " " + commenterName);

				PullRequestReviewCommentModel ob = new PullRequestReviewCommentModel();
				ob.setProjectName(projectName);
				ob.setGitRepoName(gitRepoName);
				ob.setGitProjectName(gitProjectName);
				ob.setPrNumber(prNumber);
				ob.setCommentBody(commentBody);
				ob.setCommentCreatedAt(commentCreatedAt);
				ob.setCommentCreatorGitLoginName(commentCreatedGitLoginName);

				ob.setCommenterName(commenterName);
				ob.setCommenterUrl(commenterUrl);
				ob.setCommenterLocation(commenterLocation);

				if(discussionComment.compareTo("0") == 0){
					ob.setDiscussionComment(true);
				}else{
					ob.setDiscussionComment(false);
				}

				if(!prCommentList.containsKey(prNumber)){
					prCommentList.put(prNumber, new ArrayList<PullRequestReviewCommentModel>());
				}
				prCommentList.get(prNumber).add(ob);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return prCommentList;
	}

	public static boolean isJavaFile(String fileName){
		if(fileName.contains(".java")){
			return true;
		}
		return false;
	}
	
	public static void readKlaProjectGitCommit(String path){
		try{
			CsvReader reader = new CsvReader(path);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("Project_Name");
				projectName = projectName.split("/")[1];
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Map<String,ArrayList<GitCommitModel>> readCommitInformationMapPerProjectKLA (String path) {
		Map<String,ArrayList<GitCommitModel>> gitCommitList = new HashMap<String,ArrayList<GitCommitModel>>();
		try {
			CsvReader reader = new CsvReader(path);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			Set<String> addedCommitIdList = new HashSet<String>();
			
			while(reader.readRecord()) {
				String projectName = reader.get("Project_Name");
				projectName = projectName.replace("/", "_");
				String commitId = reader.get("Commit_Id");
				String commitDate = reader.get("Commit_Date");
				String releaseTagName = reader.get("Release");
				
				GitCommitModel ob = new GitCommitModel();
				ob.setCommitId(commitId);
				ob.setCommitAuthorDate(commitDate);
				ob.setReleaseTagName(releaseTagName);
				
				if(!gitCommitList.containsKey(projectName)){
					gitCommitList.put(projectName, new ArrayList<GitCommitModel>());
				}
				gitCommitList.get(projectName).add(ob);

				//System.out.println(ob.getCommitId());
				//System.out.println("No: " + numChangedFiles +" " + changedFileList);
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return gitCommitList;
	}

	public static ArrayList<GitCommitModel> readCommitInformationMapPerProjectKLAProject (String path) {
		ArrayList<GitCommitModel> gitCommitList = new ArrayList<GitCommitModel>();
		try {
			CsvReader reader = new CsvReader(path);
			reader.setSafetySwitch(false);
			reader.readHeaders();
			
			while(reader.readRecord()) {
				String projectName = reader.get("Project_Name");
				projectName = projectName.replace("/", "_");
				String commitId = reader.get("Commit_Id");
				String commitDate = reader.get("Commit_Date");
				String releaseTagName = reader.get("Release");
				
				GitCommitModel ob = new GitCommitModel();
				
				ob.setCommitId(commitId);
				ob.setCommitAuthorDate(commitDate);
				ob.setReleaseTagName(releaseTagName);
				ob.setProjectName(projectName);

				gitCommitList.add(ob);

			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return gitCommitList;
	}

	public static Map<String, ArrayList<PRCommitModel>> readCommitPRInfo(String path){
		Map<String, ArrayList<PRCommitModel>> commitPRList = new HashMap<String, ArrayList<PRCommitModel>>();
		try{
			CsvReader reader = new CsvReader(path);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("ProjectName");
				String prNumber = reader.get("PRId");
				String commitNumber = reader.get("CommitId");
				PRCommitModel prCommit = new PRCommitModel(projectName, prNumber, commitNumber);

				if(!commitPRList.containsKey(projectName)){
					commitPRList.put(projectName, new ArrayList<PRCommitModel>());
				}
				commitPRList.get(projectName).add(prCommit);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return commitPRList;
		
	}

	public static Map<String, Map<String,PRCommitModel>> readCommitPRInfoWithPRKey(String path){
		Map<String, Map<String,PRCommitModel>> commitPRList = new HashMap<String, Map<String,PRCommitModel>>();
		try{
			CsvReader reader = new CsvReader(path);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("ProjectName");
				String prNumber = reader.get("PRId");
				String commitNumber = reader.get("CommitId");
				PRCommitModel prCommit = new PRCommitModel(projectName, prNumber, commitNumber);

				if(!commitPRList.containsKey(projectName)){
					commitPRList.put(projectName, new HashMap<String,PRCommitModel>());
				}
				
				commitPRList.get(projectName).put(prNumber, prCommit);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return commitPRList;
		
	}

	public static Map<String, ArrayList<ImportStatementModel>> readImportStatementData(String path){
		Map<String, ArrayList<ImportStatementModel>> fileImportList = new HashMap<String, ArrayList<ImportStatementModel>>();
		try{
			CsvReader reader = new CsvReader(path);
			reader.readHeaders();
			while(reader.readRecord()){
				String projectName = reader.get("ProjectName");
				String prId = reader.get("PRId");
				String commitId = reader.get("CommitId");
				String fileName = reader.get("FileName").trim();
				//fileName = fileName.replace("/", ".");
				String importString = reader.get("ImportStatements");
				List<String> importList = TextUtil.convertStringToList(importString, "-");

				ImportStatementModel impModel = new ImportStatementModel();
				impModel.setProjectName(projectName);
				impModel.setPrId(prId);
				impModel.setCommitId(commitId);
				impModel.setFileName(fileName);
				impModel.setImportList(importList);
				impModel.setImportString(importString);

				if(!fileImportList.containsKey(commitId)){
					fileImportList.put(commitId, new ArrayList<ImportStatementModel>());
				}

				fileImportList.get(commitId).add(impModel);
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return fileImportList;
	}
}
