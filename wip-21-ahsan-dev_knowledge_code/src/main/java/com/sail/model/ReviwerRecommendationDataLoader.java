package com.sail.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvWriter;
import com.sail.github.model.GitCommitModel;
import com.sail.github.model.KUFileModel;
import com.sail.java.exam.dev.profile.KnowledgeUnitExtractorDevStudy;
import com.sail.replication.model.PullRequestModel;
import com.sail.replication.model.PullRequestReviewCommentModel;
import com.sail.replication.model.PullRequestReviewerModel;
import com.sail.replication.model.TrainTestDataModel;
import com.sail.util.ConstantUtil;
import com.sail.util.FileUtil;
import com.sail.util.GitResultParserUtil;

import org.joda.time.DateTime;

public class ReviwerRecommendationDataLoader {
    
    public String projectName = "";
    public String pullRequestPath = "";
    public String pullReviewPath = "";
    public String pullCommentPath = "";
    public String pullFileChangePath = "";

    Map<String,PullRequestModel> pullRequestList;
    Set<String> studiedPullRequestList = new HashSet<String>();

    Map<String,List<String>> prChangedFileList;
    Map<String,ArrayList<PullRequestReviewerModel>> prReviewerListFull;
    Map<String, List<String>> prOnlyReviewerInfo;
    Map<String,List<PullRequestReviewCommentModel>> prCommentList;

    ArrayList<String> pullRequestForReviewRecommendation = new ArrayList<String>();
    Map<String, List<String>> pullRequestReviewerMap = new HashMap<String, List<String>>();
    Map<String, Set<String>> reviewerToPullRequestMap = new HashMap<String, Set<String>>();

    // File, reviewername , pull Request, count
    Map<String,Map<String, Map<String,ArrayList<DateTime>>>> reviewerComments = new HashMap<String,Map<String, Map<String,ArrayList<DateTime>>>>();
    Set<String> candidateReviewerList = new HashSet<String>();
   
    Map<String, ArrayList<String>> developerChangedJavaFilesCommits = new HashMap<String, ArrayList<String>>();

    ArrayList<GitCommitModel> JavaFileChangeGitCommitList;
    ArrayList<GitCommitModel> gitCommitList;
    ArrayList<GitCommitModel> selectedGitCommits;
    Map<String,ArrayList<String>> authorCommitList = new HashMap<String, ArrayList<String>>();
    Map<String, GitCommitModel> gitCommitListMap;
    public String COMMIT_FILE_CHANGE_DIS_DATA = "/home/local/SAIL/ahsan/dev_knowledge/scratch_dev_ku_data/data/file_change_distribution.csv";
    //public String COMMIT_FILE_CHANGE_DIS_DATA = "/scratch/ahsan/Java_Exam_Work/dev_ku_data/data/file_change_distribution.csv";
    Map<String, DataStatisticsModel> projectFileDistribution = FileUtil.readCommitFileChangeDistribution(COMMIT_FILE_CHANGE_DIS_DATA);


    public Map<String, Map<String, Map<String, ArrayList<DateTime>>>> getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(Map<String, Map<String, Map<String, ArrayList<DateTime>>>> reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public Map<String, GitCommitModel> getGitCommitListMap() {
        return gitCommitListMap;
    }

    public void setGitCommitListMap(Map<String, GitCommitModel> gitCommitListMap) {
        this.gitCommitListMap = gitCommitListMap;
    }

    public void setJavaFileChangeGitCommitList(ArrayList<GitCommitModel> javaFileChangeGitCommitList) {
        JavaFileChangeGitCommitList = javaFileChangeGitCommitList;
    }

    public Map<String, ArrayList<String>> getAuthorCommitList() {
        return authorCommitList;
    }

    public void setAuthorCommitList(Map<String, ArrayList<String>> authorCommitList) {
        this.authorCommitList = authorCommitList;
    }

    public ArrayList<GitCommitModel> getJavaFileChangeGitCommitList() {
        return JavaFileChangeGitCommitList;
    }

    public void setJaavaFileChangeGitCommitList(ArrayList<GitCommitModel> javaFileChangeGitCommitList) {
        JavaFileChangeGitCommitList = javaFileChangeGitCommitList;
    }

    public Map<String, ArrayList<String>> getDeveloperChangedJavaFilesCommits() {
        return developerChangedJavaFilesCommits;
    }

    public void setDeveloperChangedJavaFilesCommits(Map<String, ArrayList<String>> developerChangedJavaFilesCommits) {
        this.developerChangedJavaFilesCommits = developerChangedJavaFilesCommits;
    }

    public Map<String, Set<String>> getReviewerToPullRequestMap() {
        return reviewerToPullRequestMap;
    }

    public void setReviewerToPullRequestMap(Map<String, Set<String>> reviewerToPullRequestMap) {
        this.reviewerToPullRequestMap = reviewerToPullRequestMap;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Set<String> getStudiedPullRequestList() {
        return studiedPullRequestList;
    }

    public void setStudiedPullRequestList(Set<String> studiedPullRequestList) {
        this.studiedPullRequestList = studiedPullRequestList;
    }

    public String getPullRequestPath() {
        return pullRequestPath;
    }

    public void setPullRequestPath(String pullRequestPath) {
        this.pullRequestPath = pullRequestPath;
    }

    public String getPullReviewPath() {
        return pullReviewPath;
    }

    public void setPullReviewPath(String pullReviewPath) {
        this.pullReviewPath = pullReviewPath;
    }

    public String getPullCommentPath() {
        return pullCommentPath;
    }

    public void setPullCommentPath(String pullCommentPath) {
        this.pullCommentPath = pullCommentPath;
    }

    public String getPullFileChangePath() {
        return pullFileChangePath;
    }

    public void setPullFileChangePath(String pullFileChangePath) {
        this.pullFileChangePath = pullFileChangePath;
    }

    public Map<String, PullRequestModel> getPullRequestList() {
        return pullRequestList;
    }

    public void setPullRequestList(Map<String, PullRequestModel> pullRequestList) {
        this.pullRequestList = pullRequestList;
    }

    public Map<String, List<String>> getPrChangedFileList() {
        return prChangedFileList;
    }

    public void setPrChangedFileList(Map<String, List<String>> prChangedFileList) {
        this.prChangedFileList = prChangedFileList;
    }

    public Map<String, ArrayList<PullRequestReviewerModel>> getPrReviewerListFull() {
        return prReviewerListFull;
    }

    public void setPrReviewerListFull(Map<String, ArrayList<PullRequestReviewerModel>> prReviewerListFull) {
        this.prReviewerListFull = prReviewerListFull;
    }

    public Map<String, List<String>> getPrOnlyReviewerInfo() {
        return prOnlyReviewerInfo;
    }

    public void setPrOnlyReviewerInfo(Map<String, List<String>> prOnlyReviewerInfo) {
        this.prOnlyReviewerInfo = prOnlyReviewerInfo;
    }

    public Map<String, List<PullRequestReviewCommentModel>> getPrCommentList() {
        return prCommentList;
    }

    public void setPrCommentList(Map<String, List<PullRequestReviewCommentModel>> prCommentList) {
        this.prCommentList = prCommentList;
    }

    public ArrayList<String> getPullRequestForReviewRecommendation() {
        return pullRequestForReviewRecommendation;
    }

    public void setPullRequestForReviewRecommendation(ArrayList<String> pullRequestForReviewRecommendation) {
        this.pullRequestForReviewRecommendation = pullRequestForReviewRecommendation;
    }

    public Map<String, List<String>> getPullRequestReviewerMap() {
        return pullRequestReviewerMap;
    }

    public void setPullRequestReviewerMap(Map<String, List<String>> pullRequestReviewerMap) {
        this.pullRequestReviewerMap = pullRequestReviewerMap;
    }

    public Set<String> getCandidateReviewerList() {
        return candidateReviewerList;
    }

    public void setCandidateReviewerList(Set<String> candidateReviewerList) {
        this.candidateReviewerList = candidateReviewerList;
    }

    public ReviwerRecommendationDataLoader( String projectName, String pullRequestPath, String pullReviewPath,
    String pullCommentPath, String pullFileChangPath){
        this.pullCommentPath = pullCommentPath;
        this.pullReviewPath = pullReviewPath;
        this.pullRequestPath = pullRequestPath;
        this.pullFileChangePath = pullFileChangPath;
        this.projectName = projectName;
        String commitDatapath = ConstantUtil.COMMIT_HISTORY_DIR + projectName + "_full_commit_data.csv";
        this.loadGitCommitData(this.projectName, commitDatapath);
        this.loadData();
        System.out.println("[Complete] Data Loader");
    }

    public void loadGitCommitData(String projectFullName, String path){
        this.gitCommitList = FileUtil.readCommitInformation(path);
        this.gitCommitListMap = FileUtil.readCommitInformationMap(path);
        ArrayList<GitCommitModel> selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(gitCommitList);
        ArrayList<GitCommitModel> commitChangesJavaFiles = new ArrayList<GitCommitModel>();
        for(GitCommitModel commit :  this.gitCommitList){
            if(commit.getAuthorName().compareTo("ywelsch") == 0){
                System.out.println("GOT AUTHOR: ywelsch" );
            }
            if(commit.getNoChangedFiles() > projectFileDistribution.get(projectFullName).getPercentile95()){
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
       this.selectedGitCommits = GitResultParserUtil.getSelectedMonthlyCommit(this.gitCommitList);
       this.selectedGitCommits.sort(new Comparator<GitCommitModel>() {
        @Override
        public int compare(GitCommitModel o1, GitCommitModel o2) {
            // TODO Auto-generated method stub
            return o1.getCommitJodaDate().compareTo(o2.getCommitJodaDate());
        }  
       });
    }

    // Only contains review submission and reviewer name
    public Map<String, List<String>> loadReviewDataInfo(String path, Set<String> studiedPullRequest){
        Map<String, List<String>> temp = new HashMap<String, List<String>> ();
        Map<String, List<String>> reviewerList = FileUtil.readOnlyPullRequestReviewerInfo(this.pullReviewPath);
        for (String key : studiedPullRequest){
            if(reviewerList.containsKey(key)){
                temp.put(key, reviewerList.get(key));
            }
        }
        return temp;
    }
    public Map<String,ArrayList<PullRequestReviewerModel>> loadReviewDataFull(String path,
    Set<String> studiedPullRequest){
        Map<String,ArrayList<PullRequestReviewerModel>> temp = new HashMap<String,ArrayList<PullRequestReviewerModel>> ();
        Map<String,ArrayList<PullRequestReviewerModel>> reviewerList = FileUtil.readPullRequestReviewerData(this.pullReviewPath);
        //System.out.println("Full REVIEWER SIZE: " + reviewerList.size());
        for(String key : studiedPullRequest){
            if(reviewerList.containsKey(key)){
                temp.put(key, reviewerList.get(key));
            }
        }
        return temp;
    }
    public Map<String,List<PullRequestReviewCommentModel>> loadCommentData(String path, Set<String> studiedPullRequest){
        Map<String,List<PullRequestReviewCommentModel>> temp = new HashMap<String,List<PullRequestReviewCommentModel>>();
        Map<String,List<PullRequestReviewCommentModel>> commentList = FileUtil.readPullReviewComment(this.pullCommentPath);
        for (String key : studiedPullRequest){
            if(commentList.containsKey(key)){
                temp.put(key, commentList.get(key));
            }
        }
        return temp;
    }
    
    public Map<String,List<String>> loadChangedFileData(String path, Set<String> studiedPullRequest){
        Map<String,List<String>> temp = new HashMap<String,List<String>>();
        Map<String,List<String>> changeFileList = FileUtil.readPullRequestChangedFiles(path);
        System.out.println("Changed File PR Initial: " + changeFileList.size());

        for(String key : studiedPullRequest){
            if(changeFileList.containsKey(key)){
                temp.put(key, changeFileList.get(key));
            }
        }

        return temp;
    }

    public TrainTestDataModel getTrainTestSplits(){
        TrainTestDataModel trainTestModel = new TrainTestDataModel();
        int totalPullRequest = pullRequestForReviewRecommendation.size();
        // Taking the 80% training and 20% testing chronologically
        int cutOffPoint =  (int)Math.ceil((totalPullRequest * 0.80));
        for (int i = 0 ; i < cutOffPoint ; i++){
            trainTestModel.trainintPullRequestList.add(pullRequestForReviewRecommendation.get(i));
        }
        for (int i = cutOffPoint ; i < totalPullRequest ; i ++ ){
            trainTestModel.testingPullRequestList.add(pullRequestForReviewRecommendation.get(i));
        }
        trainTestModel.setFullPullRequestList(pullRequestForReviewRecommendation);
        return trainTestModel;
    }
    
    public ArrayList<String> filterBasedOnKU(Set<String> firstFilterPRSelection, String projectName){
        ArrayList<String> finalSelectedPRList = new ArrayList<String>();
        KnowledgeUnitExtractorDevStudy knowledgeUnitExtractor = new KnowledgeUnitExtractorDevStudy();
        Map<String, Map<String, KUFileModel>> savedKnowledgeUnits = new HashMap<String, Map<String, KUFileModel>>();
        for(String prNumber : firstFilterPRSelection){
            PullRequestModel pullRequest = this.getPullRequestList().get(prNumber);
            //System.out.println("Taarget: " + pullRequest.getPrCreatedJodaTime() + " Select Commit: " + this.selectedGitCommits.get(this.selectedGitCommits.size() - 1).getCommitJodaDate());

            GitCommitModel gitCommit = GitResultParserUtil.getClosestCommitGivenDate(pullRequest.getPrCreatedJodaTime(), this.selectedGitCommits);
            // take review only we have commit information
            if(gitCommit == null) continue;
            List<String> changedFileList = this.getPrChangedFileList().get(prNumber);
            String fileTag = projectName + "-" + gitCommit.getCommitId() + ".csv";
			String fileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectName + "/" + fileTag;
		    Map<String, KUFileModel> knowledgeUnitData = null;
            if(!savedKnowledgeUnits.containsKey(fileLocation)){
                knowledgeUnitData = knowledgeUnitExtractor.extractKnowledgeUnits(fileLocation, projectName);	
                savedKnowledgeUnits.put(fileLocation, knowledgeUnitData);
            }else{
                knowledgeUnitData = savedKnowledgeUnits.get(fileLocation);
            }
            List<String> missingMappingChangedFile = new ArrayList<String>();
            boolean flag = true;
            for(String fileName : changedFileList){
                fileName = fileName.substring(0, fileName.lastIndexOf(".java"));
                fileName = fileName.replace("/", ".").trim(); 
                if(!knowledgeUnitData.containsKey(fileName)){
                    flag = false;
                    break;
                }
            }
            if (flag){
                finalSelectedPRList.add(prNumber);
            }
        }
        return finalSelectedPRList;
    }

    public void loadData(){
        Map<String, ArrayList<String>> missingPRComments = new HashMap<String, ArrayList<String>>();
        Map<String, Boolean> reviewerNoComments = new HashMap<String, Boolean>();

        pullRequestList = FileUtil.readPullRequestData(this.pullRequestPath);
        Set<String> studiedPullRequestInitial = pullRequestList.keySet();
        Set<String> firstFilterPRSelection = new HashSet<String>();

        prChangedFileList = loadChangedFileData(this.pullFileChangePath, studiedPullRequestInitial);
        prReviewerListFull = loadReviewDataFull(this.pullReviewPath, studiedPullRequestInitial);
        prCommentList = loadCommentData(this.pullCommentPath, studiedPullRequestInitial);
        prOnlyReviewerInfo = loadReviewDataInfo(this.pullReviewPath, studiedPullRequestInitial);

        System.out.println("Chagned Files in PRs: " + prChangedFileList.size());
        System.out.println("Reviewer Full List: " + prReviewerListFull.size());
        System.out.println("Comment Full List: " + prCommentList.size());
        System.out.println("Review Data: " + prOnlyReviewerInfo.size());

        for(String prNumber : studiedPullRequestInitial){
            if(prChangedFileList.containsKey(prNumber) &&
            prReviewerListFull.containsKey(prNumber)){
                firstFilterPRSelection.add(prNumber);
            }
        }

       pullRequestForReviewRecommendation =  filterBasedOnKU(firstFilterPRSelection, this.projectName);


        /*for(String prNumber: pullRequestForReviewRecommendation){
            System.out.println(prNumber + " changed files [ " + prChangedFileList.get(prNumber).size() + " ]");
        }*/

        Set<String> closedPRs = new HashSet<String>();
        for(String prNumber : pullRequestForReviewRecommendation){
            List<String> reviewerList = prOnlyReviewerInfo.get(prNumber);
            if (!pullRequestReviewerMap.containsKey(prNumber)){
                pullRequestReviewerMap.put(prNumber, new ArrayList<String>());
            }
            for(String reviewString : reviewerList){
                String reviewerName = reviewString.split("!")[0].trim();
                candidateReviewerList.add(reviewerName);
                pullRequestReviewerMap.get(prNumber).add(reviewerName);
            }

            PullRequestModel prModel = getPullRequestList().get(prNumber);
            if(prModel.getPrClosedJodaTime() != null){
                closedPRs.add(prNumber);
            }
            
        }

        pullRequestForReviewRecommendation.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                DateTime d1 = pullRequestList.get(o1).getPrCreatedJodaTime();
                DateTime d2 = pullRequestList.get(o2).getPrCreatedJodaTime();
                return d1.compareTo(d2);
            }
        });

        for(String prNumber : pullRequestForReviewRecommendation){
            ArrayList<PullRequestReviewerModel> reviewInfoList = prReviewerListFull.get(prNumber);
            for(PullRequestReviewerModel revModel : reviewInfoList){
                if(!this.reviewerToPullRequestMap.containsKey(revModel.getReviewerName())){
                    this.reviewerToPullRequestMap.put(revModel.getReviewerName(), new HashSet<String>());
                }
                this.reviewerToPullRequestMap.get(revModel.getReviewerName()).add(prNumber);
            }
            if(!this.getPrCommentList().containsKey(prNumber)){
                this.getPrCommentList().put(prNumber, new ArrayList<PullRequestReviewCommentModel>());
            }
        }

        for(String prNumber : pullRequestForReviewRecommendation){
            PullRequestModel prModel = getPullRequestList().get(prNumber);
            ArrayList<PullRequestReviewerModel> reviewInfoList = prReviewerListFull.get(prNumber);
            List<String> changeFileList = getPrChangedFileList().get(prNumber);

            /*
            // Add reviewer as a commenter.
            for(PullRequestReviewerModel revModel : reviewInfoList){
                String reviewerName = revModel.getReviewerName();
                if(prModel.getPrClosedJodaTime() == null){
                    break;
                }

                for(String changeFile : changeFileList){
                    if(changeFile.compareTo("hbase-server/src/test/java/org/apache/hadoop/hbase/ipc/AbstractTestIPC.java") == 0){
                        System.out.println(changeFile + " " + reviewerName);
                    }

                   if(!this.reviewerComments.containsKey(changeFile)){
                       this.reviewerComments.put(changeFile, new HashMap<String, Map<String, ArrayList<DateTime>>>());
                   }
                   if(!this.reviewerComments.get(changeFile).containsKey(prNumber)){
                       this.reviewerComments.get(changeFile).put(prNumber, new HashMap<String, ArrayList<DateTime>>());
                   }
                   if(!this.reviewerComments.get(changeFile).get(prNumber).containsKey(reviewerName)){
                       this.reviewerComments.get(changeFile).get(prNumber).put(reviewerName, new ArrayList<DateTime>());
                   }
                   this.reviewerComments.get(changeFile).get(prNumber).get(reviewerName).add(prModel.getPrClosedJodaTime());

                }
            }*/

            List<PullRequestReviewCommentModel> revComModelList = getPrCommentList().get(prNumber);
            if(prNumber.compareTo("1589") == 0){
                System.out.println(prNumber + " Total Comments: " + revComModelList.size());
             }
            for(PullRequestReviewCommentModel revComModel : revComModelList){
                String commenterName = revComModel.getCommenterName();
                if(prNumber.compareTo("144") == 0){
                    System.out.println("PR Number: 144");
                }
                for(String changeFile : changeFileList){
                    if(!this.reviewerComments.containsKey(changeFile)){
                        this.reviewerComments.put(changeFile, new HashMap<String, Map<String, ArrayList<DateTime>>>());
                    }
                    if(!this.reviewerComments.get(changeFile).containsKey(prNumber)){
                        this.reviewerComments.get(changeFile).put(prNumber, new HashMap<String, ArrayList<DateTime>>());
                    }
                    if(!this.reviewerComments.get(changeFile).get(prNumber).containsKey(commenterName)){
                        this.reviewerComments.get(changeFile).get(prNumber).put(commenterName, new ArrayList<DateTime>());
                    }
                    this.reviewerComments.get(changeFile).get(prNumber).get(commenterName).add(revComModel.getCommentCreatedJodaTime());
                 }
            }

            /*if(prNumber.compareTo("144") == 0){
                System.out.println("FIND 144 Change File: " + this.getPrChangedFileList().get(prNumber).size());
                for(String changeFile : changeFileList){
                    System.out.println(changeFile);
                    for(String pr : this.reviewerComments.get(changeFile).keySet()){
                        if(pr.compareTo(prNumber) == 0){
                            System.out.println("Change File: " + changeFile + " commenter List: " + this.reviewerComments.get(changeFile).get(prNumber).size());
                            
                        }
                    }
                }
            }*/
    
        }

        for(String changeFile : this.reviewerComments.keySet()){
            for(String prNumber: this.reviewerComments.get(changeFile).keySet()){
                for(String commenterName : this.reviewerComments.get(changeFile).get(prNumber).keySet()){
                    this.reviewerComments.get(changeFile).get(prNumber).get(commenterName).sort(new Comparator<DateTime>() {
                        @Override
                        public int compare(DateTime o1, DateTime o2) {
                            return (o1.compareTo(o2));
                        }
                    });
                }
            }
        }
       
        for (String prNumber : pullRequestForReviewRecommendation) {
            List<PullRequestReviewCommentModel> revComModelList = getPrCommentList().get(prNumber);
            List<String> changeFileList = getPrChangedFileList().get(prNumber);
            boolean flag = false;
            for (String changeFile : changeFileList) {
                if(this.reviewerComments.containsKey(changeFile)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                if(!missingPRComments.containsKey(projectName)){
                    missingPRComments.put(projectName, new ArrayList<String>());
                }
                missingPRComments.get(projectName).add(prNumber);
            }
        }
        for(String changeFile : this.reviewerComments.keySet()){
            for(String prNumber: this.reviewerComments.get(changeFile).keySet()){
                boolean flag = false;
                if(!reviewerNoComments.containsKey(prNumber)){
                    reviewerNoComments.put(prNumber, false);
                }
                List<String> reviewers = getPullRequestReviewerMap().get(prNumber);
                for(String commenterName : this.reviewerComments.get(changeFile).get(prNumber).keySet()){
                    if(reviewers.contains(commenterName)){
                        reviewerNoComments.put(prNumber, true);
                    }
                }
            }
        }

        /*
        for(String prNumber : pullRequestForReviewRecommendation){
            System.out.println(prNumber + " " + pullRequestList.get(prNumber).getPrCreatedJodaTime());
        }*/

        System.out.println("First Filter PR: " + firstFilterPRSelection.size());
        System.out.println("Initial PR: " + studiedPullRequestInitial.size());
        System.out.println("Final PR: " + pullRequestForReviewRecommendation.size());
        System.out.println("Closed PR: " + closedPRs.size());


        /*System.out.println("Caandidate Reviwers Total: " + this.candidateReviewerList.size());
        System.out.println("PR Data Size: " + this.pullRequestList.size());
        System.out.println("PR Change File SIze: " + this.prChangedFileList.size());
        System.out.println("PR Comment Size: " + this.prCommentList.size());
        System.out.println("PR Reviewer Size: " + this.prReviewerListFull.size());
        System.out.println("PR Only Reviewer Size: " + this.prOnlyReviewerInfo.size());*/
        //writeMissingPRComments(missingPRComments, projectName);
        writeReviewerNotComments(reviewerNoComments, projectName);
    }

    public void writeReviewerNotComments(Map<String,Boolean> reviewerNoComments, String projectName){
        try{
            CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/reviewer_developement_history/" + projectName + "_reviewer_not_comments.csv");
            writer.write("Project_Name");
            writer.write("PR_Number_Reviewer_No_Comments");
            writer.endRecord();
            for(String prNumber: reviewerNoComments.keySet()){
                if(!reviewerNoComments.get(prNumber)){
                    writer.write(projectName);
                    writer.write(prNumber);
                    writer.endRecord();
                }
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeMissingPRComments(Map<String, ArrayList<String>> missingPRComments, String projectName){
        try{
            CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/reviewer_developement_history/" + projectName + "_missing_pr_comments.csv");
            writer.write("Project_Name");
            writer.write("Missing_Comments_PR_Number");
            writer.endRecord();
            for(String projName : missingPRComments.keySet()){
                for(String prNumber : missingPRComments.get(projName)){
                    writer.write(projectName);
                    writer.write(prNumber);
                    writer.endRecord();
                }
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   public static void main(String[] args) {
       List<String> projectList = Arrays.asList("apache_lucene", "apache_wicket", "apache_activemq", "jruby_jruby",
                "caskdata_cdap", "apache_hbase", "apache_hive", "apache_storm", "apache_groovy",
                "elastic_elasticsearch");
    //List<String> projectList = Arrays.asList("apache_hbase","apache_hive", "apache_storm", "elastic_elasticsearch");
    int totalPRs = 0;
    for(String projectName : projectList){
        System.out.println("Working ["+ projectName +"]");
        String prFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request/pr_reports_" + projectName +".csv";
        String prChagneFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_changed_files/pull_request_files_csv/" + projectName +"_files.csv";
        String prCommentFilePath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_comments/comments_csv_files/" + projectName +"_comments_with_discussion.csv";
        String prReviewerPath = "/scratch/ahsan/Java_Exam_Work/Result/pull_request_reviewer/reviewer_csv_files/" + projectName + "_review.csv";
        //ReviwerRecommendationDataLoader ob = new ReviwerRecommendationDataLoader(projectName, prFilePath, prReviewerPath, prCommentFilePath, prChagneFilePath);
        Map<String,PullRequestModel> prList = FileUtil.readPullRequestData(prFilePath);
        totalPRs = totalPRs + prList.size();
        System.out.println("["+projectName + "]" + " PR: ["+prList.size()+"] Total PRs: ["+totalPRs+"]" );  
    }
    System.out.println("Total PRs ["+totalPRs+"]");
   }
}
