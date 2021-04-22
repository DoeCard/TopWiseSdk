package com.doe.topwisesdk.domain.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


public class CardDataModel implements Serializable {

    private int gender_Int;
    private int writeDataFor;
    private int cardStatus_Int;
    private int cardVersion_Int;
    private int cardBalance_Int;
    private int vehicleType_Int;
    private Long terminalId;

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    public @NotNull String toString() {
        return "CardDataModel{" +
                "gender_Int=" + gender_Int +
                ", writeDataFor=" + writeDataFor +
                ", cardStatus_Int=" + cardStatus_Int +
                ", cardVersion_Int=" + cardVersion_Int +
                ", cardBalance_Int=" + cardBalance_Int +
                ", vehicleType_Int=" + vehicleType_Int +
                ", vehicleSubType_String='" + vehicleSubType_String + '\'' +
                ", rechargeAmount_Int=" + rechargeAmount_Int +
                ", vehicleMainType_String='" + vehicleMainType_String + '\'' +
                ", singleJourneyAmt_Int=" + singleJourneyAmt_Int +
                ", journeyAmt_Int=" + journeyAmt_Int +
                ", cardNumber_Long=" + cardNumber_Long +
                ", mobileNumber_Long=" + mobileNumber_Long +
                ", aadhaarNumber_Long=" + aadhaarNumber_Long +
                ", dob_String='" + dob_String + '\'' +
                ", branchId_String='" + branchId_String + '\'' +
                ", cardName_String='" + cardName_String + '\'' +
                ", inDateTime_Long=" + inDateTime_Long +
                ", lastSyncTime_String='" + lastSyncTime_String + '\'' +
                ", cardIssuerId_String='" + cardIssuerId_String + '\'' +
                ", vehicleNumber_String='" + vehicleNumber_String + '\'' +
                ", cardExpiryDate_String='" + cardExpiryDate_String + '\'' +
                ", cardIssueDateTime_Long=" + cardIssueDateTime_Long +
                ", tollFeeExpiryDateTime_Long=" + tollFeeExpiryDateTime_Long +
                ", dobLong=" + dobLong +
                "transactionType=" + transactionType +
                ", cardExpiryDateLong=" + cardExpiryDateLong +
                ", lastSyncTimeLong=" + lastSyncTimeLong +
                ", forActivate=" + forActivate +
                ",terminalId=" + terminalId +
                '}';
    }

    private String vehicleSubType_String;
    private int rechargeAmount_Int;
    private String vehicleMainType_String;
    private int singleJourneyAmt_Int;
    private int journeyAmt_Int;

    private long cardNumber_Long;
    private long mobileNumber_Long;
    private long aadhaarNumber_Long;

    private String dob_String;
    private String branchId_String;
    private String cardName_String;
    private Long inDateTime_Long;
    private String lastSyncTime_String;
    private String cardIssuerId_String;
    private String vehicleNumber_String;
    private String cardExpiryDate_String;
    private Long cardIssueDateTime_Long;
    private Long tollFeeExpiryDateTime_Long;

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    private int transactionType;

    private Long dobLong;

    public Long getDobLong() {
        return dobLong;
    }

    public void setDobLong(Long dobLong) {
        this.dobLong = dobLong;
    }

    public Long getCardExpiryDateLong() {
        return cardExpiryDateLong;
    }

    public void setCardExpiryDateLong(Long cardExpiryDateLong) {
        this.cardExpiryDateLong = cardExpiryDateLong;
    }

    public Long getLastSyncTimeLong() {
        return lastSyncTimeLong;
    }

    public void setLastSyncTimeLong(Long lastSyncTimeLong) {
        this.lastSyncTimeLong = lastSyncTimeLong;
    }

    private Long cardExpiryDateLong;
    private Long lastSyncTimeLong;

    private boolean forActivate;

    public boolean isForActivate() {
        return forActivate;
    }

    public void setForActivate(boolean forActivate) {
        this.forActivate = forActivate;
    }

    public int getGender_Int() {
        return gender_Int;
    }

    public void setGender_Int(int gender_Int) {
        this.gender_Int = gender_Int;
    }

    public int getWriteDataFor() {
        return writeDataFor;
    }

    public void setWriteDataFor(int writeDataFor) {
        this.writeDataFor = writeDataFor;
    }

    public int getCardStatus_Int() {
        return cardStatus_Int;
    }

    public void setCardStatus_Int(int cardStatus_Int) {
        this.cardStatus_Int = cardStatus_Int;
    }

    public int getCardVersion_Int() {
        return cardVersion_Int;
    }

    public void setCardVersion_Int(int cardVersion_Int) {
        this.cardVersion_Int = cardVersion_Int;
    }

    public int getCardBalance_Int() {
        return cardBalance_Int;
    }

    public void setCardBalance_Int(int cardBalance_Int) {
        this.cardBalance_Int = cardBalance_Int;
    }

    public int getVehicleType_Int() {
        return vehicleType_Int;
    }

    public void setVehicleType_Int(int vehicleType_Int) {
        this.vehicleType_Int = vehicleType_Int;
    }

    public String getVehicleSubType_String() {
        return vehicleSubType_String;
    }

    public void setVehicleSubType_String(String vehicleSubType_String) {
        this.vehicleSubType_String = vehicleSubType_String;
    }

    public int getRechargeAmount_Int() {
        return rechargeAmount_Int;
    }

    public void setRechargeAmount_Int(int rechargeAmount_Int) {
        this.rechargeAmount_Int = rechargeAmount_Int;
    }

    public String getVehicleMainType_String() {
        return vehicleMainType_String;
    }

    public void setVehicleMainType_String(String vehicleMainType_String) {
        this.vehicleMainType_String = vehicleMainType_String;
    }

    public int getSingleJourneyAmt_Int() {
        return singleJourneyAmt_Int;
    }

    public void setSingleJourneyAmt_Int(int singleJourneyAmt_Int) {
        this.singleJourneyAmt_Int = singleJourneyAmt_Int;
    }

    public int getJourneyAmt_Int() {
        return journeyAmt_Int;
    }

    public void setJourneyAmt_Int(int journeyAmt_Int) {
        this.journeyAmt_Int = journeyAmt_Int;
    }

    public long getCardNumber_Long() {
        return cardNumber_Long;
    }

    public void setCardNumber_Long(long cardNumber_Long) {
        this.cardNumber_Long = cardNumber_Long;
    }

    public long getMobileNumber_Long() {
        return mobileNumber_Long;
    }

    public void setMobileNumber_Long(long mobileNumber_Long) {
        this.mobileNumber_Long = mobileNumber_Long;
    }

    public long getAadhaarNumber_Long() {
        return aadhaarNumber_Long;
    }

    public void setAadhaarNumber_Long(long aadhaarNumber_Long) {
        this.aadhaarNumber_Long = aadhaarNumber_Long;
    }

    public String getDob_String() {
        return dob_String;
    }

    public void setDob_String(String dob_String) {
        this.dob_String = dob_String;
    }

    public String getBranchId_String() {
        return branchId_String;
    }

    public void setBranchId_String(String branchId_String) {
        this.branchId_String = branchId_String;
    }

    public String getCardName_String() {
        return cardName_String;
    }

    public void setCardName_String(String cardName_String) {
        this.cardName_String = cardName_String;
    }

    public Long getInDateTime_Long() {
        return inDateTime_Long;
    }

    public void setInDateTime_Long(Long inDateTime_Long) {
        this.inDateTime_Long = inDateTime_Long;
    }

    public String getCardExpiryDate_String() {
        return cardExpiryDate_String;
    }

    public void setCardExpiryDate_String(String cardExpiryDate_String) {
        this.cardExpiryDate_String = cardExpiryDate_String;
    }

    public String getLastSyncTime_String() {
        return lastSyncTime_String;
    }

    public void setLastSyncTime_String(String lastSyncTime_String) {
        this.lastSyncTime_String = lastSyncTime_String;
    }

    public String getCardIssuerId_String() {
        return cardIssuerId_String;
    }

    public void setCardIssuerId_String(String cardIssuerId_String) {
        this.cardIssuerId_String = cardIssuerId_String;
    }

    public String getVehicleNumber_String() {
        return vehicleNumber_String;
    }

    public void setVehicleNumber_String(String vehicleNumber_String) {
        this.vehicleNumber_String = vehicleNumber_String;
    }

    public Long getTollFeeExpiryDateTime_Long() {
        return tollFeeExpiryDateTime_Long;
    }

    public void setTollFeeExpiryDateTime_Long(Long tollFeeExpiryDateTime_Long) {
        this.tollFeeExpiryDateTime_Long = tollFeeExpiryDateTime_Long;
    }

    public Long getCardIssueDateTime_Long() {
        return cardIssueDateTime_Long;
    }

    public void setCardIssueDateTime_Long(Long cardIssueDateTime_String) {
        this.cardIssueDateTime_Long = cardIssueDateTime_String;
    }
}
