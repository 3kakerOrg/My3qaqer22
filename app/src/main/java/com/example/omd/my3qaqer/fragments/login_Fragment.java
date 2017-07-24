package com.example.omd.my3qaqer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.omd.my3qaqer.Flag;
import com.example.omd.my3qaqer.Profile;
import com.example.omd.my3qaqer.R;
import com.example.omd.my3qaqer.R2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Delta on 16/06/2017.
 */

public class login_Fragment extends android.support.v4.app.Fragment {
    Context mContext;
    @BindView(R2.id.Login_userPhone)
    EditText userPhone;
    @BindView(R2.id.Login_userPassword)
    EditText userPassword;
    @BindView(R2.id.Login_logInBtn)
    Button login_Btn;


    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference dRef;
    ProgressDialog mDialog;
//
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        ButterKnife.bind(this, view);
        init_View(view);
        return view;
    }


    private void init_View(View view) {
        mContext = view.getContext();
        ////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        ///////////////////////////////////////////////////////////////////
        userPhone.setText(null);
        userPassword.setText(null);
        ///////////////////////////////////////////////////////////////////
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(getResources().getString(R.string.DialogLogin_text));
        mDialog.setCanceledOnTouchOutside(false);
    }

    @OnClick(R2.id.Login_logInBtn)
     void Login() {
        String userphone = userPhone.getText().toString();
        String userpassword = userPassword.getText().toString();
        String email = userphone + "@3qaqer.com";
        if (TextUtils.isEmpty(userphone)) {
            Toast.makeText(mContext, getResources().getString(R.string.checkphone).toString(), Toast.LENGTH_SHORT).show();

        } else if (!userphone.matches("^(010|011|012)[0-9]{8}$")) {
            Toast.makeText(mContext, getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();
        } else if (!userpassword.matches("[A-Za-z0-9]{6,}")) {
            Toast.makeText(mContext, getResources().getString(R.string.checkpassword2).toString(), Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(userpassword)) {
            Toast.makeText(mContext, getResources().getString(R.string.checkpassword).toString(), Toast.LENGTH_SHORT).show();

        } else if (!TextUtils.isEmpty(email)) {
            mDialog.show();
            mAuth.signInWithEmailAndPassword(email, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        getActivity().startActivity(new Intent(getActivity(), Profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        userPhone.setText(null);
                        userPassword.setText(null);
                        mDialog.dismiss();
                        Toast.makeText(mContext, "تم الدخول بنجاح", Toast.LENGTH_SHORT).show();
                        Flag.setFlag(false);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                        Toast.makeText(mContext, getResources().getString(R.string.usernot_register), Toast.LENGTH_SHORT).show();

                    } else if (e.getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                        Toast.makeText(mContext, getResources().getString(R.string.checkinternet), Toast.LENGTH_SHORT).show();

                    } else if (e.getMessage().equals("The password is invalid or the user does not have a password.")) {
                        Toast.makeText(mContext, getResources().getString(R.string.passwordnot_correct), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.errorin_login), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
