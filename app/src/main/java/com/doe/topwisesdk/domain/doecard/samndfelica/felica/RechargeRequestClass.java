package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RechargeRequestClass {

    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("amount")
    @Expose
    private Amount amount;
    @SerializedName("activityTime")
    @Expose
    private String activityTime;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("readerId")
    @Expose
    private String readerId;
    @SerializedName("cardNumber")
    @Expose
    private String cardNumber;

    private int transactionType;

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    private Long terminalId;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

}