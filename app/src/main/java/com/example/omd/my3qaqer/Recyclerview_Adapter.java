package com.example.omd.my3qaqer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Delta on 21/06/2017.
 */

public class Recyclerview_Adapter extends RecyclerView.Adapter<Recyclerview_Adapter.ViewHolder>{


    List<Drug_Model> DrugList;
    LayoutInflater inflater,inflater2,inflater3;
    Context mContext;
    FirebaseAuth mAuth;
    DatabaseReference dref;
    AlertDialog.Builder mAlertDialog;
    AlertDialog mAlertDialog_update;
    Spinner spinner;
    EditText Drug_name,Drug_concentrate;
    Button updateDrugBtn;
    private static String id,drugimageuri,drugname,drugconcentrate,drugtype;

    public Recyclerview_Adapter(List<Drug_Model> drugList, Context mContext)
    {
        DrugList      = drugList;
        this.mContext = mContext;
        inflater      = LayoutInflater.from(mContext);
        mAuth         = FirebaseAuth.getInstance();
        dref          = FirebaseDatabase.getInstance().getReference();
        mAlertDialog  = new AlertDialog.Builder(mContext);
        mAlertDialog.setMessage("هل تريد حزف الدواء ؟");
        //////////////////////////////////////////////////////////////
        inflater2              = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v                 = inflater2.inflate(R.layout.update_drug,null);
        mAlertDialog_update    = new AlertDialog.Builder(mContext).create();
        Drug_name              = (EditText)  v.findViewById(R.id.Drug_name);
        Drug_concentrate       = (EditText)  v.findViewById(R.id.Drug_concentrate);
        spinner                = (Spinner)   v.findViewById(R.id.spinner);
        updateDrugBtn             = (Button)    v.findViewById(R.id.updateDrugBtn);
        spinner.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,mContext.getResources().getStringArray(R.array.spinner)));
        mAlertDialog_update.setView(v);



    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.drug_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final Drug_Model drug_model = DrugList.get(position);
        Picasso.with(mContext).load(drug_model.getDrug_image()).into(holder.drug_image);
        holder.drug_name.setText(drug_model.getDrug_name());
        holder.drug_concentrate.setText(drug_model.getDrug_concentration());
        holder.drugtype.setText(drug_model.getDrug_type());
        holder.delete_drug_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drug_Model model = DrugList.get(holder.getLayoutPosition());
                String id        = model.getDrug_name()+"_"+model.getDrug_concentration()+"_"+model.getDrug_type();
                Delete_Drug(mAuth.getCurrentUser().getUid().toString(),id);
            }
        });
        holder.drug_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drug_Model drug_model = DrugList.get(holder.getLayoutPosition());
                String image_uri = drug_model.getDrug_image().toString();
                Intent intent= new Intent(mContext,DisplayImage.class);
                intent.putExtra("image",image_uri);
                mContext.startActivity(intent);

            }
        });
        holder.update_drug_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Drug_Model drug_model = DrugList.get(holder.getLayoutPosition());
                id                    = drug_model.getDrug_name().toString()+"_"+drug_model.getDrug_concentration().toString()+"_"+drug_model.getDrug_type().toString();
                drugimageuri          = drug_model.getDrug_image().toString();
                drugname              = drug_model.getDrug_name().toString();
                drugconcentrate       = drug_model.Drug_concentration.toString();
                drugtype              = drug_model.getDrug_type().toString();

                Toast.makeText(mContext,id, Toast.LENGTH_SHORT).show();
                Drug_name.setText(drug_model.getDrug_name().toString());
                Drug_concentrate.setText(drug_model.getDrug_concentration().toString());

                if (drug_model.getDrug_type().toString().equals("حبوب"))
                {
                    spinner.setSelection(1);
                }
                else if (drug_model.getDrug_type().toString().equals("شراب"))
                {
                    spinner.setSelection(2);
                }
                else if (drug_model.getDrug_type().toString().equals("حقن"))
                {
                    spinner.setSelection(3);
                }

                mAlertDialog_update.show();
            }
        });
        updateDrugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Drug_name.getText().toString().equals(drugname)&&Drug_concentrate.getText().toString().equals(drugconcentrate)&&spinner.getSelectedItem().toString().equals(drugtype))
                {
                    Toast.makeText(mContext, "ليس هناك اي تعديلات", Toast.LENGTH_SHORT).show();
                }
                else if (Drug_name.getText().toString().isEmpty())
                {
                    Toast.makeText(mContext, "اسم الدواء لاينبغي ان يكون فارغ", Toast.LENGTH_SHORT).show();

                }

                else if (Drug_concentrate.getText().toString().isEmpty())
                {
                    Toast.makeText(mContext, "التركيز لاينبغي ان يكون فارغ", Toast.LENGTH_SHORT).show();

                }
                else if (spinner.getSelectedItem().toString().equals("النوع"))
                {
                    Toast.makeText(mContext, "من فضلك ادخل نوع الدواء", Toast.LENGTH_SHORT).show();

                }
                else
                    {
                        Drug_Model drugModel = new Drug_Model(drugimageuri,Drug_concentrate.getText().toString(),Drug_name.getText().toString(),spinner.getSelectedItem().toString(),mAuth.getCurrentUser().getUid().toString());
                        Delete_OldDrug(id,drugModel);

                    }

            }
        });
    }

    private void Delete_OldDrug(String id, final Drug_Model drug_model) {
        DatabaseReference drug_info = dref.child(Firebase_DataBase_Holder.drugs_Info).child(mAuth.getCurrentUser().getUid().toString()).child(id);
        drug_info.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    UpdateDrug_info(drug_model.getDrug_image());

                }
            }
        });
    }

    private void UpdateDrug_info(String drugimage_uri) {
        String newId = Drug_name.getText().toString()+"_"+Drug_concentrate.getText().toString()+"_"+spinner.getSelectedItem().toString();
        DatabaseReference drug_info = dref.child(Firebase_DataBase_Holder.drugs_Info).child(mAuth.getCurrentUser().getUid().toString()).child(newId);
        Drug_Model drugModel = new Drug_Model(drugimageuri,Drug_concentrate.getText().toString(),Drug_name.getText().toString(),spinner.getSelectedItem().toString(),mAuth.getCurrentUser().getUid().toString());
        drug_info.setValue(drugModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(mContext, "تم التعديل", Toast.LENGTH_SHORT).show();
                    mAlertDialog_update.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return DrugList.size();
    }
    private void Delete_Drug(final String pharmacy_id, final String drug_id)
    {
        mAlertDialog.setPositiveButton("حزف", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference DruginfoRef = dref.child(Firebase_DataBase_Holder.drugs_Info).child(pharmacy_id);
                DruginfoRef.child(drug_id).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.deleteDrug_txt), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mAlertDialog.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext,"الغاء", Toast.LENGTH_SHORT).show();
            }
        });
        mAlertDialog.create();
        mAlertDialog.show();

    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView delete_drug_image;
        ImageView drug_image;
        TextView drug_name,drug_concentrate,drugtype,update_drug_data;
        public ViewHolder(View itemView) {
            super(itemView);
            delete_drug_image = (ImageView) itemView.findViewById(R.id.delete_drug_image);
            drug_image        = (ImageView) itemView.findViewById(R.id.drug_image);
            drug_name         = (TextView)  itemView.findViewById(R.id.drug_name);
            drug_concentrate  = (TextView)  itemView.findViewById(R.id.drug_concentrate);
            drugtype          = (TextView)  itemView.findViewById(R.id.drug_type);
            update_drug_data  = (TextView)  itemView.findViewById(R.id.update_drug_data);
        }
    }

}
