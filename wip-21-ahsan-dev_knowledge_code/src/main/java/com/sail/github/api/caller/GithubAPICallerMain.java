package com.sail.github.api.caller;

import java.util.HashSet;
import java.util.Set;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;

public class GithubAPICallerMain {

	String gitHubToken = "e9e07eac8877755807bae14dd65d08bca5a975e7";
	
	GitHub githubHandler = null;
	
	public void readFullJavaStudiedProject() {
		
	}
	
	public void gitHubConnection() throws Exception{
		GitHub github = new GitHubBuilder().withOAuthToken(gitHubToken).build();
		GHRepository repositoryHandler = github.getRepository("elastic/elasticsearch");
		
		int starCount = repositoryHandler.getStargazersCount();
		int forkCount = repositoryHandler.getForksCount();
		PagedIterable<GHCommit> commitIterator = repositoryHandler.listCommits();
		Set<String> commitIdList = new HashSet<String>();
		Set<String> committerNameList = new HashSet<String>();
		System.out.println("GitHubRateLimit: " + github.getRateLimit());
		for(GHCommit ghCommit : commitIterator) {
			commitIdList.add(ghCommit.getHtmlUrl().getContent().toString());
			System.out.println("Commit: " + ghCommit.getHtmlUrl().getContent().toString());
			committerNameList.add(ghCommit.getCommitter().getName());
		}
		System.out.println("Commit: " + commitIdList.size());
		System.out.println("GitHubRateLimit: " + github.getRateLimit());
		
		System.out.println("FullName: " + repositoryHandler.getFullName());
		System.out.println("Fork Count: " + repositoryHandler.getForksCount());
		System.out.println("Star: " + repositoryHandler.getStargazersCount());
	}
	
	public static void main(String[] args) throws Exception{
		GithubAPICallerMain ob = new GithubAPICallerMain();
		ob.gitHubConnection();
		System.out.println("Program finishes successfully");
	}
}
