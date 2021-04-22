package com.doe.topwisesdk.domain.models.TollDataModelClasses;

public class TollDataModelClass {

    private PassesDataModelClass passesDataModelClasses;

    private TollTransactionDataModelClass tollTransactionDataModelClasses;

    public PassesDataModelClass getPassesDataModelClasses() {
        return passesDataModelClasses;
    }

    public void setPassesDataModelClasses(PassesDataModelClass passesDataModelClasses) {
        this.passesDataModelClasses = passesDataModelClasses;
    }

    public TollTransactionDataModelClass getTollTransactionDataModelClasses() {
        return tollTransactionDataModelClasses;
    }

    public void setTollTransactionDataModelClasses(TollTransactionDataModelClass tollTransactionDataModelClasses) {
        this.tollTransactionDataModelClasses = tollTransactionDataModelClasses;
    }
}
