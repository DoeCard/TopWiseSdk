package com.doe.topwisesdk.domain.models.genericdata;

import java.util.List;

public class GenericDataPojoResponse {
    private String message;

    @Override
    public String toString() {
        return "GenericDataPojoResponse{" +
                "message='" + message + '\'' +
                ", genericDataPojoList=" + genericDataPojoList +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GenericDataPojo> getGenericDataPojoList() {
        return genericDataPojoList;
    }

    public void setGenericDataPojoList(List<GenericDataPojo> genericDataPojoList) {
        this.genericDataPojoList = genericDataPojoList;
    }

    private List<GenericDataPojo> genericDataPojoList;
}
