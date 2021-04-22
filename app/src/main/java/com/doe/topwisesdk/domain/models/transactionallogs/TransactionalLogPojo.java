package com.doe.topwisesdk.domain.models.transactionallogs;

public class TransactionalLogPojo {
    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public long getTxnDateTimeInMilliSeconds() {
        return txnDateTimeInMilliSeconds;
    }

    public void setTxnDateTimeInMilliSeconds(long txnDateTimeInMilliSeconds) {
        this.txnDateTimeInMilliSeconds = txnDateTimeInMilliSeconds;
    }

    @Override
    public String toString() {
        return "TransactionalLogPojo{" +
                "transactionType=" + transactionType +
                ", txnDateTimeInMilliSeconds=" + txnDateTimeInMilliSeconds +
                ", amount=" + amount +
                ", terminalId=" + terminalId +
                '}';
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(long terminalId) {
        this.terminalId = terminalId;
    }

    //Transaction Type,TxnDateTimeInMilliSeconds,Amount,TerminalId
    private int transactionType;
    private long txnDateTimeInMilliSeconds;
    private int amount;
    private long terminalId;


}
