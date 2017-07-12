package com.example.omd.my3qaqer;

/**
 * Created by Delta on 17/06/2017.
 */

public class Pharmacy_Model {
    String pharmacy_name;
    String pharmacy_phone;
    String pharmacy_licence;
    String pharmacy_location;
    String pharmacy_image;


    public Pharmacy_Model() {
    }

    public String getPharmacy_image() {
        return pharmacy_image;
    }

    public void setPharmacy_image(String pharmacy_image) {
        this.pharmacy_image = pharmacy_image;
    }

    public Pharmacy_Model(String pharmacy_name, String pharmacy_phone, String pharmacy_licence, String pharmacy_location, String pharmacy_image) {
        this.pharmacy_name     = pharmacy_name;
        this.pharmacy_phone    = pharmacy_phone;
        this.pharmacy_licence  = pharmacy_licence;
        this.pharmacy_location = pharmacy_location;
        this.pharmacy_image    = pharmacy_image;

    }

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
    }

    public String getPharmacy_phone() {
        return pharmacy_phone;
    }

    public void setPharmacy_phone(String pharmacy_phone) {
        this.pharmacy_phone = pharmacy_phone;
    }

    public String getPharmacy_licence() {
        return pharmacy_licence;
    }

    public void setPharmacy_licence(String pharmacy_licence) {
        this.pharmacy_licence = pharmacy_licence;
    }

    public String getPharmacy_location() {
        return pharmacy_location;
    }

    public void setPharmacy_location(String pharmacy_location) {
        this.pharmacy_location = pharmacy_location;
    }

}
