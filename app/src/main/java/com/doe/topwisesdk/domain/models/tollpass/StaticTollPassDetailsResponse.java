package com.doe.topwisesdk.domain.models.tollpass;

import java.util.List;

public class StaticTollPassDetailsResponse {
    private List<StaticTollPassDetailPojo> staticTollPassDetailPojoList;

    public List<StaticTollPassDetailPojo> getStaticTollPassDetailPojoList() {
        return staticTollPassDetailPojoList;
    }

    @Override
    public String toString() {
        return "StaticTollPassDetailsResponse{" +
                "staticTollPassDetailPojoList=" + staticTollPassDetailPojoList +
                ", message='" + message + '\'' +
                '}';
    }

    public void setStaticTollPassDetailPojoList(List<StaticTollPassDetailPojo> staticTollPassDetailPojoList) {
        this.staticTollPassDetailPojoList = staticTollPassDetailPojoList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}
