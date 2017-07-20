package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.omd.my3qaqer.fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private static final String MAIN_FRAG_TAG="main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, Profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainFragment(), MAIN_FRAG_TAG).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment mainF = getSupportFragmentManager().findFragmentByTag(MAIN_FRAG_TAG);
        if (mainF != null) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainFragment(),MAIN_FRAG_TAG).commit();
        }


    }
}
