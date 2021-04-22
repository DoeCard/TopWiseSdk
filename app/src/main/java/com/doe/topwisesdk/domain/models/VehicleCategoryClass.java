package com.doe.topwisesdk.domain.models;


import androidx.annotation.NonNull;

public class VehicleCategoryClass implements Comparable<VehicleCategoryClass> {


    private String categoryCodeString;
    private int categoryNumericCode;
    private String fullLabel;
    private String vehicleTypeCode;
    private String vehicleTypeLabel;
    private String vehicleCategoryCode;
    private String vehicleCategoryLabel;


    public String getCategoryCodeString() {
        return categoryCodeString;
    }

    public void setCategoryCodeString(String categoryCodeString) {
        this.categoryCodeString = categoryCodeString;
    }

    public int getCategoryNumericCode() {
        return categoryNumericCode;
    }

    public void setCategoryNumericCode(int categoryNumericCode) {
        this.categoryNumericCode = categoryNumericCode;
    }

    public String getFullLabel() {
        return fullLabel;
    }

    public void setFullLabel(String fullLabel) {
        this.fullLabel = fullLabel;
    }


    public String getVehicleTypeLabel() {

        vehicleTypeLabel = fullLabel.split("_")[1];

        return vehicleTypeLabel;
    }


    public String getVehicleCategoryLabel() {

        vehicleCategoryLabel = fullLabel.split("_")[0];

        return vehicleCategoryLabel;
    }

    @Override
    public int compareTo(@NonNull VehicleCategoryClass o) {

        /* For Ascending order*/
        return categoryNumericCode-o.getCategoryNumericCode();

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }
}
