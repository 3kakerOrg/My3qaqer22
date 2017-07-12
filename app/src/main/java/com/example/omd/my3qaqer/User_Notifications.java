package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class User_Notifications extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView user_notf_RecyclerView;
    private DatabaseReference dRef;
    private RelativeLayout user_notf_progressBar_container;
    private TextView no_notf_txt,delete_all_userNotf;
    private AlertDialog.Builder mBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__notifications);
        init_View();
        GetAll_Notifications(PhoneNumber.getPhoneNumber());
        Read_All_Notifications(PhoneNumber.getPhoneNumber());
        Delete_All_userNotifications(delete_all_userNotf);

    }
    private void init_View()
    {

        mToolbar = (Toolbar) findViewById(R.id.user_notf_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        delete_all_userNotf = (TextView) findViewById(R.id.delete_all_userNotf);
        /////////////////////////////////////////////////////////
        dRef = FirebaseDatabase.getInstance().getReference();
        /////////////////////////////////////////////////////////
        user_notf_progressBar_container = (RelativeLayout) findViewById(R.id.user_notf_progressBar_container);
        no_notf_txt                     = (TextView) findViewById(R.id.no_notf_txt);
        user_notf_progressBar_container.setVisibility(View.VISIBLE);
        //////////////////////////////////////////////////////////
        user_notf_RecyclerView = (RecyclerView) findViewById(R.id.user_notf_RecyclerView);
        user_notf_RecyclerView.setLayoutManager(new LinearLayoutManager(User_Notifications.this));
        ///////////////////////////////////////////////////////////
        mBuilder = new AlertDialog.Builder(User_Notifications.this);
        mBuilder.setMessage("حزف جميع الاشعارات ؟");
    }
    private void GetAll_Notifications(String phoneNumber)
    {
        if (!phoneNumber.isEmpty()) {
            DatabaseReference notfRef = dRef.child(Firebase_DataBase_Holder.notification).child(phoneNumber);
            notfRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        List<Notification_Model> notification_modelList = new ArrayList<Notification_Model>();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            Notification_Model model = ds.getValue(Notification_Model.class);
                            notification_modelList.add(model);
                        }
                        if (notification_modelList.size() ==0)
                        {
                            user_notf_progressBar_container.setVisibility(View.GONE);
                            no_notf_txt.setVisibility(View.VISIBLE);



                        }
                        else if (notification_modelList.size() >0)
                        {
                            User_Notifications_Adapter adapter = new User_Notifications_Adapter(User_Notifications.this, notification_modelList);
                            user_notf_RecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            user_notf_progressBar_container.setVisibility(View.GONE);

                        }

                    }
                    else
                        {
                            user_notf_progressBar_container.setVisibility(View.GONE);
                            no_notf_txt.setVisibility(View.VISIBLE);

                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
            {
                Toast.makeText(this,"تاكد من ادخال رقم المحمول", Toast.LENGTH_SHORT).show();
                user_notf_progressBar_container.setVisibility(View.GONE);
                no_notf_txt.setVisibility(View.VISIBLE);
            }
    }
    private void Read_All_Notifications(final String phoneNumber)
    {
        if (!phoneNumber.equals(null))
        {
            DatabaseReference NotfReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(phoneNumber);
            NotfReadedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                        if (Flag.isUser_notf_readed() ==true)
                        {
                            for (DataSnapshot ds:dataSnapshot.getChildren())
                            {

                                Notifications_readed_Model readedModel = ds.getValue(Notifications_readed_Model.class);
                                if (readedModel.isValue()=="false")
                                {
                                    DatabaseReference NotfReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(phoneNumber).child(ds.getKey());
                                    NotfReadedRef.child("value").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Flag.setUser_notf_readed(false);
                                                /*DatabaseReference NotfCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(phoneNumber).child("Count");
                                                NotfCountRef.setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            Flag.setUser_notf_readed(false);
                                                        }
                                                    }
                                                });*/

                                            }
                                        }
                                    });
                                }
                                else {
                                    Flag.setUser_notf_readed(false);
                                }
                            }
                        }
                    }
                    else
                    {
                        Flag.setUser_notf_readed(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private void Delete_All_userNotifications(TextView delete_all_userNotf)
    {
        delete_all_userNotf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBuilder.setPositiveButton("حزف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!PhoneNumber.getPhoneNumber().equals(null)) {
                            DatabaseReference NotRef = dRef.child(Firebase_DataBase_Holder.notification).child(PhoneNumber.getPhoneNumber().toString());
                            NotRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        DatabaseReference NotReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(PhoneNumber.getPhoneNumber().toString());
                                        NotReadedRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    DatabaseReference NotCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(PhoneNumber.getPhoneNumber().toString());
                                                    NotCountRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(User_Notifications.this, "تم حزف جميع الاشعارات", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                mBuilder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            mBuilder.show();
            }

        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        Flag.setUser_notf_readed(true);
    }
    private void CreateNotification(String title,String contenttext)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(User_Notifications.this);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(contenttext);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setSmallIcon(R.drawable.add2);
        mBuilder.build();
        Intent intent = new Intent(this,User_Notifications.class);
        PendingIntent pi = PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(100,mBuilder.build());
    }

}
