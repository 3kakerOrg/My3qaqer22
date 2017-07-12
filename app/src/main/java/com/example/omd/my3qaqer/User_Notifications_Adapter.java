package com.example.omd.my3qaqer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Delta on 24/06/2017.
 */

public class User_Notifications_Adapter extends RecyclerView.Adapter<User_Notifications_Adapter.ViewHolder> {
    Context mContext;
    List<Notification_Model> notificationModelList;
    LayoutInflater inflater;
    FirebaseAuth mAuth;
    DatabaseReference dRef;

    public User_Notifications_Adapter(Context mContext, List<Notification_Model> notificationModelList) {
        this.mContext = mContext;
        this.notificationModelList = notificationModelList;
        inflater                   = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAuth                      = FirebaseAuth.getInstance();
        dRef                       = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_notf_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification_Model model = notificationModelList.get(position);
        holder.user_notf_Drugstate.setText(model.getNotification_text().toString());
        holder.user_notf_Drugname.setText(model.getDrug_name());
        GetPharmacy_Info(holder,model);

    }

    private void GetPharmacy_Info(final ViewHolder holder, Notification_Model model) {

            DatabaseReference pharmacyInfoRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(model.getUser_id());
            pharmacyInfoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        Pharmacy_Model pharmacy_model = dataSnapshot.getValue(Pharmacy_Model.class);
                        holder.user_notf_pharmacy_name.setText(pharmacy_model.getPharmacy_name().toString());
                        holder.user_notf_pharmacy_location.setText(pharmacy_model.getPharmacy_location().toString());
                        holder.user_notf_pharmacy_phone.setText(pharmacy_model.getPharmacy_phone().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_notf_Drugstate,user_notf_Drugname,user_notf_pharmacy_name,user_notf_pharmacy_location,user_notf_pharmacy_phone;
        public ViewHolder(View itemView) {
            super(itemView);
            user_notf_Drugstate         = (TextView) itemView.findViewById(R.id.user_notf_Drugstate);
            user_notf_Drugname          = (TextView) itemView.findViewById(R.id.user_notf_Drugname);
            user_notf_pharmacy_name     = (TextView) itemView.findViewById(R.id.user_notf_pharmacy_name);
            user_notf_pharmacy_location = (TextView) itemView.findViewById(R.id.user_notf_pharmacy_location);
            user_notf_pharmacy_phone    = (TextView) itemView.findViewById(R.id.user_notf_pharmacy_phone);
        }
    }
}
