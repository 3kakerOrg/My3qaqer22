package com.example.omd.my3qaqer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omd.my3qaqer.Firebase_DataBase_Holder;
import com.example.omd.my3qaqer.Flag;
import com.example.omd.my3qaqer.GpsLoc;
import com.example.omd.my3qaqer.Location_Model;
import com.example.omd.my3qaqer.Pharmacy_Model;
import com.example.omd.my3qaqer.Profile;
import com.example.omd.my3qaqer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Delta on 16/06/2017.
 */

public class Register_Fragment extends Fragment{
    private EditText userName,userPhone,userPassword,userLicence,userLocation;
    private Button registerBtn;
    Context mContext;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    ProgressDialog mDialog;
    private Double latitude,longitude;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register,container,false);
        init_View(view);
        registerBtnAction(registerBtn);
        return view;
    }




    private void init_View(View view) {
        mContext     =  view.getContext();
        userName     = (EditText) view.findViewById(R.id.userName);
        userPhone    = (EditText) view.findViewById(R.id.userPhone);
        userPassword = (EditText) view.findViewById(R.id.userPassword);
        userLicence  = (EditText) view.findViewById(R.id.userLicence);
        userLicence  = (EditText) view.findViewById(R.id.userLicence);
        userLocation = (EditText) view.findViewById(R.id.userLocation);
        registerBtn  = (Button) view.findViewById(R.id.Regiser_registerBtn);
        ///////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef  = FirebaseDatabase.getInstance().getReference();
        ///////////////////////////////////////////////////////////////////
        userName.setText(null);
        userPhone.setText(null);
        userPassword.setText(null);
        userLicence.setText(null);
        userLocation.setText(null);
        ///////////////////////////////////////////////////////////////////
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(getResources().getString(R.string.DialogRegister_text));
        mDialog.setCanceledOnTouchOutside(false);
        //////////////////////////////////////////////////////////////////

    }
    private void registerBtnAction(Button registerBtn) {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }
    private void SignUp() {

        final String username     = userName.getText().toString();
        final String phone        = userPhone.getText().toString();
        final String licence      = userLicence.getText().toString();
        final String userlocation = userLocation.getText().toString();
        String email        = phone+"@3qaqer.com";
        String password     = userPassword.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkname).toString(), Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkphone).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (!phone.matches("^(010|011|012)[0-9]{8}$"))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkpassword).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (!password.matches("[A-Za-z0-9]{6,}"))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkpassword2).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(licence))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checklicence).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(userlocation))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checklocation).toString(), Toast.LENGTH_SHORT).show();

        }

        else if (!TextUtils.isEmpty(email))
        {
            mDialog.show();
            GpsLoc mLoc = new GpsLoc(mContext);
            if (mLoc.canGetLocation())
            {
                latitude = mLoc.getLatitude();
                longitude= mLoc.getLongitude();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            Pharmacy_Model mModel = new Pharmacy_Model(username,phone,licence,userlocation,"");
                            DatabaseReference pharmacyRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(mAuth.getCurrentUser().getUid().toString());
                            pharmacyRef.setValue(mModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        DatabaseReference LocRef = dRef.child(Firebase_DataBase_Holder.location_Info).child(mAuth.getCurrentUser().getUid().toString());
                                        Location_Model l_Model = new Location_Model(latitude.toString(),longitude.toString());
                                        LocRef.setValue(l_Model);
                                        getActivity().startActivity(new Intent(getActivity(), Profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        Toast.makeText(mContext,getResources().getString(R.string.registerCorrect).toString(), Toast.LENGTH_SHORT).show();

                                        mDialog.dismiss();
                                        userName.setText(null);
                                        userPhone.setText(null);
                                        userPassword.setText(null);
                                        userLicence.setText(null);
                                        userLocation.setText(null);
                                    }
                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mDialog.dismiss();
                        if (e.getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                        {
                            Toast.makeText(mContext,getResources().getString(R.string.checkinternet), Toast.LENGTH_SHORT).show();

                        }
                        else if (e.getMessage().equals("The email address is already in use by another account."))
                        {
                            Toast.makeText(mContext,getResources().getString(R.string.userregister_befor), Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(mContext,getResources().getString(R.string.errorin_registration), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
            else
            {
                mLoc.ShowAlertDialog(getActivity());
                mDialog.dismiss();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Flag.setFlag(false);
    }
}
