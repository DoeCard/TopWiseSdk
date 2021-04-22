package com.doe.topwisesdk.domain.models.ErrorResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponseClass {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("rule")
    @Expose
    private String rule;
    @SerializedName("code")
    @Expose
    private Integer code;

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field The field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The rule
     */
    public String getRule() {
        return rule;
    }

    /**
     * @param rule The rule
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @return The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

}

