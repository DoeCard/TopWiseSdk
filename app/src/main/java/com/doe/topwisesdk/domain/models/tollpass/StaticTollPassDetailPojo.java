package com.doe.topwisesdk.domain.models.tollpass;

public class StaticTollPassDetailPojo {
    //From branch id(4),To branch id(4),Num trips(2),Expiry DateTime(4),Pass type(1),Limit periodicity(1)
    //Limits max count(2),limits remaining count(2),Limits start DateTime(4),Limits max count(2),
    // limits remaining count(2),limits start DateTime(4)
    private long fromBranchId;
    private long toBranchId;
    private int numOfTrips;
    private long expiryDate;

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    private int blockNumber;

    @Override
    public String toString() {
        return "TollPassDetailPojo{" +
                "fromBranchId=" + fromBranchId +
                ", toBranchId=" + toBranchId +
                ", numOfTrips=" + numOfTrips +
                ", expiryDate=" + expiryDate +
                ", passType=" + passType +
                ", limitPeriodicity=" + limitPeriodicity +
                ", limitMaxCount=" + limitMaxCount +
                ", limitRemainingCount=" + limitRemainingCount +
                ", limitStartDateTIme=" + limitStartDateTIme +
                ", limitMaxCountReturnJourney=" + limitMaxCountReturnJourney +
                ", limitRemainingCountReturnJourney=" + limitRemainingCountReturnJourney +
                ", limitStartDateTImeReturnJourney=" + limitStartDateTImeReturnJourney +
                ", blockNumber=" + blockNumber +
                '}';
    }

    public long getFromBranchId() {
        return fromBranchId;
    }

    public void setFromBranchId(long fromBranchId) {
        this.fromBranchId = fromBranchId;
    }

    public long getToBranchId() {
        return toBranchId;
    }

    public void setToBranchId(long toBranchId) {
        this.toBranchId = toBranchId;
    }

    public int getNumOfTrips() {
        return numOfTrips;
    }

    public void setNumOfTrips(int numOfTrips) {
        this.numOfTrips = numOfTrips;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getPassType() {
        return passType;
    }

    public void setPassType(int passType) {
        this.passType = passType;
    }

    public int getLimitPeriodicity() {
        return limitPeriodicity;
    }

    public void setLimitPeriodicity(int limitPeriodicity) {
        this.limitPeriodicity = limitPeriodicity;
    }

    public int getLimitMaxCount() {
        return limitMaxCount;
    }

    public void setLimitMaxCount(int limitMaxCount) {
        this.limitMaxCount = limitMaxCount;
    }

    public int getLimitRemainingCount() {
        return limitRemainingCount;
    }

    public void setLimitRemainingCount(int limitRemainingCount) {
        this.limitRemainingCount = limitRemainingCount;
    }

    public long getLimitStartDateTIme() {
        return limitStartDateTIme;
    }

    public void setLimitStartDateTIme(long limitStartDateTIme) {
        this.limitStartDateTIme = limitStartDateTIme;
    }

    public int getLimitMaxCountReturnJourney() {
        return limitMaxCountReturnJourney;
    }

    public void setLimitMaxCountReturnJourney(int limitMaxCountReturnJourney) {
        this.limitMaxCountReturnJourney = limitMaxCountReturnJourney;
    }

    public int getLimitRemainingCountReturnJourney() {
        return limitRemainingCountReturnJourney;
    }

    public void setLimitRemainingCountReturnJourney(int limitRemainingCountReturnJourney) {
        this.limitRemainingCountReturnJourney = limitRemainingCountReturnJourney;
    }

    public long getLimitStartDateTImeReturnJourney() {
        return limitStartDateTImeReturnJourney;
    }

    public void setLimitStartDateTImeReturnJourney(long limitStartDateTImeReturnJourney) {
        this.limitStartDateTImeReturnJourney = limitStartDateTImeReturnJourney;
    }

    private int passType;
    private int limitPeriodicity;

    private int limitMaxCount;
    private int limitRemainingCount;
    private long limitStartDateTIme;

    private int limitMaxCountReturnJourney;
    private int limitRemainingCountReturnJourney;
    private long limitStartDateTImeReturnJourney;
}
