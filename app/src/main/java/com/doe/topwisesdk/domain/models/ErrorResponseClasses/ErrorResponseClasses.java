package com.doe.topwisesdk.domain.models.ErrorResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorResponseClasses {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("debugId")
    @Expose
    private String debugId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errorDetails")
    @Expose
    private List<ErrorDetail> errorDetails = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDebugId() {
        return debugId;
    }

    public void setDebugId(String debugId) {
        this.debugId = debugId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<ErrorDetail> errorDetails) {
        this.errorDetails = errorDetails;
    }

}