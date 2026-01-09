package com.sail.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sail.github.model.GitCommitModel;

import org.joda.time.DateTime;
import org.joda.time.Months;

public class GitResultParserUtil {

	public static ArrayList<GitCommitModel> getSelectedMonthlyCommit(ArrayList<GitCommitModel> gitCommitList){
		ArrayList<GitCommitModel> selectedGitCommits  = new ArrayList<GitCommitModel>();
		selectedGitCommits.add(gitCommitList.get(0));
		GitCommitModel prevCommit = gitCommitList.get(0);
		int j = -1;
		for (int i = 1 ; i < gitCommitList.size() ; i ++) {
			GitCommitModel commit = gitCommitList.get(i);
			int month = Months.monthsBetween(prevCommit.getCommitJodaDate(), commit.getCommitJodaDate()).getMonths();
			//System.out.println("Diff: " + month + " " + prevCommit.getCommitCommitterDate() + " " + commit.getCommitCommitterDate());
			if (month >= 1) {
				selectedGitCommits.add(commit);
				prevCommit = commit;
				j = i;
			}
		}
		if (j < gitCommitList.size() - 1) {
			selectedGitCommits.add(gitCommitList.get(gitCommitList.size() - 1));
		}
		
		System.out.println("Total selected commits for KU: " + selectedGitCommits.size());
		return selectedGitCommits;
	}
	
	public static GitCommitModel parseCommitInfo(String record) {
		//System.out.println("Record: " + record);
		GitCommitModel commitModel = new GitCommitModel();
		String columns[] = record.split("~");
		commitModel.setCommitId(columns[0].replace("'","").trim());
		commitModel.setAuthorName(columns[1]);
		commitModel.setAuthorEmail(columns[2]);
		commitModel.setCommitterName(columns[3]);
		commitModel.setCommitterEmail(columns[4]);
		commitModel.setCommitCommitterDate(columns[5]);
		commitModel.setCommitMessage(columns[6]);
		//System.out.println("Length: " + columns.length);
		try{
			commitModel.getCommitJodaDate();
		}catch(Exception e){
			System.err.println("ERROR: " + " "+ commitModel.getCommitCommitterDate() + " " + columns.length + " " + record);
		}
		commitModel.getCommitJodaDate();
		return commitModel;
	}
	
	
	public static ArrayList<GitCommitModel> parseGitLogInfo(String result[], int threadNo, String projectName){
		ArrayList<GitCommitModel> gitCommitList = new ArrayList<GitCommitModel>();
		for(int i = 0 ; i < result.length ; i ++ ) {
			String record = result[i];
			try{
				GitCommitModel commit = parseCommitInfo(record);
				gitCommitList.add(commit);
			}catch(Exception e){
				
			}
		}
		
		return gitCommitList;
	}
	
	//An eample of git show result in the file Others/git_show_result.txt
	public static ArrayList<GitCommitModel> parseGitShowReleaseCommand(String result[], int threadNo, String projectName){
		ArrayList<GitCommitModel> gitCommitList = new ArrayList<GitCommitModel>();
		String releaseTagName = "";
		for(int i = 0 ; i < result.length ; i ++ ) {
			try{
				String line = result[i];
				line = line.trim();
				//System.out.println("L: " + line);
				if(line.startsWith("tag")) {
					releaseTagName = line.split(" ")[1];
				}
				else if (line.startsWith("'[CommitResult]")) {
						String commitInfoRecord = line.substring("'[CommitResult]".length());
						GitCommitModel gitCommit = parseCommitInfo(commitInfoRecord);
						gitCommit.getCommitJodaDate();
						gitCommit.setReleaseTagName(releaseTagName);
						gitCommitList.add(gitCommit);
				}
			}catch(Exception e){
				System.err.println("[Problem][GitParsing] " + threadNo + " " + projectName);
				//e.printStackTrace();
			}
		}
		return gitCommitList;
	}

	public static GitCommitModel getClosestCommitGivenDate(DateTime targetDate, 
    ArrayList<GitCommitModel> selectedCommitList){
        
        GitCommitModel identifiedCommit  = null;


        for(int i = 1 ; i < selectedCommitList.size(); i ++ ){
            GitCommitModel commitOb = selectedCommitList.get(i);
            if (targetDate.isBefore(commitOb.getCommitJodaDate())){
                return selectedCommitList.get(i);
            }
        }

        return identifiedCommit;
    }
}
