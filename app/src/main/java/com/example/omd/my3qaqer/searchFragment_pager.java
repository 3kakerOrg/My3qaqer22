package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.Serializable;
import java.text.Bidi;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Delta on 02/07/2017.
 */

public class searchFragment_pager extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private SearchView search;
    private Context mContext;
    private ProgressDialog mDialog;
    private AlertDialog m_AlertDialog,m_AlertDialog_phone;
    private CardView phone_container;
    private TextView note_tex,alert_send_phonenumber,alert_cancelBtn,alert_text_drug,notf_txt;
    private TextView m_alert_send_phonenumber,m_alert_cancelBtn;
    private com.getbase.floatingactionbutton.FloatingActionButton goto_posts;
    private EditText alert_text_phone,m_alert_text_phone,m_alert_text_name,m_alert_text_address;
    private String phone;
    private String Drugname;
    private ImageView user_notf;
    private int x ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.searchfragment_pager,container,false);
        init_View(view);
        CancelBtn_Action(alert_cancelBtn);
        sendBtn_Action(alert_send_phonenumber);
        search_Action(search);
        GetNotifications_Count();
        setNotificationCount();
        Show_All_userNotifications(user_notf);
        return view;
    }
    private void init_View(View view)
    {
        mContext              = view.getContext();
        search                = (SearchView) view.findViewById(R.id.search);
        notf_txt              = (TextView) view.findViewById(R.id.notf_txt);
        user_notf             = (ImageView) view.findViewById(R.id.user_notf);
        goto_posts            = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.goto_allpost);
        /////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////
        dRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        ////////////////////////////////////////////////////////////////////////
        final SharedPreferences pref = getActivity().getSharedPreferences("pref1", MODE_PRIVATE);
        String phonenumber = pref.getString("phone", "");
        PhoneNumber.setPhoneNumber(phonenumber);
        ////////////////////////////////////////////////////////////////////////
        m_AlertDialog = new AlertDialog.Builder(mContext).create();
        View v                = getActivity().getLayoutInflater().inflate(R.layout.alert_dialog,null);
        alert_text_phone      = (EditText) v.findViewById(R.id.alert_text_phone);
        alert_send_phonenumber= (TextView) v.findViewById(R.id.alert_send_phonenumber);
        alert_cancelBtn       = (TextView) v.findViewById(R.id.alert_cancelBtn);
        alert_text_drug       = (TextView) v.findViewById(R.id.alert_text_drug);
        phone_container       = (CardView) v.findViewById(R.id.phone_container);
        note_tex              = (TextView) v.findViewById(R.id.note);
        m_AlertDialog.setView(v);

        m_AlertDialog_phone      = new AlertDialog.Builder(mContext).create();
        View v2                  = getActivity().getLayoutInflater().inflate(R.layout.alert_dialog_phone,null);
        m_alert_text_phone       = (EditText) v2.findViewById(R.id.m_alert_text_phone);
        m_alert_send_phonenumber = (TextView) v2.findViewById(R.id.m_alert_send_phonenumber);
        m_alert_cancelBtn        = (TextView) v2.findViewById(R.id.m_alert_cancelBtn);
        m_AlertDialog_phone.setView(v2);
        /////////////////////////////////////////////////////////////////////////
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("جاري البحث انتظر قليلا...");
        mDialog.setCanceledOnTouchOutside(false);
        ////////////////////////////////////////////////////////////////////////
        goto_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   if (PhoneNumber.getPhoneNumber().isEmpty())
                {
                    m_AlertDialog_phone.show();
                }
                else
                    {
                        getActivity().startActivity(new Intent(mContext,All_Posts.class));

                    }

            }
        });
        m_alert_send_phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (m_alert_text_phone.getText().toString().isEmpty())
                {
                    Toast.makeText(mContext,getResources().getString(R.string.checkphone), Toast.LENGTH_SHORT).show();

                }
                else if (!m_alert_text_phone.getText().toString().matches("^(010|011|012)[0-9]{8}"))
                {
                    Toast.makeText(mContext,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    CreateSharedPreferenc(m_alert_text_phone.getText().toString());
                    PhoneNumber.setPhoneNumber(m_alert_text_phone.getText().toString());
                    getActivity().startActivity(new Intent(mContext,All_Posts.class));
                    m_AlertDialog_phone.dismiss();



                }


            }
        });
        m_alert_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                m_AlertDialog_phone.dismiss();
            }
        });


    }
    private void search_Action(final SearchView search)
    {

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Flag.setFlag(true);
                Flag.setLocflag(true);
                mDialog.show();
                Bidi bidi = new Bidi(query,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                if (bidi.getBaseLevel()==1)
                {
                    Toast.makeText(mContext, "يرجى كتابه الدواء بالغه الانجليزيه", Toast.LENGTH_SHORT).show();
                    Flag.setFlag(false);
                    Flag.setLocflag(false);
                    mDialog.dismiss();
                    search.setQuery("",false);
                }
                else
                {
                    Flag.setLocflag(true);
                    Search_aboutLocation_Drugs(query);

                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void CancelBtn_Action(TextView alert_cancelBtn)
    {

        alert_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_AlertDialog.dismiss();
            }
        });
    }
    private void sendBtn_Action(final TextView alert_send_phonenumber)
    {

        alert_send_phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Flag.setNotfReaded(false);
                search.setQuery("",false);
                search.setIconified(true);
                String Phone_number =alert_text_phone.getText().toString();
                Drugname     = alert_text_drug.getText().toString();
                Bidi bidi = new Bidi(Drugname,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                x =phone_container.getVisibility();
                if (x==8)
                {
                    if (TextUtils.isEmpty(Drugname))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.checkDrugname), Toast.LENGTH_SHORT).show();

                    }
                    else if (!TextUtils.isEmpty(Drugname))
                    {
                        if (bidi.getBaseLevel()==1)
                        {
                            Toast.makeText(mContext, "يرجى كتابه الدواء بالغه الانجليزيه", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Flag.setFlag_notification(true);
                            if (PhoneNumber.getPhoneNumber().isEmpty())
                            {
                                note_tex.setVisibility(View.VISIBLE);
                                phone = alert_text_phone.getText().toString();
                                CreateSharedPreferenc(phone);
                                PhoneNumber.setPhoneNumber(phone);
                                getAllPharmacyKeys(phone);
                                alert_text_phone.setText(null);
                                alert_text_drug.setText(null);
                            }
                            else
                            {
                                note_tex.setVisibility(View.GONE);
                                getAllPharmacyKeys(PhoneNumber.getPhoneNumber());
                                alert_text_phone.setText(null);
                                alert_text_drug.setText(null);
                            }


                        }
                    }

                    else if (PhoneNumber.getPhoneNumber().isEmpty())
                    {

                        Flag.setFlag_notification(true);
                        phone = alert_text_phone.getText().toString();
                        CreateSharedPreferenc(phone);
                        PhoneNumber.setPhoneNumber(phone);
                        getAllPharmacyKeys(phone);
                        phone_container.setVisibility(View.VISIBLE);
                        note_tex.setVisibility(View.VISIBLE);
                        alert_text_phone.setText(null);
                        alert_text_drug.setText(null);
                    }

                    else
                    {
                        Flag.setFlag_notification(true);
                        getAllPharmacyKeys(PhoneNumber.getPhoneNumber());
                        note_tex.setVisibility(View.GONE);
                        phone_container.setVisibility(View.GONE);
                        alert_text_phone.setText(null);
                        alert_text_drug.setText(null);
                    }

                }
                else if (TextUtils.isEmpty(Phone_number))
                {
                    Toast.makeText(mContext,getResources().getString(R.string.checkphone), Toast.LENGTH_SHORT).show();
                }
                else if (!Phone_number.matches("^(010|011|012)[0-9]{8}$"))
                {
                    Toast.makeText(mContext,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(Drugname))
                {
                    Toast.makeText(mContext,getResources().getString(R.string.checkDrugname), Toast.LENGTH_SHORT).show();

                }
                else if (!TextUtils.isEmpty(Drugname))
                {
                    if (bidi.getBaseLevel()==1)
                    {
                        Toast.makeText(mContext, "يرجى كتابه الدواء بالغه الانجليزيه", Toast.LENGTH_SHORT).show();

                    }
                    else if (TextUtils.isEmpty(Phone_number))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.checkphone), Toast.LENGTH_SHORT).show();

                    }
                    else if (!Phone_number.matches("^(010|011|012)[0-9]{8}$"))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Flag.setFlag_notification(true);
                        if (PhoneNumber.getPhoneNumber().isEmpty())
                        {
                            note_tex.setVisibility(View.VISIBLE);
                            phone = alert_text_phone.getText().toString();
                            CreateSharedPreferenc(phone);
                            PhoneNumber.setPhoneNumber(phone);
                            getAllPharmacyKeys(phone);
                            alert_text_phone.setText(null);
                            alert_text_drug.setText(null);
                        }
                        else
                        {
                            note_tex.setVisibility(View.GONE);
                            getAllPharmacyKeys(PhoneNumber.getPhoneNumber());
                            alert_text_phone.setText(null);
                            alert_text_drug.setText(null);
                        }


                    }
                }
                else
                {

                    Flag.setFlag_notification(true);
                    if (PhoneNumber.getPhoneNumber().isEmpty())
                    {
                        note_tex.setVisibility(View.VISIBLE);
                        phone = alert_text_phone.getText().toString();
                        CreateSharedPreferenc(phone);
                        PhoneNumber.setPhoneNumber(phone);
                        getAllPharmacyKeys(phone);
                        alert_text_phone.setText(null);
                        alert_text_drug.setText(null);
                    }
                    else
                    {
                        note_tex.setVisibility(View.GONE);
                        getAllPharmacyKeys(PhoneNumber.getPhoneNumber());
                        alert_text_phone.setText(null);
                        alert_text_drug.setText(null);
                    }



                }
            }




        });

    }
    private void GetNotifications(final String pharmacy_id)
    {
        DatabaseReference notReadedRef = dRef.child(Firebase_DataBase_Holder.notification_readed).child(pharmacy_id);
        notReadedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    int count =0;
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        Notifications_readed_Model readedModel = ds.getValue(Notifications_readed_Model.class);
                        if (readedModel.isValue().equals("false"))
                        {
                            count++;
                        }

                    }
                    DatabaseReference notCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(pharmacy_id);
                    notCountRef.child("Count").setValue(count);


                }
                else
                {
                    DatabaseReference notCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(pharmacy_id);
                    notCountRef.child("Count").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Show_All_userNotifications(ImageView user_notf)
    {
        user_notf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(mContext,User_Notifications.class));
            }
        });
    }
    private void CreateSharedPreferenc(String phone)
    {
        SharedPreferences spref = getActivity().getSharedPreferences("pref1",MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putString("phone",phone);
        editor.apply();

    }
    private void getAllPharmacyKeys(final String phone)
    {
        DatabaseReference NotRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info);
        NotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    List<String> pharmacyKeys_list = new ArrayList<String>();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        pharmacyKeys_list.add(ds.getKey().toString());
                    }
                    if (pharmacyKeys_list.size()>0)
                    {
                        if (Flag.isFlag_notification()==true)
                        {
                            SendNotification_toAll_Pharmacies(pharmacyKeys_list,phone);
                        }
                        else
                        {
                            Flag.setFlag_notification(false);
                        }
                    }


                }
                else
                {
                    Flag.setFlag_notification(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void SendNotification_toAll_Pharmacies(List<String> pharmacyKeys_list,String phone)
    {
        DatabaseReference NotRef     = dRef.child(Firebase_DataBase_Holder.notification);
        final DatabaseReference NotReadRef = dRef.child(Firebase_DataBase_Holder.notification_readed);
        for (final String s:pharmacyKeys_list)
        {
            String dateFormat  = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date().getTime());
            String text ="هذا الرقم"+"("+phone+")"+" يبحث عن هذا الدواء "+"("+Drugname+") "+"\n"+dateFormat;
            Notification_Model notification_model = new Notification_Model(text,PhoneNumber.getPhoneNumber(),Drugname);
            Toast.makeText(mContext,phone+"  "+Drugname, Toast.LENGTH_SHORT).show();
            NotRef.child(s).child(phone).child(Drugname).setValue(notification_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Flag.setFlag_notification(false);
                        Notifications_readed_Model readedModel = new Notifications_readed_Model("false",PhoneNumber.getPhoneNumber());
                        //NotReadRef.child(s).push().setValue(readedModel).addOnCompleteListener
                        NotReadRef.child(s).child(Drugname).setValue(readedModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    GetNotifications(s);

                                }
                            }
                        });
                    }
                }
            });



        }
        Toast.makeText(mContext, PhoneNumber.getPhoneNumber()+"", Toast.LENGTH_SHORT).show();
    }
    private void GetNotifications_Count()
    {
        if (!PhoneNumber.getPhoneNumber().isEmpty()&&!PhoneNumber.getPhoneNumber().toString().equals(null)){

           DatabaseReference notfReaded = dRef.child(Firebase_DataBase_Holder.notification_readed).child(PhoneNumber.getPhoneNumber().toString());
            notfReaded.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                        int count = 0;
                        for (DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            Notifications_readed_Model readedModel = ds.getValue(Notifications_readed_Model.class);
                            if (readedModel.isValue()=="false")
                            {
                                count++;
                            }
                        }

                        DatabaseReference notCount = dRef.child(Firebase_DataBase_Holder.notification_count).child(PhoneNumber.getPhoneNumber().toString()).child("Count");
                        notCount.setValue(count);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }
    }
    private void setNotificationCount()
    {
        if (!PhoneNumber.getPhoneNumber().isEmpty()&&!PhoneNumber.getPhoneNumber().toString().equals(null))
        {
            DatabaseReference notfCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(PhoneNumber.getPhoneNumber().toString()).child("Count");
            notfCountRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {
                        int count = 0;
                        count = dataSnapshot.getValue(Integer.class);
                        if (count==0)
                        {
                            notf_txt.setVisibility(View.GONE);

                        }
                        else if (count<=9&&count>0)
                        {
                            notf_txt.setVisibility(View.VISIBLE);
                            notf_txt.setText(String.valueOf(count));
                        }
                        else if (count>9)
                        {
                            notf_txt.setVisibility(View.VISIBLE);
                            notf_txt.setText("+9");
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    private void Search_aboutLocation_Drugs(final String query)
    {
        if (!query.equals(null) && !query.isEmpty()) {
            DatabaseReference drugRef = dRef.child(Firebase_DataBase_Holder.drugs_Info);
            drugRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null)

                    {
                        List<String> pharmacyKeys = new ArrayList<String>();
                        List<Drug_Model> Drag_List = new ArrayList<Drug_Model>();

                        if (Flag.isFlag()== true) {

                            for (DataSnapshot ds1 :dataSnapshot.getChildren())
                            {
                                for (DataSnapshot ds:ds1.getChildren())
                                {

                                    Drug_Model d_Model = ds.getValue(Drug_Model.class);
                                    String drug_name_concentrate = d_Model.getDrug_name().toString().toLowerCase()+" "+d_Model.getDrug_concentration().toString().toLowerCase();
                                    String drug_name_concentrate_type = d_Model.getDrug_name().toString().toLowerCase()+" "+d_Model.getDrug_concentration().toString().toLowerCase()+" "+d_Model.getDrug_type().toString().toLowerCase();

                                    if (d_Model.getDrug_name().toString().toLowerCase().equals(query.toString().toLowerCase())) {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);

                                    }
                                    else if (drug_name_concentrate.equals(query.toString().toLowerCase()))
                                    {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);
                                    }
                                    else if (drug_name_concentrate_type.equals(query.toString().toLowerCase()))
                                    {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);
                                    }
                                }
                            }
                            if (pharmacyKeys.size()>0)
                            {
                                Flag.setLocflag(true);
                                SetUp_Intent(Drag_List, pharmacyKeys);
                                search.setQuery("",false);
                                search.setIconified(true);




                            }

                            if (pharmacyKeys.size() == 0) {
                                mDialog.dismiss();


                                Flag.setFlag(false);
                                Flag.setLocflag(false);
                                Toast.makeText(mContext, getResources().getString(R.string.noDrugname), Toast.LENGTH_SHORT).show();
                                //fabBtn.setVisibility(View.VISIBLE);
                                m_AlertDialog.show();
                            }


                        }

                    } else {
                        mDialog.dismiss();
                        search.setQuery("",false);
                        search.setIconified(true);
                        Flag.setFlag(false);
                        Flag.setLocflag(false);
                        // fabBtn.setVisibility(View.VISIBLE);
                        m_AlertDialog.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Flag.setLocflag(false);
            search.setQuery("", false);
            search.setIconified(true);


        }


    }
    private void SetUp_Intent(List<Drug_Model> drug_model,List<String > pharmacyKeys)
    {
        mDialog.dismiss();
        Intent intent = new Intent(mContext,MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("drugmodel", (Serializable) drug_model);
        intent.putExtra("pharmacyKeys", (Serializable) pharmacyKeys);
        mContext.startActivity(intent);
    }
    @Override
    public void onStart()
    {
        super.onStart();

        if (mAuth.getCurrentUser()!=null)
        {
            mContext.startActivity(new Intent(mContext, Profile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();

        }
        if (PhoneNumber.getPhoneNumber().isEmpty())
        {
            phone_container.setVisibility(View.VISIBLE);
            note_tex.setVisibility(View.VISIBLE);
        }
        else
        {
            phone_container.setVisibility(View.GONE);
            note_tex.setVisibility(View.GONE);
        }
    }


}
