package com.example.omd.my3qaqer;

import java.io.Serializable;

/**
 * Created by Delta on 17/06/2017.
 */

public class Drug_Model implements Serializable{
    String Drug_image;
    String Drug_concentration;
    String Drug_name;
    String Drug_type;
    String Drug_pharmacyid;

    public Drug_Model() {
    }

    public Drug_Model(String drug_image, String drug_concentration, String drug_name, String drug_type, String drug_pharmacyid) {
        Drug_image = drug_image;
        Drug_concentration = drug_concentration;
        Drug_name = drug_name;
        Drug_type = drug_type;
        Drug_pharmacyid = drug_pharmacyid;
    }

    public String getDrug_image() {
        return Drug_image;
    }

    public void setDrug_image(String drug_image) {
        Drug_image = drug_image;
    }

    public String getDrug_concentration() {
        return Drug_concentration;
    }

    public void setDrug_concentration(String drug_concentration) {
        Drug_concentration = drug_concentration;
    }

    public String getDrug_name() {
        return Drug_name;
    }

    public void setDrug_name(String drug_name) {
        Drug_name = drug_name;
    }

    public String getDrug_type() {
        return Drug_type;
    }

    public void setDrug_type(String drug_type) {
        Drug_type = drug_type;
    }

    public String getDrug_pharmacyid() {
        return Drug_pharmacyid;
    }

    public void setDrug_pharmacyid(String drug_pharmacyid) {
        Drug_pharmacyid = drug_pharmacyid;
    }
}
