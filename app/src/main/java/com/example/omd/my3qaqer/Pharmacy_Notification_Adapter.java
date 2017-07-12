package com.example.omd.my3qaqer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Delta on 23/06/2017.
 */

public class Pharmacy_Notification_Adapter extends RecyclerView.Adapter<Pharmacy_Notification_Adapter.ViewHolder> {

    Context mContext;
    List<Notification_Model> notificationModelList;
    LayoutInflater inflater;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    public Pharmacy_Notification_Adapter(Context mContext, List<Notification_Model> notificationModelList)
    {
        this.mContext              = mContext;
        this.notificationModelList = notificationModelList;
        inflater                   = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAuth                      = FirebaseAuth.getInstance();
        dRef                       = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.pharmacy_not_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder,int position)
    {
        Notification_Model model = notificationModelList.get(position);
        holder.notf_txt.setText(model.getNotification_text().toString());
        holder.avialableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flag.setUser_notf_readed(false);
                Notification_Model model =notificationModelList.get(holder.getLayoutPosition());
                Send_Notf_to_user(model);
            }
        });
        holder.delete_notf_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flag.setUser_notf_readed(false);
                Notification_Model model = notificationModelList.get(holder.getLayoutPosition());
                Delete_notifications(model);
            }
        });
    }
    private void Delete_notifications(final Notification_Model model)
    {

        DatabaseReference NotRef = dRef.child(Firebase_DataBase_Holder.notification).child(mAuth.getCurrentUser().getUid().toString());
        NotRef.child(model.getUser_id()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                    DatabaseReference NotReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(mAuth.getCurrentUser().getUid().toString());
                    NotReadedRef.child(model.getDrug_name()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(mContext, "تم الحزف", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView notf_txt;
        public Button   avialableBtn,delete_notf_txt;
        public ViewHolder(View itemView) {
            super(itemView);
            notf_txt       = (TextView) itemView.findViewById(R.id.pharmacy_notf_txt);
            avialableBtn   = (Button)   itemView.findViewById(R.id.avialableBtn);
            delete_notf_txt= (Button)   itemView.findViewById(R.id.delete_not_Btn);
        }
    }
    private void Send_Notf_to_user(final Notification_Model model)
    {
        final String user_id   = model.getUser_id().toString();
        String notf_text = "متوفر الان";
        Notification_Model notification_model = new Notification_Model(notf_text,mAuth.getCurrentUser().getUid().toString(),model.getDrug_name());
        final DatabaseReference notfRef = dRef.child(Firebase_DataBase_Holder.notification).child(user_id).push();
        notfRef.setValue(notification_model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    DatabaseReference notReadedRef        = dRef.child(Firebase_DataBase_Holder.notification_readed).child(user_id).push();
                    Notifications_readed_Model readedModel= new Notifications_readed_Model("false",mAuth.getCurrentUser().getUid().toString());
                    notReadedRef.setValue(readedModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                DatabaseReference notfRef = dRef.child(Firebase_DataBase_Holder.notification).child(mAuth.getCurrentUser().getUid().toString()).child(model.getUser_id()).child(model.drug_name).getRef();
                                notfRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            DatabaseReference notfReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(mAuth.getCurrentUser().getUid().toString()).child(model.getDrug_name()).getRef();
                                            notfReadedRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        //GetNotifications(user_id);
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
    }
    private void GetNotifications(final String user_id)
    {
        DatabaseReference notReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(user_id);
        notReadedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    int count =0;
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        Notifications_readed_Model readedModel = ds.getValue(Notifications_readed_Model.class);
                        if (readedModel.isValue()=="false")
                        {
                            count++;
                        }

                    }
                    DatabaseReference notCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(user_id);
                    notCountRef.child("Count").setValue(count);


                }
                else
                {
                    DatabaseReference notCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(user_id);
                    notCountRef.child("Count").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
