package com.sail.model;

public class DataStatisticsModel {
    public double min;
    public double max;
    public double percentile25;
    public double percentile50;
    public double percentile75;
    public double percentile90;
    public double percentile95;
    public double mean;
    public double getMin() {
        return min;
    }
    public void setMin(double min) {
        this.min = min;
    }
    public double getMax() {
        return max;
    }
    public void setMax(double max) {
        this.max = max;
    }
    public double getPercentile25() {
        return percentile25;
    }
    public void setPercentile25(double percentile25) {
        this.percentile25 = percentile25;
    }
    public double getPercentile50() {
        return percentile50;
    }
    public void setPercentile50(double percentile50) {
        this.percentile50 = percentile50;
    }
    public double getPercentile75() {
        return percentile75;
    }
    public void setPercentile75(double percentile75) {
        this.percentile75 = percentile75;
    }
    public double getPercentile90() {
        return percentile90;
    }
    public void setPercentile90(double percentile90) {
        this.percentile90 = percentile90;
    }
    public double getPercentile95() {
        return percentile95;
    }
    public void setPercentile95(double percentile95) {
        this.percentile95 = percentile95;
    }
    public double getMean() {
        return mean;
    }
    public void setMean(double mean) {
        this.mean = mean;
    }

    public void printInfo(){
        System.out.println("-- Data Stats --");
        System.out.println("Mean: " + this.mean);
        System.out.println("Min: " + this.min);
        System.out.println("25 Percentile: " + this.percentile25);
        System.out.println("50 Percentile: " + this.percentile50);
        System.out.println("75 Percentile: " + this.percentile75);
        System.out.println("90 Percentile: " + this.percentile90);
        System.out.println("95 Percentile: " + this.percentile95);
        System.out.println("Max: " + this.max);

    }
}
