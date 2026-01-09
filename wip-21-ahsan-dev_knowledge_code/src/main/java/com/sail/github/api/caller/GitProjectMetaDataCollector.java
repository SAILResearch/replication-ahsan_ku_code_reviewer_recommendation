package com.sail.github.api.caller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.evaluatingevaluator.diff.GitDiff;
import com.sail.github.model.GitCommitModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

public class GitProjectMetaDataCollector implements Runnable{

	ArrayList<String> projectList;
	int startPos 	= -1;
	int endPos 		= -1;
	int threadNo 	=  0;
	
	GitDiff gitDiff = new GitDiff();

	List<String> commitColumnHeaders = Arrays.asList("Commit_Id", "Release_Tag", "Author_Name", "Author_Email", "Committer_Name",
			"Committer_Email", "Commit_Date", "Commit_Message", "No_Changed_Files", "Changed_Java_File_List",
			"Changed_Gradle_File_List", "Chagned_Pom_File_List");

	
	List<String> misstingFileList = Arrays.asList("FredJul_Flym",
		    "omnifaces_omnifaces",
		   "hsz_idea-gitignore",
		    "embulk_embulk",
		    "spring-projects_spring-restdocs",
		    "kuujo_copycat",
		    "Thaumic-Tinkerer_ThaumicTinkerer",
		    "QuantumBadger_RedReader",
		    "Realm_realm-java",
		    "mpcjanssen_simpletask-android",
		    "openhab_openhab2",
		    "seven332_EhViewer",
		    "elastic_elasticsearch",
		   "usc-isi-i2_Web-Karma",
		   "pac4j_pac4j",
		    "serenity-bdd_serenity-core",
		    "spring-cloud_spring-cloud-config",
		    "midonet_midonet",
		    "spotify_docker-maven-plugin",
		    "jphp-compiler_jphp",
		    "h2oai_h2o-3");
			
	
	public GitCommitModel getCommitInfo(String record) {
		GitCommitModel commitModel = new GitCommitModel();
		String columns[] = record.split(",");
		commitModel.setCommitId(columns[0].replace("'", "").trim());
		commitModel.setAuthorName(columns[1]);
		commitModel.setAuthorEmail(columns[2]);
		commitModel.setCommitterName(columns[3]);
		commitModel.setCommitterEmail(columns[4]);
		commitModel.setCommitCommitterDate(columns[5]);
		commitModel.setCommitMessage(columns[6]);
		return commitModel;
	}

	public List<String> getChangedJavaFiles(String changeFileString, String matchPattern) {
		List<String> fileChangeList = new ArrayList<String>();
		for (String s : changeFileString.split("\n")) {
			if (s.contains(matchPattern)) {
				fileChangeList.add(s);
			}
		}
		return fileChangeList;
	}

	public ArrayList<GitCommitModel> extractProjectInfo(String projectName) throws Exception {

		String projectPath = ConstantUtil.STUDIED_PROJECT_DIR + projectName + "/";

		ArrayList<GitCommitModel> commitList = new ArrayList<GitCommitModel>();

		String commandRevTags[] = { "git", "tag", "--sort=refname" };
		String resultCommandRevTag[] = ShellUtil.runCommand(projectPath, commandRevTags).split("\n");

		System.out.println("Project: " + projectName + " Total releases: " + resultCommandRevTag.length);

		int total = 0;
		for (String revTag : resultCommandRevTag) {
			String commandProjectInfo[] = { "git", "log", revTag, "-1", "--pretty='%H,%an,%ae,%cn,%ce,%cd,%s'",
					"--date=format:%Y-%m-%dT%H-%M-%S" };
			String projectInfoResult = ShellUtil.runCommand(projectPath, commandProjectInfo);
			// System.out.println("P: " + projectInfoResult);
			GitCommitModel commitModel = getCommitInfo(projectInfoResult);
			commitModel.setReleaseTagName(revTag);
			commitList.add(commitModel);
			//if(++total > 10) break;
			System.out.println("[" + projectName + "] Collecting commit info of Releases: " + (++total));
		}

		commitList.sort(new Comparator<GitCommitModel>() {
			@Override
			public int compare(GitCommitModel o1, GitCommitModel o2) {
				// TODO Auto-generated method stub
				return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
			}
		});		
		
		System.out.println("Project: " + projectName + " Total commits: " + commitList.size());
		for (int i = 1 ; i < commitList.size(); i++) {
			GitCommitModel commitModel = commitList.get(i);
			// more than one commit
			GitCommitModel prevCommit = commitList.get(i - 1);
			String diffCommand[] = { "git", "diff", "--name-only", prevCommit.getCommitId(),
					commitModel.getCommitId() };
			String changedFileResult = ShellUtil.runCommand(projectPath, diffCommand);
			List<String> changeJavaFileList = getChangedJavaFiles(changedFileResult, FileUtil.JAVA_FILE_EXTENSION);
			List<String> changeGradleFileList = getChangedJavaFiles(changedFileResult, FileUtil.GRADLE_BUILD_EXTENSION);
			List<String> changeMavenFileList = getChangedJavaFiles(changedFileResult, FileUtil.MAVEN_CONF_EXTENSION);
			commitModel.setChangedJavaFileList(changeJavaFileList);
			System.out.println("[" + projectName + "] identifying change information: " + (i));
		}
		System.out.println("Done: " + projectName);
		return commitList;
	}

	
	
	public ArrayList<GitCommitModel> extractProjectInfoWithSingleGitCommand(String projectName) throws Exception {

		String projectPath = ConstantUtil.STUDIED_PROJECT_DIR + projectName + "/";

		ArrayList<GitCommitModel> commitList = new ArrayList<GitCommitModel>();

		//git show  --pretty='%H,%an,%ae,%cn,%ce,%cd,%s' --date=format:%Y-%m-%dT%H-%M-%S --tags --no-patch
		
		String commandProjectCommitInfoForReleaseTags[] = { "git", "show","--pretty='[CommitResult]%H,%an,%ae,%cn,%ce,%cd,%s'",
		"--date=format:%Y-%m-%dT%H-%M-%S", "--tags","--no-patch","--date-order" };
		
		String resultCommandReleaseTagsInfo[] = ShellUtil.runCommand(projectPath, commandProjectCommitInfoForReleaseTags).split("\n");

		commitList = GitResultParserUtil.parseGitShowReleaseCommand(resultCommandReleaseTagsInfo, threadNo, projectName);
		
		System.out.println("[TH:"+this.threadNo+"]"+"Project: " + projectName + " Total releases: " + commitList.size());

		commitList.sort(new Comparator<GitCommitModel>() {
			@Override
			public int compare(GitCommitModel o1, GitCommitModel o2) {
				// TODO Auto-generated method stub
				return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
			}
		});
		
		ArrayList<GitCommitModel> filteredCommitList = new ArrayList<GitCommitModel>();
		
		for (int i = commitList.size() - 1 ; i >= 1 ; i--) {
			GitCommitModel commitModel = commitList.get(i);
			// more than one commit
			GitCommitModel prevCommit = commitList.get(i - 1);
			String diffCommand[] = { "git", "diff", "--name-only", prevCommit.getCommitId(),
					commitModel.getCommitId() };
			String changedFileResult = ShellUtil.runCommand(projectPath, diffCommand);
			List<String> changeJavaFileList = getChangedJavaFiles(changedFileResult, FileUtil.JAVA_FILE_EXTENSION);
			List<String> changeGradleFileList = getChangedJavaFiles(changedFileResult, FileUtil.GRADLE_BUILD_EXTENSION);
			List<String> changeMavenFileList = getChangedJavaFiles(changedFileResult, FileUtil.MAVEN_CONF_EXTENSION);
			commitModel.setChangedJavaFileList(changeJavaFileList);
			if(commitModel.getCommitJodaDate().isAfter(ConstantUtil.analysisStartDate)) {
				filteredCommitList.add(commitModel);
			}else {
				break;
			}
			System.out.println("[CommitDiff][TH:"+this.threadNo+"]"+"Project: " + projectName + "Identifying change information: " + (commitList.size() - i) + " Changed Java Files: " + changeJavaFileList.size());
		}
		
		filteredCommitList.sort(new Comparator<GitCommitModel>() {
			@Override
			public int compare(GitCommitModel o1, GitCommitModel o2) {
				// TODO Auto-generated method stub
				return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
			}
		});
		
		return filteredCommitList;
	}
	
	
	
	public void writeProjectCommitInfoToFile(String projectName, ArrayList<GitCommitModel> commitList,
			CsvWriter writer) {
		try {
			for (GitCommitModel commit : commitList) {
				writer.write(projectName);
				writer.write(commit.getCommitId());
				writer.write(commit.getReleaseTagName());
				writer.write(commit.getAuthorName());
				writer.write(commit.getAuthorEmail());
				writer.write(commit.getCommitterName());
				writer.write(commit.getCommitterEmail());
				writer.write(commit.getCommitCommitterDate());
				writer.write(commit.getCommitMessage());
				writer.write(Integer.toString(commit.getChangedJavaFileList().size()));
				writer.write(commit.getChangedJavaFileList().toString());
				writer.endRecord();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init(CsvWriter writer) {
		try {
			writer.write("Project_Name");
			for (int i = 0; i < commitColumnHeaders.size(); i++) {
				writer.write(commitColumnHeaders.get(i));
			}
			writer.endRecord();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testing() throws Exception{
		
		
		String projectName = "h2oai_h2o-3";
		String projectFullName = projectName.replace("/", "_");
		System.out.println("Analyzing project " + projectFullName);
		CsvWriter writer = new CsvWriter(ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv");
		init(writer);
		ArrayList<GitCommitModel> commitList = extractProjectInfoWithSingleGitCommand(projectFullName);
		
		for(GitCommitModel commit : commitList) {
			System.out.println(commit.getCommitCommitterDate());
		}
		writer.close();
	}
	
	
	
	
   
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = startPos ; i < endPos ; i ++ ) {
			String p = projectList.get(i);
			//System.out.println("Project: " + p);

			String projectFullName = p.replace("/", "_");
			try {
				CsvWriter writer = new CsvWriter(ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv");
				init(writer);
				ArrayList<GitCommitModel> commitList = extractProjectInfoWithSingleGitCommand(projectFullName);
				writeProjectCommitInfoToFile(p, commitList, writer);
				writer.close();
				System.out.println("[TH:"+this.threadNo+"]"+"Project: " + p + "[Done]");

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public GitProjectMetaDataCollector(int startPos, int endPos, int threadNo) {
		projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
	}
	
	public GitProjectMetaDataCollector() {
		// Default constructor
	}
	
	public static void main(String[] args) throws Exception {
		GitProjectMetaDataCollector ob = new GitProjectMetaDataCollector();
		//ob.extractCommitFromStudiedProjects();
		ob.testing();
		System.out.println("Program finishes successfully");
	}
}
