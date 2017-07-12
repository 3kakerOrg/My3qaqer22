package com.example.omd.my3qaqer;

import java.io.Serializable;

/**
 * Created by Delta on 06/07/2017.
 */

public class PostModel implements Serializable {
    String userPhone;
    String userName;
    String userAddress;
    String drugImage;
    String drugName;
    String drugConcentrate;
    String drugType;
    String date;

    public PostModel() {
    }

    public PostModel(String userPhone, String userName, String userAddress, String drugImage, String drugName, String drugConcentrate, String drugType, String date) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.userAddress = userAddress;
        this.drugImage = drugImage;
        this.drugName = drugName;
        this.drugConcentrate = drugConcentrate;
        this.drugType = drugType;
        this.date = date;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getDrugImage() {
        return drugImage;
    }

    public void setDrugImage(String drugImage) {
        this.drugImage = drugImage;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugConcentrate() {
        return drugConcentrate;
    }

    public void setDrugConcentrate(String drugConcentrate) {
        this.drugConcentrate = drugConcentrate;
    }

    public String getDrugType() {
        return drugType;
    }

    public void setDrugType(String drugType) {
        this.drugType = drugType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
