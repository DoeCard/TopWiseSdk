package com.doe.topwisesdk.domain.models.transactionallogs;

import java.util.List;

public class TransactionalLogsResponse {
    public List<TransactionalLogPojo> getTransactionalLogPojoList() {
        return transactionalLogPojoList;
    }

    public void setTransactionalLogPojoList(List<TransactionalLogPojo> transactionalLogPojoList) {
        this.transactionalLogPojoList = transactionalLogPojoList;
    }

    @Override
    public String toString() {
        return "TransactionalLogsResponse{" +
                "transactionalLogPojoList=" + transactionalLogPojoList +
                ", message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private List<TransactionalLogPojo> transactionalLogPojoList;
    private String message;
}
