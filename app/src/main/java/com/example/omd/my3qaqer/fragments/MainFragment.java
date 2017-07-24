package com.example.omd.my3qaqer.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.omd.my3qaqer.Drug_Model;
import com.example.omd.my3qaqer.Firebase_DataBase_Holder;
import com.example.omd.my3qaqer.Flag;
import com.example.omd.my3qaqer.R;
import com.example.omd.my3qaqer.R2;
import com.example.omd.my3qaqer.Result_Activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static butterknife.ButterKnife.bind;


public class MainFragment extends Fragment {
    private ProgressDialog mDialog;
    @BindView(R2.id.loginBtnId)
    Button loginBtn;
    @BindView(R2.id.registerBtnId)
    Button registerBtn;
    @BindView(R2.id.search)
    SearchView search;
    private DatabaseReference dRef;
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        dRef = FirebaseDatabase.getInstance().getReference();
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_Action(search);
            }
        });
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("جاري البحث انتظر قليلا...");
        mDialog.setCanceledOnTouchOutside(false);

        return view;
    }

    @OnClick(R2.id.loginBtnId)
    void gotoLoginFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new login_Fragment()).commit();
    }

    @OnClick(R2.id.registerBtnId)
    void gotoRegisterFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Register_Fragment()).commit();
    }


    private void search_Action(final SearchView search) {

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Flag.setFlag(true);
                Flag.setLocflag(true);
                mDialog.show();
                Bidi bidi = new Bidi(query, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
                if (bidi.getBaseLevel() == 1) {
                    Toast.makeText(getActivity(), "يرجى كتابه الدواء بالغه الانجليزيه", Toast.LENGTH_SHORT).show();
                    Flag.setFlag(false);
                    Flag.setLocflag(false);
                    mDialog.dismiss();
                    search.setQuery("", false);
                } else {
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

    private void Search_aboutLocation_Drugs(final String query) {
        if (!query.equals(null) && !query.isEmpty()) {
            DatabaseReference drugRef = dRef.child(Firebase_DataBase_Holder.drugs_Info);
            drugRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null)

                    {
                        List<String> pharmacyKeys = new ArrayList<String>();
                        List<Drug_Model> Drag_List = new ArrayList<Drug_Model>();

                        if (Flag.isFlag() == true) {

                            for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                for (DataSnapshot ds : ds1.getChildren()) {

                                    Drug_Model d_Model = ds.getValue(Drug_Model.class);
                                    String drug_name_concentrate = d_Model.getDrug_name().toString().toLowerCase() + " " + d_Model.getDrug_concentration().toString().toLowerCase();
                                    String drug_name_concentrate_type = d_Model.getDrug_name().toString().toLowerCase() + " " + d_Model.getDrug_concentration().toString().toLowerCase() + " " + d_Model.getDrug_type().toString().toLowerCase();

                                    if (d_Model.getDrug_name().toString().toLowerCase().equals(query.toString().toLowerCase())) {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);

                                    } else if (drug_name_concentrate.equals(query.toString().toLowerCase())) {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);
                                    } else if (drug_name_concentrate_type.equals(query.toString().toLowerCase())) {
                                        pharmacyKeys.add(d_Model.getDrug_pharmacyid());
                                        Drag_List.add(d_Model);
                                    }
                                }
                            }
                            if (pharmacyKeys.size() > 0) {
                                Flag.setLocflag(true);
                                SetUp_Intent(Drag_List, pharmacyKeys);
                                search.setQuery("", false);
                                search.setIconified(true);


                            }

                            if (pharmacyKeys.size() == 0) {
                                mDialog.dismiss();


                                Flag.setFlag(false);
                                Flag.setLocflag(false);
                                Toast.makeText(getActivity(), getResources().getString(R.string.noDrugname), Toast.LENGTH_SHORT).show();
                                //fabBtn.setVisibility(View.VISIBLE);
                            }


                        }

                    } else {
                        mDialog.dismiss();
                        search.setQuery("", false);
                        search.setIconified(true);
                        Flag.setFlag(false);
                        Flag.setLocflag(false);
                        // fabBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Flag.setLocflag(false);
            search.setQuery("", false);
            search.setIconified(true);


        }


    }

    private void SetUp_Intent(List<Drug_Model> drug_model, List<String> pharmacyKeys) {
        mDialog.dismiss();
        Intent intent = new Intent(getActivity(), Result_Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("drugmodel", (Serializable) drug_model);
        intent.putExtra("pharmacyKeys", (Serializable) pharmacyKeys);
        getActivity().startActivity(intent);
    }


}
