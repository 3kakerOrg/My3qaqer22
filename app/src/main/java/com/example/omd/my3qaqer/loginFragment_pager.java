package com.example.omd.my3qaqer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Delta on 02/07/2017.
 */

public class loginFragment_pager extends Fragment {
    private boolean saved;
    private CheckBox remember_me_chechbox;
    private Context mContext;
    private EditText userPhone,userPassword;
    private Button login_Btn;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.loginfragment_pager,container,false);
        init_View(view);
        Remember_Me();
        loginBtn_Action(login_Btn);
        return view;
    }



    private void init_View(View view) {
        mContext             =  view.getContext();
        userPhone            = (EditText) view.findViewById(R.id.Login_userPhone);
        userPassword         = (EditText) view.findViewById(R.id.Login_userPassword);
        login_Btn            = (Button)   view.findViewById(R.id.Login_logInBtn);
        remember_me_chechbox = (CheckBox) view.findViewById(R.id.remember_me_chechbox);
        ////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        ///////////////////////////////////////////////////////////////////
        userPhone.setText(null);
        userPassword.setText(null);
        ///////////////////////////////////////////////////////////////////
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(getResources().getString(R.string.DialogLogin_text));
        mDialog.setCanceledOnTouchOutside(false);
        //////////////////////////////////////////////////////////////////
        spref = mContext.getSharedPreferences("pref",mContext.MODE_PRIVATE);
        editor = spref.edit();
        saved =spref.getBoolean("saved",false);
        if (saved == true)
        {
            userPhone.setText(spref.getString("userPhone","").toString());
            userPassword.setText(spref.getString("password","").toString());
            remember_me_chechbox.setChecked(true);
        }
        else
            {
                remember_me_chechbox.setChecked(false);
            }

        ///////////////////////////////////////////////////////////////////


    }
    private void loginBtn_Action(Button login_btn) {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login() {
        String userphone = userPhone.getText().toString();
        String userpassword = userPassword.getText().toString();
        String email = userphone+"@3qaqer.com";
        if (TextUtils.isEmpty(userphone))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkphone).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (!userphone.matches("^(010|011|012)[0-9]{8}$"))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();
        }

        else if (!userpassword.matches("[A-Za-z0-9]{6,}"))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkpassword2).toString(), Toast.LENGTH_SHORT).show();

        }

        else if (TextUtils.isEmpty(userpassword))
        {
            Toast.makeText(mContext,getResources().getString(R.string.checkpassword).toString(), Toast.LENGTH_SHORT).show();

        }
        else if (!TextUtils.isEmpty(email))
        {
            mDialog.show();
            mAuth.signInWithEmailAndPassword(email,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {

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
                    if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted."))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.usernot_register), Toast.LENGTH_SHORT).show();

                    }
                    else if (e.getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.checkinternet), Toast.LENGTH_SHORT).show();

                    }
                    else if (e.getMessage().equals("The password is invalid or the user does not have a password."))
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.passwordnot_correct), Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(mContext,getResources().getString(R.string.errorin_login), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    private void Remember_Me() {
        remember_me_chechbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remember_me_chechbox.isChecked()==true)
                {
                    editor.putBoolean("saved",true);
                    editor.putString("userPhone",userPhone.getText().toString());
                    editor.putString("password",userPassword.getText().toString());
                    editor.commit();

                }
                else
                    {

                        editor.clear();
                        editor.commit();


                    }
            }
        });
        /*if (remember_me_chechbox.isChecked())
        {
            Toast.makeText(mContext, "ffff", Toast.LENGTH_SHORT).show();
            editor.putBoolean("saved",true);
            editor.putString("userPhone",userPhone.getText().toString());
            editor.putString("password",userPassword.getText().toString());
            editor.commit();
        }
        else
        {
            editor.clear();
            editor.commit();
        }*/
    }

}
