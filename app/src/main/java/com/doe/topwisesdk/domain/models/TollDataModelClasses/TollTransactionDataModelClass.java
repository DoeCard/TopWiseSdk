package com.doe.topwisesdk.domain.models.TollDataModelClasses;

public class TollTransactionDataModelClass {

    private long branchId;
    private long inDateTime;
    private long expiryDateTime;

    public long getBranchId() {
        return branchId;
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
}
