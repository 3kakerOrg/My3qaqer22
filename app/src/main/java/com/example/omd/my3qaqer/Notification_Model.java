package com.example.omd.my3qaqer;

/**
 * Created by Delta on 23/06/2017.
 */

public class Notification_Model {
    String Notification_text;
    String User_id;
    String drug_name;

    public Notification_Model() {
    }

    public Notification_Model(String notification_text, String user_id, String drug_name) {
        Notification_text = notification_text;
        User_id = user_id;
        this.drug_name = drug_name;
    }

    public String getNotification_text() {
        return Notification_text;
    }

    public void setNotification_text(String notification_text) {
        Notification_text = notification_text;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }
}
