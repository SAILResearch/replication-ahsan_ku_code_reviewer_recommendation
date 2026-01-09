package com.sail.java.exam.work;

public class ChildKnowledgeUnitExtractor implements Runnable{
    int startPos = -1;
	int endPos = -1;
	int threadNo = 0;

    public ChildKnowledgeUnitExtractor(int startPos, int endPos, int threadNo){
        this.startPos = startPos;
        this.endPos = endPos;
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for (int i = startPos; i > endPos; i--) {
            JavaExamTopicExtractor ob = new JavaExamTopicExtractor();
		    
            /*String fileTag = projectRepoName + "-" + currentCommit.getCommitId() + ".csv";
		    String outputFileLocation = ConstantUtil.COMMIT_HISTORY_RESULT_LOC + "/" + projectRepoName + "/" + fileTag;
		    try {
			    ob.startReleaseLevelWithDependencyChangeAnalysis(commitId, projectRepoPath, projectRepoName, outputFileLocation);
		    } catch (Exception e) {
			    e.printStackTrace();
		    }*/
        }
        
    }
    
}
