package com.sail.github.api.caller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.Days;

import com.csvreader.CsvWriter;
import com.sail.evaluatingevaluator.diff.GitDiff;
import com.sail.github.model.GitCommitModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;
import com.sail.util.ShellUtil;

public class GitProjectMetaDataCollectorCommitLevel implements Runnable {

	ArrayList<String> projectList;
	int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

	GitDiff gitDiff = new GitDiff();

	int DAYS_DIFF_COMMIT = 90;
	
	List<String> commitColumnHeaders = Arrays.asList("Commit_Id", "Author_Name", "Author_Email", "Committer_Name",
			"Committer_Email", "Commit_Date", "Commit_Message", "No_Changed_Files", "Changed_Java_File_List",
			"Changed_Gradle_File_List", "Chagned_Pom_File_List");

	List<String> misstingFileList = Arrays.asList("FredJul_Flym", "omnifaces_omnifaces", "hsz_idea-gitignore",
			"embulk_embulk", "spring-projects_spring-restdocs", "kuujo_copycat", "Thaumic-Tinkerer_ThaumicTinkerer",
			"QuantumBadger_RedReader", "Realm_realm-java", "mpcjanssen_simpletask-android", "openhab_openhab2",
			"seven332_EhViewer", "elastic_elasticsearch", "usc-isi-i2_Web-Karma", "pac4j_pac4j",
			"serenity-bdd_serenity-core", "spring-cloud_spring-cloud-config", "midonet_midonet",
			"spotify_docker-maven-plugin", "jphp-compiler_jphp", "h2oai_h2o-3");

	public List<String> getChangedJavaFiles(String changeFileString, String matchPattern) {
		List<String> fileChangeList = new ArrayList<String>();
		for (String s : changeFileString.split("\n")) {
			if (s.contains(matchPattern)) {
				fileChangeList.add(s);
			}
		}
		return fileChangeList;
	}

	public ArrayList<GitCommitModel> extractProjectInfoWithSingleGitCommand(String projectName) throws Exception {

		String projectPath = ConstantUtil.STUDIED_PROJECT_DIR + projectName + "/";
		ArrayList<GitCommitModel> commitList = new ArrayList<GitCommitModel>();
		String commandProjectInfo[] = { "git", "log", "--pretty='%H~%an~%ae~%cn~%ce~%cd~%s'",
				"--date=format:%Y-%m-%dT%H-%M-%S" };
		String resultCommandReleaseTagsInfo[] = ShellUtil.runCommand(projectPath, commandProjectInfo).split("\n");
		
		System.out.println("Finish Git Command");
		
		commitList = GitResultParserUtil.parseGitLogInfo(resultCommandReleaseTagsInfo, threadNo, projectName);
		
		commitList.sort(new Comparator<GitCommitModel>() {
			@Override
			public int compare(GitCommitModel o1, GitCommitModel o2) {
				// TODO Auto-generated method stub
				return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
			}
		});

		ArrayList<GitCommitModel> filteredCommitList = new ArrayList<GitCommitModel>();
		
		/*filteredCommitList.add(commitList.get(commitList.size() - 1));
		GitCommitModel previousCommit = commitList.get(commitList.size() - 1);

		for (int i = commitList.size() - 2; i >= 0; i--) {
			GitCommitModel presentCommit = commitList.get(i);
			int daysBetween = Days.daysBetween(presentCommit.getCommitJodaDate(), previousCommit.getCommitJodaDate())
					.getDays();
			if (daysBetween >= DAYS_DIFF_COMMIT) {
				filteredCommitList.add(presentCommit);
				previousCommit = presentCommit;
			}
		}*/
		
		filteredCommitList = commitList;
		
		filteredCommitList.sort(new Comparator<GitCommitModel>() {
			@Override
			public int compare(GitCommitModel o1, GitCommitModel o2) {
				// TODO Auto-generated method stub
				return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
			}
		});

		System.out.println(
				"[TH:" + this.threadNo + "]" + "Project: " + projectName + " Total releases: " + commitList.size() +" After filtering: " + filteredCommitList.size());
		for (int i = filteredCommitList.size() - 1; i >= 1; i--) {
			GitCommitModel commit = filteredCommitList.get(i);
			// more than one commit
			GitCommitModel prevCommit = filteredCommitList.get(i - 1);
			String diffCommand[] = { "git", "diff", "--name-only", prevCommit.getCommitId(),
					commit.getCommitId() };
			
			int days = Days.daysBetween(prevCommit.getCommitJodaDate(), commit.getCommitJodaDate()).getDays();
			
			//System.out.println(commit.getCommitId() + " " + prevCommit.getCommitId() +" Diff days: " + days);
			
			String changedFileResult = ShellUtil.runCommand(projectPath, diffCommand);
			List<String> changeJavaFileList = getChangedJavaFiles(changedFileResult, FileUtil.JAVA_FILE_EXTENSION);
			List<String> changeGradleFileList = getChangedJavaFiles(changedFileResult, FileUtil.GRADLE_BUILD_EXTENSION);
			List<String> changeMavenFileList = getChangedJavaFiles(changedFileResult, FileUtil.MAVEN_CONF_EXTENSION);

			commit.setChangedJavaFileList(changeJavaFileList);
			
			System.out.println("[CommitDiff][TH:" + this.threadNo + "]" + "Project: " + projectName
					+ "Identifying change information: " + (filteredCommitList.size() - i) + " Changed Java Files: "
					+ changeJavaFileList.size());
		}

		return filteredCommitList;
	}

	public void writeProjectCommitInfoToFile(String projectName, ArrayList<GitCommitModel> commitList,
			CsvWriter writer) {
		try {
			for (GitCommitModel commit : commitList) {
				writer.write(projectName);
				writer.write(commit.getCommitId());
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = startPos; i < endPos; i++) {
			String p = projectList.get(i);
			// System.out.println("Project: " + p);

			String projectFullName = p.replace("/", "_");
			try {
				CsvWriter writer = new CsvWriter(ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv");
				init(writer);
				ArrayList<GitCommitModel> commitList = extractProjectInfoWithSingleGitCommand(projectFullName);
				writeProjectCommitInfoToFile(p, commitList, writer);
				writer.close();
				System.out.println("[TH:" + this.threadNo + "]" + "Project: " + p + "[Done]");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testing() throws Exception {

		String projectName = "elastic_elasticsearch";
		String projectFullName = projectName.replace("/", "_");
		System.out.println("Analyzing project " + projectFullName);
		CsvWriter writer = new CsvWriter(ConstantUtil.COMMIT_HISTORY_DIR + projectFullName + "-Commits.csv");
		init(writer);
		long start = System.currentTimeMillis();
		
		ArrayList<GitCommitModel> commitList = extractProjectInfoWithSingleGitCommand(projectFullName);

		/*for (GitCommitModel commit : commitList) {
			System.out.println(commit.getCommitDate());
		}*/
		writeProjectCommitInfoToFile(projectName, commitList, writer);
		writer.close();
		// some time passes
		long end = System.currentTimeMillis();
		long elapsedTime = (end - start)/1000;
		System.out.println("Program executation time: " + elapsedTime);
	}

	public GitProjectMetaDataCollectorCommitLevel(int startPos, int endPos, int threadNo) {
		projectList = FileUtil.readAnalyzedProjectName(ConstantUtil.STUDIED_PROJECT_FILE);
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadNo = threadNo;
	}

	public GitProjectMetaDataCollectorCommitLevel() {
		// Default constructor
	}

	public static void main(String[] args) throws Exception {
		GitProjectMetaDataCollectorCommitLevel ob = new GitProjectMetaDataCollectorCommitLevel();
		// ob.extractCommitFromStudiedProjects();
		ob.testing();
		System.out.println("Program finishes successfully");
	}
}
