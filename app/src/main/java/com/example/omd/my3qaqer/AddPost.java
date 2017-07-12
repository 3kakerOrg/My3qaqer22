package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.Bidi;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPost extends AppCompatActivity {
    private EditText  addpost_userName,addpost_userPhone,addpost_userAddress,addpost_drugName,addpost_drugConcentrate;
    private ImageView addpost_drugImage,addpost_upload_drugImage;
    private Spinner   addpost_drugType_spinner;
    private Button    postBtn;
    private Toolbar   mToolbar;
    private AlertDialog.Builder mBuilder;
    private Uri ImageUri;
    private LayoutInflater inflater;
    private static final int RC1 = 100;
    private static final int RC2 = 200;
    private DatabaseReference dRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init_View();
        SelectDrugImage(addpost_upload_drugImage);
        Add_Post(postBtn);
    }
    private void init_View()
    {

        mToolbar                = (Toolbar) findViewById(R.id.addpost_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dRef                    = FirebaseDatabase.getInstance().getReference();
        //////////////////////////////////////////////////////////////////////////////////
        addpost_userName        = (EditText)  findViewById(R.id.addpost_userName);
        addpost_userPhone       = (EditText)  findViewById(R.id.addpost_userPhone);
        addpost_userAddress     = (EditText)  findViewById(R.id.addpost_userAddress);
        addpost_drugName        = (EditText)  findViewById(R.id.addpost_drugName);
        addpost_drugConcentrate = (EditText)  findViewById(R.id.addpost_drugConcentrate);
        addpost_drugImage       = (ImageView) findViewById(R.id.addpost_drugImage);
        addpost_upload_drugImage= (ImageView) findViewById(R.id.addpost_upload_drugImage);
        postBtn                 = (Button)    findViewById(R.id.postBtn);
        addpost_drugType_spinner= (Spinner)   findViewById(R.id.addpost_drugType);
        ///////////////////////////////////////////////////////////////////////////////////
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner));
        addpost_drugType_spinner.setAdapter(adapter);
        ///////////////////////////////////////////////////////////////////////////////////
        mBuilder                = new AlertDialog.Builder(this);
        //////////////////////////////////////////////////////////////////////////////////
        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        String name                   = preferences.getString("name","");
        String address                = preferences.getString("address","");

        ///////////////////////////////////////////////////////////////////////////////////
        addpost_userPhone.setText(PhoneNumber.getPhoneNumber().toString());
        addpost_userPhone.setEnabled(false);
        addpost_userName.setText(name.toString());
        addpost_userAddress.setText(address.toString());
        addpost_userName.setText(name);
        addpost_userAddress.setText(address);






    }
    private void SelectDrugImage(ImageView addpost_upload_drugImage)
    {
    addpost_upload_drugImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final AlertDialog mAlertDialog = mBuilder.create();
            final String [] items         = {"التقاط صوره","اختيار صوره" ,"الغاء"};
            inflater                      = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view2                     = inflater .inflate(R.layout.custom_alert_title2,null);
            mBuilder.setCustomTitle(view2);

            mBuilder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals("التقاط صوره"))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,RC1);
                    }
                    else if (items[i].equals("اختيار صوره"))
                    {
                        Intent intent2  = new Intent(Intent.ACTION_GET_CONTENT);
                        intent2.setType("image/*");
                        startActivityForResult(intent2,RC2);
                    } else if (items[i].equals("الغاء"))

                    {

                        mAlertDialog.dismiss();
                    }



                }
            });
            mBuilder.show();
        }

    });

    }
    private void Add_Post(Button postBtn) {
    postBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String mDrugName        = addpost_drugName.getText().toString();
            String mDrugConcentrate = addpost_drugConcentrate.getText().toString();
            String mDrugType        = addpost_drugType_spinner.getSelectedItem().toString();
            final String mUserName        = addpost_userName.getText().toString();
            final String mUserPhone       = addpost_userPhone.getText().toString();
            final String mUserAddress     = addpost_userAddress.getText().toString();
            Bidi bidi_Drugname      = new Bidi(mDrugName,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            Bidi bidi_username      = new Bidi(mUserName,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            Bidi bidi_userAddress   = new Bidi(mUserAddress,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            if (TextUtils.isEmpty(mUserName))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkname), Toast.LENGTH_SHORT).show();

            }
            else if (bidi_username.getBaseLevel()==0)
            {
                Toast.makeText(AddPost.this,"يرجي كتابه الاسم باللغه العربيه", Toast.LENGTH_SHORT).show();

            }
            else if (!mUserName.matches("^\\w((\\s)?(\\w))*$"))
            {
                Toast.makeText(AddPost.this,"يرجي كتابه الاسم بشكل صحيح", Toast.LENGTH_SHORT).show();

            }

            else if (TextUtils.isEmpty(mUserPhone))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkphone), Toast.LENGTH_SHORT).show();

            }
            else if (!mUserPhone.matches("^(010|011|012)[0-9]{8}$"))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkphone2), Toast.LENGTH_SHORT).show();

            }
            else if (TextUtils.isEmpty(mUserAddress))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkaddress), Toast.LENGTH_SHORT).show();

            }
            else if (bidi_userAddress.getBaseLevel()==0)
            {
                Toast.makeText(AddPost.this,"يرجي كتابه العنوان باللغه العربيه", Toast.LENGTH_SHORT).show();

            }


            else if (TextUtils.isEmpty(mDrugName))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkDrugname), Toast.LENGTH_SHORT).show();

            }
            else if (bidi_Drugname.getBaseLevel()==1)
            {
                Toast.makeText(AddPost.this,"يرجي كتابه الدواء باللغه الانجليزيه", Toast.LENGTH_SHORT).show();

            }
            else if (TextUtils.isEmpty(mDrugConcentrate))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkDrugconcentrate), Toast.LENGTH_SHORT).show();

            }
            else if (mDrugType.equals("النوع"))
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkDrugtype), Toast.LENGTH_SHORT).show();

            }
            else if (ImageUri==null)
            {
                Toast.makeText(AddPost.this,getResources().getString(R.string.checkDrugimage), Toast.LENGTH_SHORT).show();
            }
            else
                {
                    if (PhoneNumber.getPhoneNumber()==null||PhoneNumber.getPhoneNumber().isEmpty())
                    {

                    }else {
                        String id = mDrugName+"_"+mDrugConcentrate+"_"+mDrugType;
                        String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date().getTime());
                        PostModel postModel = new PostModel(mUserPhone,mUserName,mUserAddress,ImageUri.toString(),mDrugName,mDrugConcentrate,mDrugType,dateFormat.toString());
                        DatabaseReference postsRef = dRef.child(Firebase_DataBase_Holder.posts).push().child(PhoneNumber.getPhoneNumber().toString()).child(id);
                        postsRef.setValue(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {

                                    Toast.makeText(AddPost.this, "Posted", Toast.LENGTH_SHORT).show();
                                    CreatSharedPref(mUserName,mUserAddress);
                                    addpost_drugName.setText(null);
                                    addpost_drugConcentrate.setText(null);
                                    addpost_drugType_spinner.setSelection(0);
                                    ImageUri=null;
                                    addpost_drugImage.setImageBitmap(null);
                                }
                            }
                        });

                }
                }

        }
    });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null)
        {
            if (requestCode==RC1&&resultCode==RESULT_OK)
            {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ImageUri = Convert_to_Uti(AddPost.this,bitmap);
                addpost_drugImage.setImageURI(ImageUri);
                addpost_drugImage.setVisibility(View.VISIBLE);
            }
            else if (requestCode==RC2&&resultCode==RESULT_OK)
            {
                ImageUri = data.getData();
                Picasso.with(AddPost.this).load(ImageUri).into(addpost_drugImage);
                addpost_drugImage.setVisibility(View.VISIBLE);
            }
        }
    }
    private void CreatSharedPref(String name,String address)
    {
        SharedPreferences preferences   = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name",name);
        editor.putString("address",address);
        editor.apply();
    }
    private Uri Convert_to_Uti(Context context,Bitmap image)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,os);
        String path =MediaStore.Images.Media.insertImage(context.getContentResolver(),image,"title",null);
        return Uri.parse(path);
    }
}
