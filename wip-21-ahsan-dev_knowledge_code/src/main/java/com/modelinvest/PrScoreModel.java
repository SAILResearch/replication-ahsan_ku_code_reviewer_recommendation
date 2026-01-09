package com.modelinvest;

public class PrScoreModel {
    public String prNumber;
    public int rank1;
    public int rank2;
    public int rank3;
    public int rank4;
    public int rank5;

    public int totalPRs;

    public int rankOneToFive;

    public int getTotalPRs() {
        return totalPRs;
    }

    public void setTotalPRs(int totalPRs) {
        this.totalPRs = totalPRs;
    }

    public int getRankOneToFive(){
        return (rank1 + rank2 + rank3 + rank4 + rank5);
    }

    public String getPrNumber() {
        return prNumber;
    }
    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }
    public int getRank1() {
        return rank1;
    }
    public void setRank1(int rank1) {
        this.rank1 = rank1;
    }
    public int getRank2() {
        return rank2;
    }
    public void setRank2(int rank2) {
        this.rank2 = rank2;
    }
    public int getRank3() {
        return rank3;
    }
    public void setRank3(int rank3) {
        this.rank3 = rank3;
    }
    public int getRank4() {
        return rank4;
    }
    public void setRank4(int rank4) {
        this.rank4 = rank4;
    }
    public int getRank5() {
        return rank5;
    }
    public void setRank5(int rank5) {
        this.rank5 = rank5;
    }


    public void printPercentageResult(int totalTestCase){
        System.out.println(String.format("Rank 1 : %.3f", ((double)this.getRank1()/totalTestCase)));
        System.out.println(String.format("Rank 2 : %.3f", ((double)this.getRank2()/totalTestCase)));
        System.out.println(String.format("Rank 3 : %.3f", ((double)this.getRank3()/totalTestCase)));
        System.out.println(String.format("Rank 4 : %.3f", ((double)this.getRank4()/totalTestCase)));
        System.out.println(String.format("Rank 5 : %.3f", ((double)this.getRank5()/totalTestCase)));
        System.out.println("=====================================================");
    }
    
    public void printRawResult(int totalTestCase){
        System.out.println(String.format("Rank 1 : %d", this.getRank1()));
        System.out.println(String.format("Rank 2 : %d", this.getRank2()));
        System.out.println(String.format("Rank 3 : %d", this.getRank3()));
        System.out.println(String.format("Rank 4 : %d", this.getRank4()));
        System.out.println(String.format("Rank 5 : %d", this.getRank5()));
        System.out.println("=====================================================");
    }

}
