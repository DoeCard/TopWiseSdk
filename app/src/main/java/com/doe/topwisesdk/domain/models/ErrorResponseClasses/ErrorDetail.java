package com.doe.topwisesdk.domain.models.ErrorResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorDetail {

    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("issue")
    @Expose
    private String issue;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

}