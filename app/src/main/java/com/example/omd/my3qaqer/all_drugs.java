package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;

public class all_drugs extends AppCompatActivity {

    private FloatingActionButton fab_add,fab_deleteAll;
    private FloatingActionsMenu fab_menu;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private AlertDialog alertDialog;
    private EditText Drug_name,Drug_concentrate;
    private Spinner spinner;
    private ImageView upload_drugimageBtn,Drug_image;
    private Button addDrugBtn;
    private Uri image_uri;
    private ProgressDialog mDialog;
    private AlertDialog.Builder choose_alertDialog,mBuilder;
    private static final int RC1= 1;
    private static final int RC2=2;
    private RelativeLayout allDrugs_ProgressBar_Container;
    private TextView no_drug_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_drugs);
        init_View();
        Add_newDrug(fab_add);
        AddDrug(addDrugBtn);
        Upload_Drugimage(upload_drugimageBtn);
        GetDrug_Informations(mAuth.getCurrentUser().getUid().toString());
        Delete_All_Drug(fab_deleteAll);
        Search(mSearchView);
        allDrugs_ProgressBar_Container.setVisibility(View.VISIBLE);

    }
    private void Search(SearchView mSearchView)
    {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Find_Drug(newText);
                return true;
            }
        });
    }
    private void Find_Drug(final String query)
    {
        DatabaseReference DruginfoRef  = dRef.child(Firebase_DataBase_Holder.drugs_Info).child(mAuth.getCurrentUser().getUid().toString());
        DruginfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    List<Drug_Model> DrugList = new ArrayList<Drug_Model>();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if (ds.child("drug_name").getValue().toString().toLowerCase().startsWith(query.toLowerCase()))
                        {
                            Drug_Model drug_model = ds.getValue(Drug_Model.class);
                            DrugList.add(drug_model);
                        }

                    }
                    Recyclerview_Adapter adapter = new Recyclerview_Adapter(DrugList,all_drugs.this);
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void init_View()
    {
        mToolbar = (Toolbar) findViewById(R.id.all_drug_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        allDrugs_ProgressBar_Container = (RelativeLayout) findViewById(R.id.allDrugs_ProgressBar_Container);
        no_drug_txt                    = (TextView) findViewById(R.id.no_drug_txt);
        //////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef  = FirebaseDatabase.getInstance().getReference();
        /////////////////////////////////////////////////////
        mRecyclerView = (RecyclerView) findViewById(R.id.all_drug_Recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(all_drugs.this));
        mSearchView   = (SearchView) findViewById(R.id.all_drug_searchview);
        ////////////////////////////////////////////////////
        fab_menu      = (FloatingActionsMenu) findViewById(R.id.fab_menu_drug);
        fab_add       = new FloatingActionButton(all_drugs.this);
        fab_deleteAll = new FloatingActionButton(all_drugs.this);
        fab_add.setSize(FloatingActionButton.SIZE_MINI);
        fab_add.setIcon(R.drawable.add2);
        fab_menu.addButton(fab_add);
        fab_deleteAll.setSize(FloatingActionButton.SIZE_MINI);
        fab_deleteAll.setIcon(R.drawable.delete_all);
        fab_menu.addButton(fab_deleteAll);
        ///////////////////////////////////////////////////
        View v                 = getLayoutInflater().inflate(R.layout.add_drug,null);
        alertDialog            = new AlertDialog.Builder(this).create();
        Drug_name              = (EditText)  v.findViewById(R.id.Drug_name);
        Drug_concentrate       = (EditText)  v.findViewById(R.id.Drug_concentrate);
        spinner                = (Spinner)   v.findViewById(R.id.spinner);
        upload_drugimageBtn    = (ImageView) v.findViewById(R.id.upload_Drug_image);
        Drug_image             = (ImageView) v.findViewById(R.id.Drug_image);
        addDrugBtn             = (Button)    v.findViewById(R.id.addDrugBtn);
        spinner.setAdapter(new ArrayAdapter<String>(all_drugs.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner)));
        alertDialog.setView(v);
        Drug_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(all_drugs.this,DisplayImage.class).putExtra("image",image_uri.toString()));
            }
        });
        ////////////////////////////////////////////////////
        mDialog        = new ProgressDialog(all_drugs.this);
        mDialog.setMessage(getResources().getString(R.string.DialogaddDrug_text));
        mDialog.setCanceledOnTouchOutside(false);
        /////////////////////////////////////////////////////
        choose_alertDialog     = new AlertDialog.Builder(this);
        mBuilder               = new AlertDialog.Builder(this);
        mBuilder.setMessage("حزف جميع الادويه ؟");

    }
    private void Add_newDrug(FloatingActionButton fab1)
    {
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });
    }
    private void AddDrug(Button addDrugBtn)
    {
        addDrugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flag.setFlag(false);
                Add_Drug();
            }
        });
    }
    private void Add_Drug()
    {
        String drug_name        = Drug_name.getText().toString();
        String drug_concentrate = Drug_concentrate.getText().toString();
        String drug_type        = spinner.getSelectedItem().toString();
        Uri drug_image_uri      = image_uri;
        if (drug_image_uri == null)
        {
            Toast.makeText(this,getResources().getString(R.string.checkDrugimage), Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(drug_name))
        {
            Toast.makeText(this,getResources().getString(R.string.checkDrugname), Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(drug_concentrate))
        {
            Toast.makeText(this,getResources().getString(R.string.checkDrugconcentrate), Toast.LENGTH_SHORT).show();
        }
        else if (drug_type.equals("النوع"))
        {
            Toast.makeText(this,getResources().getString(R.string.checkDrugtype), Toast.LENGTH_SHORT).show();

        }
        else
        {
            mDialog.show();
            String id = Drug_name.getText().toString()+"_"+Drug_concentrate.getText().toString()+"_"+spinner.getSelectedItem().toString();
            Bidi bidi1 = new Bidi(Drug_name.getText().toString(),Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            if (bidi1.getBaseLevel()==1)
            {
                Toast.makeText(this, "يرجى كتابه الدواء بالغه الانجليزيه", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
            else {
                //DatabaseReference drugRef = dRef.child(Firebase_DataBase_Holder.drugs_Info).push();
                // DatabaseReference drugRef = dRef.child(Firebase_DataBase_Holder.drugs_Info).child(id);
                DatabaseReference drugRef = dRef.child(Firebase_DataBase_Holder.drugs_Info).child(mAuth.getCurrentUser().getUid().toString()).child(id);
                Drug_Model mModel = new Drug_Model(drug_image_uri.toString(), drug_concentrate, drug_name, drug_type, mAuth.getCurrentUser().getUid().toString());
                drugRef.setValue(mModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Flag.setFlag(false);
                            mDialog.dismiss();
                            Toast.makeText(all_drugs.this, getResources().getString(R.string.result_txt), Toast.LENGTH_SHORT).show();
                            Drug_name.setText(null);
                            spinner.setSelection(0);
                            Drug_concentrate.setText(null);
                            image_uri = null;
                            Drug_image.setImageBitmap(null);
                            Flag.setFlag(false);
                        }
                    }


                });
            }

        }

    }
    private void GetDrug_Informations(final String pharmacy_id)
    {
        DatabaseReference drugInfoRef = dRef.child(Firebase_DataBase_Holder.drugs_Info).child(pharmacy_id);
        drugInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null)
                {
                    List<Drug_Model> drug_info_List = new ArrayList<Drug_Model>();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        Drug_Model drug_model = ds.getValue(Drug_Model.class);
                        drug_info_List.add(drug_model);

                    }
                    if (drug_info_List.size() == 0)
                    {
                        mRecyclerView.setVisibility(View.GONE);
                        allDrugs_ProgressBar_Container.setVisibility(View.GONE);
                        no_drug_txt.setVisibility(View.VISIBLE);
                        fab_deleteAll.setVisibility(View.GONE);
                        mSearchView.setVisibility(View.GONE);
                    }
                    else if (drug_info_List.size()>0)
                    {
                        Recyclerview_Adapter adapter = new Recyclerview_Adapter(drug_info_List,all_drugs.this);
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        allDrugs_ProgressBar_Container.setVisibility(View.GONE);
                        mSearchView.setVisibility(View.VISIBLE);
                        no_drug_txt.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        fab_deleteAll.setVisibility(View.VISIBLE);


                    }

                }else
                    {
                        mRecyclerView.setVisibility(View.GONE);
                        allDrugs_ProgressBar_Container.setVisibility(View.GONE);
                        no_drug_txt.setVisibility(View.VISIBLE);
                        fab_deleteAll.setVisibility(View.GONE);
                        mSearchView.setVisibility(View.GONE);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Upload_Drugimage(ImageView upload_drugimageBtn)
    {
        upload_drugimageBtn.setOnClickListener(new View.OnClickListener() {
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
    private void Delete_All_Drug(FloatingActionButton fab_deleteAll)
    {
        fab_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBuilder.setPositiveButton("حزف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference DruginfoRef = dRef.child(Firebase_DataBase_Holder.drugs_Info).child(mAuth.getCurrentUser().getUid().toString());
                        DruginfoRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(all_drugs.this,getResources().getString(R.string.deleteallDrug_txt), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                mBuilder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(all_drugs.this,"الغاء", Toast.LENGTH_SHORT).show();
                    }
                });
                mBuilder.create();
                mBuilder.show();
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
                    image_uri = getImageUri(all_drugs.this,bitmap);
                    Picasso.with(all_drugs.this).load(image_uri).into(Drug_image);

                }
                else if (requestCode == RC2)
                {
                    image_uri = data.getData();
                    Picasso.with(all_drugs.this).load(image_uri).into(Drug_image);

                }

            }

        }
        else
        {
        }
    }
    private Uri getImageUri(all_drugs context, Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"title",null);
        return Uri.parse(path);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fab_menu.collapse();
    }
}
