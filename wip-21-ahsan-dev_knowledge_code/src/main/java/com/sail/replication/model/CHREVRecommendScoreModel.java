package com.sail.replication.model;

import java.util.HashMap;
import java.util.Map;

public class CHREVRecommendScoreModel {
    
    Map<String,Map<String, Double>> commentContributionRatio = new HashMap<String, Map<String, Double>>();
        Map<String,Map<String, Double>> workDayContributionRatio = new HashMap<String, Map<String, Double>>();
        Map<String,Map<String, Double>> recentWorkDayContributionRatio = new HashMap<String, Map<String, Double>>();
        public Map<String, Map<String, Double>> getCommentContributionRatio() {
            return commentContributionRatio;
        }
        public void setCommentContributionRatio(Map<String, Map<String, Double>> commentContributionRatio) {
            this.commentContributionRatio = commentContributionRatio;
        }
        public Map<String, Map<String, Double>> getWorkDayContributionRatio() {
            return workDayContributionRatio;
        }
        public void setWorkDayContributionRatio(Map<String, Map<String, Double>> workDayContributionRatio) {
            this.workDayContributionRatio = workDayContributionRatio;
        }
        public Map<String, Map<String, Double>> getRecentWorkDayContributionRatio() {
            return recentWorkDayContributionRatio;
        }
        public void setRecentWorkDayContributionRatio(Map<String, Map<String, Double>> recentWorkDayContributionRatio) {
            this.recentWorkDayContributionRatio = recentWorkDayContributionRatio;
        }

        
}
