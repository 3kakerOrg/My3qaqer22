package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by Delta on 17/06/2017.
 */

public class GpsLoc extends Service implements LocationListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    String GPSProvider = LocationManager.GPS_PROVIDER;
    String NetworkProvider = LocationManager.NETWORK_PROVIDER;
    Location mLocation;
    LocationManager mLocationManager;
    Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean mcanGetLocation = false;
    double Latitude;
    double Longitude;

    public GpsLoc(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isNetworkEnabled = mLocationManager.isProviderEnabled(NetworkProvider);
            isGPSEnabled = mLocationManager.isProviderEnabled(GPSProvider);
            if (!isNetworkEnabled && !isGPSEnabled) {

            } else {

                mcanGetLocation = true;
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(NetworkProvider, 0, 0, this);
                    if (mLocationManager !=null){
                        mLocation = mLocationManager.getLastKnownLocation(NetworkProvider);
                        if (mLocation!=null)
                        {
                            Latitude = mLocation.getLatitude();
                            Longitude = mLocation.getLongitude();
                        }
                    }



                }

                if (isGPSEnabled){
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(GPSProvider, 0, 0, this);

                        if (mLocationManager !=null){
                            mLocation = mLocationManager.getLastKnownLocation(GPSProvider);
                            if (mLocation!=null)
                            {
                                Latitude = mLocation.getLatitude();
                                Longitude = mLocation.getLongitude();
                            }
                        }
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mLocation;
    }
    public double getLatitude()
    {
        if (mLocation !=null){
            Latitude = mLocation.getLatitude();
        }
        return Latitude;
    }
    public double getLongitude()
    {
        if (mLocation!=null){
            Longitude = mLocation.getLongitude();
        }
        return Longitude;
    }
    private void StopGPS(){
        if (mLocationManager!=null){
            mLocationManager.removeUpdates(this);
        }
    }
    public boolean canGetLocation(){
        return this.mcanGetLocation;
    }
    public void ShowAlertDialog(Context context)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Settings");
        dialog.setMessage("Gps is not enable Do you want open ?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }

}
