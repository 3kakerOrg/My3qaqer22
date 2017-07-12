package com.example.omd.my3qaqer;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Delta on 17/06/2017.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
