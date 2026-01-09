package com.sail.collaborator;

import java.util.ArrayList;
import java.util.Comparator;

import com.sail.github.model.GitCommitModel;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class LoadGitCommitData extends DataModelInterface {
    
    public String projectName;
    public String commitPath;
    public DescriptiveStatistics changeFileDistribution;

    @Override
    public void loadData() {
        // TODO Auto-generated method stub
        calculateChangeFileDistribution();
        loadGitCommitData(this.projectName, this.commitPath);
    }

    public void calculateChangeFileDistribution(){
       ArrayList<GitCommitModel> gitCommitList = FileUtil.readCommitInformation(this.commitPath);
       this.changeFileDistribution = new DescriptiveStatistics();
       for(GitCommitModel commit : gitCommitList){
           this.changeFileDistribution.addValue(commit.getChangedJavaFileList().size());
       }
    }


    public void loadGitCommitData(String projectFullName, String path){
        this.gitCommitList = FileUtil.readCommitInformation(path);
        this.gitCommitListMap = FileUtil.readCommitInformationMap(path);
        this.selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);

        ArrayList<GitCommitModel> commitChangesJavaFiles = new ArrayList<GitCommitModel>();
        for(GitCommitModel commit :  this.gitCommitList){
            if(commit.getAuthorName().compareTo("ywelsch") == 0){
                System.out.println("GOT AUTHOR: ywelsch" );
            }
            if(commit.getNoChangedFiles() > changeFileDistribution.getPercentile(95)){
				continue;
			}
            if(commit.getChangedJavaFileList().size() > 0){
                commitChangesJavaFiles.add(commit);
                if(!this.developerChangedJavaFilesCommits.containsKey(commit.getAuthorName())){
                    this.developerChangedJavaFilesCommits.put(commit.getAuthorName(), new ArrayList<String>());
                }
                this.developerChangedJavaFilesCommits.get(commit.getAuthorName()).add(commit.getCommitId());
                if(!authorCommitList.containsKey(commit.getAuthorName())){
                    authorCommitList.put(commit.getAuthorName(), new ArrayList<String>());
                }
                authorCommitList.get(commit.getAuthorName()).add(commit.getCommitId());
            }
        }
       this.JavaFileChangeGitCommitList = commitChangesJavaFiles;
       this.selectedGitCommits.sort(new Comparator<GitCommitModel>() {
        @Override
        public int compare(GitCommitModel o1, GitCommitModel o2) {
            // TODO Auto-generated method stub
            return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
        }  
       });
    }

    public void printCommitHead(int size){
        //System.out.println("Commit Head Print");
        int condition = 0;
        if (size < 0 || size > this.gitCommitList.size()){
            condition = 10;
        }else{
            condition = size;
        }
        for(int i = 0 ; i < condition ; i ++){
            System.out.println(this.gitCommitList.get(i).getCommitId() + " " + this.gitCommitList.get(i).getCommitAuthorDate());
        }
    }

    public LoadGitCommitData(String projectName, String commitPath){
        this.projectName = projectName;
        this.commitPath = commitPath;
        loadData();
    }


    public static void main(String[] args) {

        String projectName = "alibaba-weex";
        String commitPath = "/home/local/SAIL/ahsan/BACKUP/ahsan_project_2022/xin_xia_paper_data/commit_merge_data/" + projectName + "_full_commit_data.csv";
        
        LoadGitCommitData ob = new LoadGitCommitData(projectName, commitPath);
        ob.loadData();
        ob.printCommitHead(10);
        
        System.out.println("Program finishes successfully.");
    }
}
