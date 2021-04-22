package com.doe.topwisesdk.domain.models.tollpass;

import java.util.List;

public class TemporaryTollPassResponse {
    public List<TemporaryTollPassPojo> getTemporaryTollPassPojoList() {
        return temporaryTollPassPojoList;
    }

    @Override
    public String toString() {
        return "TemporaryTollPassResponse{" +
                "temporaryTollPassPojoList=" + temporaryTollPassPojoList +
                ", message='" + message + '\'' +
                '}';
    }

    public void setTemporaryTollPassPojoList(List<TemporaryTollPassPojo> temporaryTollPassPojoList) {
        this.temporaryTollPassPojoList = temporaryTollPassPojoList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private List<TemporaryTollPassPojo> temporaryTollPassPojoList;
    private String message;
}
