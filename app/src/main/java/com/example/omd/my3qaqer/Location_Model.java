package com.example.omd.my3qaqer;

import java.io.Serializable;

/**
 * Created by Delta on 17/06/2017.
 */

public class Location_Model implements Serializable{
    String latitude;
    String longitude;

    public Location_Model() {
    }

    public Location_Model(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
