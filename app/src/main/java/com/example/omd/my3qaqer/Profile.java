package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity {
    private KenBurnsView pharmacy_image;
    private TextView pharmacy_name,pharmacy_location,pharmacy_phone,update_pharmacyimage,update_pharmacyinfo,not_txt;
    private FloatingActionsMenu fabBtn_menu;
    private FloatingActionButton fab2,fab3;
    private EditText updatepharmacy_name,updatepharmacy_location,updatepharmacy_phone;
    private Button update_pharmacyinfoBtn;
    private static final int RC1=1;
    private static final int RC2=2;
    private Uri image_uri;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private ProgressDialog update_mDialog;
    private AlertDialog update_alertDialog;
    private AlertDialog.Builder choose_alertDialog;
    private RelativeLayout profile_content_Container,profile_ProgressBar_Container;
    private ImageView not_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pharmacy_profile);

        init_View();
        LogOut(fab2);
        ShowAllDrugs(fab3);
        upload_Pharmacyimage(update_pharmacyimage);
        update_pharmacyinfoBtn_Action(update_pharmacyinfoBtn);
        UpdatePharmacy_Info(update_pharmacyinfo);
        Get_Pharmacy_informations(mAuth.getCurrentUser().getUid().toString());
        GetNotifications_Counts(mAuth.getCurrentUser().getUid().toString(),not_txt);
        ShowAllNotifications(not_Btn);

    }

    private void init_View()
    {
        pharmacy_image      = (KenBurnsView) findViewById(R.id.pharmacy_image);
        pharmacy_name       = (TextView) findViewById(R.id.pharmacy_name);
        pharmacy_location   = (TextView) findViewById(R.id.pharmacy_location);
        pharmacy_phone      = (TextView) findViewById(R.id.pharmacy_phone);
        update_pharmacyimage= (TextView) findViewById(R.id.update_pharmacyimage);
        update_pharmacyinfo = (TextView) findViewById(R.id.update_pharmacyinfo);
        //////////////////////////////////////////////////////
        fabBtn_menu = (FloatingActionsMenu) findViewById(R.id.fabBtn_menu);
        fab2 = new FloatingActionButton(this);
        fab2.setIcon(R.drawable.logout);
        fab2.setSize(FloatingActionButton.SIZE_MINI);
        fabBtn_menu.addButton(fab2);
        fab3 = new FloatingActionButton(this);
        fab3.setIcon(R.drawable.searchicon);
        fab3.setSize(FloatingActionButton.SIZE_MINI);
        fabBtn_menu.addButton(fab3);
        /////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef  = FirebaseDatabase.getInstance().getReference();
        ////////////////////////////////////////////////////

        update_alertDialog     = new AlertDialog.Builder(this).create();
        choose_alertDialog     = new AlertDialog.Builder(this);
        View view              = getLayoutInflater().inflate(R.layout.update_pharmacy_info,null);
        updatepharmacy_name    = (EditText) view.findViewById(R.id.pharmacy_name);
        updatepharmacy_location= (EditText) view.findViewById(R.id.pharmacy_location);
        updatepharmacy_phone   = (EditText) view.findViewById(R.id.pharmacy_phone);
        update_pharmacyinfoBtn = (Button)   view.findViewById(R.id.update_pharmacyinfoBtn);
        ///////////////////////////////////////////////////
        update_alertDialog.setView(view);
        update_mDialog = new ProgressDialog(Profile.this);
        update_mDialog.setMessage("جاري التعديل....");
        update_mDialog.setCanceledOnTouchOutside(false);
        ///////////////////////////////////////////////////
        profile_content_Container     = (RelativeLayout) findViewById(R.id.profile_content_Container);
        profile_ProgressBar_Container = (RelativeLayout) findViewById(R.id.profile_ProgressBar_Container);
        //////////////////////////////////////////////////
        not_txt = (TextView) findViewById(R.id.not_txt);
        not_Btn = (ImageView) findViewById(R.id.not_Btn);
        profile_ProgressBar_Container.setVisibility(View.VISIBLE);
        profile_content_Container.setVisibility(View.GONE);
        //////////////////////////////////////////////////////

    }
    private void LogOut(FloatingActionButton fab2)
    {
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                //startActivity(new Intent(Profile.this, Login_Register.class));
                startActivity(new Intent(Profile.this,MainActivity.class));
                finish();
            }
        });

    }
    private void ShowAllDrugs(FloatingActionButton fab3)
    {
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,all_drugs.class);
                startActivity(intent);

            }
        });

    }
    private void upload_Pharmacyimage(TextView update_pharmacyimage)
    {
        update_pharmacyimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog mAlertDialog1 = choose_alertDialog.create();
                final String [] items = {"التقاط صوره","تحميل صوره","الغاء"};
                View v = getLayoutInflater().inflate(R.layout.custom_alert_title,null);
                choose_alertDialog.setCustomTitle(v);
                choose_alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("التقاط صوره"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,RC1);
                        }
                        else if (items[i].equals("تحميل صوره"))
                        {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent,RC2);
                        }
                        else if (items[i].equals("الغاء"))
                        {
                            mAlertDialog1.dismiss();
                        }
                    }
                });
                choose_alertDialog.show();

            }
        });
    }
    private void update_pharmacyinfoBtn_Action(Button update_pharmacyinfoBtn)
    {
        update_pharmacyinfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pharmacy_name.getText().toString().equals(updatepharmacy_name.getText().toString())&&pharmacy_location.getText().toString().equals(updatepharmacy_location.getText().toString())&& pharmacy_phone.getText().toString().equals(updatepharmacy_phone.getText().toString()))
                {

                }
                else
                {
                    update_mDialog.show();
                    final DatabaseReference pharmacyinfoRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(mAuth.getCurrentUser().getUid().toString());
                    pharmacyinfoRef.child("pharmacy_name").setValue(updatepharmacy_name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                pharmacyinfoRef.child("pharmacy_location").setValue(updatepharmacy_location.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            pharmacyinfoRef.child("pharmacy_phone").setValue(updatepharmacy_phone.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        update_mDialog.dismiss();
                                                        Toast.makeText(Profile.this, "تم التعديل", Toast.LENGTH_SHORT).show();
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
    private void UpdatePharmacy_Info(TextView update_pharmacyinfo)
    {
        update_pharmacyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatepharmacy_name.setText(pharmacy_name.getText().toString());
                updatepharmacy_location.setText(pharmacy_location.getText().toString());
                updatepharmacy_phone.setText(pharmacy_phone.getText().toString());
                update_alertDialog.show();
            }
        });
    }
    private void Get_Pharmacy_informations(String pharmacy_id)
    {
        DatabaseReference pharmacyinfoRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(pharmacy_id);
        pharmacyinfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    Pharmacy_Model pharmacy_model = dataSnapshot.getValue(Pharmacy_Model.class);
                    pharmacy_name.setText(pharmacy_model.getPharmacy_name());
                    pharmacy_location.setText(pharmacy_model.getPharmacy_location());
                    pharmacy_phone.setText(pharmacy_model.getPharmacy_phone());
                    if (pharmacy_model.getPharmacy_image().equals(""))
                    {
                        pharmacy_image.setImageResource(R.drawable.background);
                        profile_content_Container.setVisibility(View.VISIBLE);
                        profile_ProgressBar_Container.setVisibility(View.GONE);

                    }
                    else
                    {
                        Picasso.with(Profile.this).load(pharmacy_model.getPharmacy_image()).into(pharmacy_image);
                        profile_content_Container.setVisibility(View.VISIBLE);
                        profile_ProgressBar_Container.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void GetNotifications_Counts(String pharmacy_id, final TextView not_txt)
    {
        DatabaseReference NotCountRef = dRef.child(Firebase_DataBase_Holder.notification_count).child(pharmacy_id).child("Count");
        NotCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    int count = dataSnapshot.getValue(Integer.class);
                    if (count==0)
                    {
                        not_txt.setVisibility(View.GONE);
                    }
                    else if (count >0&&count<=9)
                    {
                        not_txt.setVisibility(View.VISIBLE);
                        not_txt.setText(String.valueOf(count));
                    }
                    else if (count>9)
                    {
                        not_txt.setVisibility(View.VISIBLE);
                        not_txt.setText("+9");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void ShowAllNotifications(ImageView not_btn)
    {
        not_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Profile.this,Pharmacy_Notifications.class));
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data !=null)
        {
            if (resultCode==RESULT_OK)
            {

                if (requestCode == RC1)
                {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    image_uri = getImageUri(Profile.this,bitmap);
                    DatabaseReference PhrmacyInfoRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(mAuth.getCurrentUser().getUid().toString()).child("pharmacy_image");
                    PhrmacyInfoRef.setValue(image_uri.toString());
                }
                else if (requestCode == RC2)
                {
                    image_uri = data.getData();
                    DatabaseReference PhrmacyInfoRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info).child(mAuth.getCurrentUser().getUid().toString()).child("pharmacy_image");
                    PhrmacyInfoRef.setValue(image_uri.toString());

                }
            }

        }
        else
        {
        }
    }
    private Uri getImageUri(Profile context, Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"title",null);
        return Uri.parse(path);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Flag.setFlag(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fabBtn_menu.collapse();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }


}