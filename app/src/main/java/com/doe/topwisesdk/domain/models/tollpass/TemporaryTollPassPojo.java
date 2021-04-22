package com.doe.topwisesdk.domain.models.tollpass;

public class TemporaryTollPassPojo {
    //Branch Id(4),In DateTime(4),Expiry DateTime(4)
    private long branchId;

    public long getBranchId() {
        return branchId;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    private int blockNumber;

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    private int transactionType;

    @Override
    public String toString() {
        return "TemporaryTollPassPojo{" +
                "branchId=" + branchId +
                ", inDateTime=" + inDateTime +
                ", expiryDateTime=" + expiryDateTime +
                ", blockNumber=" + blockNumber +
                '}';
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getInDateTime() {
        return inDateTime;
    }

    public void setInDateTime(long inDateTime) {
        this.inDateTime = inDateTime;
    }

    public long getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(long expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    private long inDateTime;
    private long expiryDateTime;
}
