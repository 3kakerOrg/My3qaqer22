package com.example.omd.my3qaqer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pharmacy_Notifications extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView pharmacy_not_recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private RelativeLayout pharmacy_notf_progressBar_container;
    private TextView pharmacy_no_notf_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy__notifications);
        init_View();
        Get_All_Notifications(mAuth.getCurrentUser().getUid().toString());
        Read_All_Notifications(mAuth.getCurrentUser().getUid().toString());
    }

    private void init_View()
    {
        mToolbar = (Toolbar) findViewById(R.id.pharmacy_not_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef  = FirebaseDatabase.getInstance().getReference();
        /////////////////////////////////////////////////////
        pharmacy_notf_progressBar_container = (RelativeLayout) findViewById(R.id.pharmacy_notf_progressBar_container);
        pharmacy_no_notf_txt                = (TextView) findViewById(R.id.pharmacy_no_notf_txt);
        pharmacy_notf_progressBar_container.setVisibility(View.VISIBLE);
        /////////////////////////////////////////////////////
        pharmacy_not_recyclerView = (RecyclerView) findViewById(R.id.pharmacy_not_recyclerView);
        pharmacy_not_recyclerView.setLayoutManager(new LinearLayoutManager(Pharmacy_Notifications.this));
    }
    private void Get_All_Notifications(String phrmacy_id)
    {
        DatabaseReference NotfRef = dRef.child(Firebase_DataBase_Holder.notification).child(phrmacy_id);
        NotfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    List<Notification_Model> notification_modelList = new ArrayList<Notification_Model>();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        for (DataSnapshot ds2 :ds.getChildren())
                        {
                            Notification_Model model = ds2.getValue(Notification_Model.class);
                            notification_modelList.add(model);
                        }
                    }
                    if (notification_modelList.size() ==0)
                    {
                        pharmacy_notf_progressBar_container.setVisibility(View.GONE);
                        pharmacy_no_notf_txt.setVisibility(View.VISIBLE);

                    }
                    else if (notification_modelList.size()>0)
                    {

                        Pharmacy_Notification_Adapter adapter = new Pharmacy_Notification_Adapter(Pharmacy_Notifications.this,notification_modelList);
                        pharmacy_not_recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        pharmacy_notf_progressBar_container.setVisibility(View.GONE);
                    }

                }else
                    {
                        pharmacy_notf_progressBar_container.setVisibility(View.GONE);
                        pharmacy_no_notf_txt.setVisibility(View.VISIBLE);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Read_All_Notifications(final String phrmacy_id)
    {
        DatabaseReference NotfReadRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(phrmacy_id);
        NotfReadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    if (Flag.isNotfReaded() ==true){
                        for (DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            Notifications_readed_Model readedModel = ds.getValue(Notifications_readed_Model.class);

                            if (readedModel.isValue().equals("false"))
                            {
                                DatabaseReference NotfReadRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(phrmacy_id);
                                NotfReadRef.child(ds.getKey()).child("value").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            DatabaseReference CountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(phrmacy_id).child("Count");
                                            CountRef.setValue(0);

                                        }
                                    }
                                });

                            }
                        }
                    }
                }else
                    {
                        Flag.setNotfReaded(false);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Flag.setNotfReaded(true);
    }


}
