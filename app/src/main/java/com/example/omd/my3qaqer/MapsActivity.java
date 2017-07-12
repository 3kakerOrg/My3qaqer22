package com.example.omd.my3qaqer;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FloatingActionsMenu fabBtn_menu_map;
    private GoogleMap mMap;
    private Button ShowBtn,View_nearby_ResultBtn;
    private DatabaseReference dRef;
    List<String> pharmacyKeysList;
    List<Drug_Model> drug_model_List;
    Location location_A;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fabBtn_menu_map = (FloatingActionsMenu) findViewById(R.id.fabBtn_menu_map);
        FloatingActionButton faball_results = new FloatingActionButton(this);
        faball_results.setSize(FloatingActionButton.SIZE_MINI);
        faball_results.setIcon(R.drawable.see_all_icon);
        fabBtn_menu_map.addButton(faball_results);

        FloatingActionButton fab_see_nearby_result = new FloatingActionButton(this);
        fab_see_nearby_result.setSize(FloatingActionButton.SIZE_MINI);
        fab_see_nearby_result.setIcon(R.drawable.nearby_icon);
        fabBtn_menu_map.addButton(fab_see_nearby_result);


        dRef = FirebaseDatabase.getInstance().getReference();
        location_A = new Location("A");
        location_A.setLatitude(new GpsLoc(MapsActivity.this).getLatitude());
        location_A.setLongitude(new GpsLoc(MapsActivity.this).getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Flag.setFlag(false);
        Intent intent = getIntent();
        drug_model_List = (List<Drug_Model>) intent.getSerializableExtra("drugmodel");
        pharmacyKeysList = (List<String>) intent.getSerializableExtra("pharmacyKeys");
          faball_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this,Result_Activity.class);
                i.putExtra("drugmodel", (Serializable) drug_model_List);
                i.putExtra("pharmacyKeys", (Serializable) pharmacyKeysList);
                startActivity(i);
            }
        });
        fab_see_nearby_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flag.setLocflag(true);
                Intent i = new Intent(MapsActivity.this,Nearby.class);
                i.putExtra("drugmodel", (Serializable) drug_model_List);
                i.putExtra("pharmacyKeys", (Serializable) pharmacyKeysList);
                startActivity(i);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        GetLocation(pharmacyKeysList);
        double lat = new GpsLoc(MapsActivity.this).getLatitude();
        double lng = new GpsLoc(MapsActivity.this).getLongitude();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),8.0f));


    }
    private void GetLocation(final List<String> pharmacyKeysList)
    {
        DatabaseReference locationRef = dRef.child(Firebase_DataBase_Holder.location_Info);
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    if (Flag.isLocflag()==true)
                    {

                        for (String s:pharmacyKeysList)
                        {
                            Location_Model location_model = dataSnapshot.child(s).getValue(Location_Model.class);
                            Double lat = Double.valueOf(location_model.getLatitude());
                            Double lon = Double.valueOf(location_model.getLongitude());
                            Location location_B = new Location("B");
                            location_B.setLatitude(lat);
                            location_B.setLongitude(lon);
                            double distance = 0;
                            distance = location_A.distanceTo(location_B);
                            if (distance<25000)
                            {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                            }

                        }

                        Flag.setLocflag(false);
                    }
                    else
                        {
                            Flag.setLocflag(false);
                        }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
