package com.example.omd.my3qaqer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Result_Activity extends AppCompatActivity {
    private ListView mListView;
    private DatabaseReference dRef;
    private Toolbar mToolbar;
    private RelativeLayout Result_ProgressBar_Container;
    private SearchView result_searchView;
    private List<Drug_Model> drug_model_List;
    private List<String> pharmacyKeysList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_);
        init_View();
        Search(result_searchView);
        Result_ProgressBar_Container.setVisibility(View.VISIBLE);
        Intent intent =getIntent();
        drug_model_List = (List<Drug_Model>) intent.getSerializableExtra("drugmodel");
        pharmacyKeysList = (List<String>) intent.getSerializableExtra("pharmacyKeys");
        GetParmacy_Info(pharmacyKeysList,drug_model_List);
    }
    private void init_View()
    {
        mToolbar = (Toolbar) findViewById(R.id.mToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        result_searchView = (SearchView) findViewById(R.id.result_searchView);
        /////////////////////////////////////////////////////
        dRef                         = FirebaseDatabase.getInstance().getReference();
        mListView                    = (ListView) findViewById(R.id.DrugsInfo_list);
        Result_ProgressBar_Container = (RelativeLayout) findViewById(R.id.Result_ProgressBar_Container);
    }
    private void Search(SearchView result_searchView)
    {
        result_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                GetFilter(drug_model_List,pharmacyKeysList,newText);
                return true;
            }
        });
    }
    private void GetParmacy_Info(final List<String> phrmacy_keys, final List<Drug_Model> drugModelList)
    {
        DatabaseReference pharmacyRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info);
        pharmacyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    List<Pharmacy_Model> pmodel_list = new ArrayList<Pharmacy_Model>();
                    for (String s:phrmacy_keys)
                    {
                        Pharmacy_Model pmodl = dataSnapshot.child(s).getValue(Pharmacy_Model.class);
                        pmodel_list.add(pmodl);
                    }
                    Adapter adapter = new Adapter(Result_Activity.this,pmodel_list,drugModelList);
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Result_ProgressBar_Container.setVisibility(View.GONE);
                }
                else
                {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void GetFilter(final List<Drug_Model> drug_model_list, final List<String> pharmacyKeysList, final String newText)
    {
        DatabaseReference pharmacyRef = dRef.child(Firebase_DataBase_Holder.pharmacy_Info);
        pharmacyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    List<Pharmacy_Model> pmodel_list = new ArrayList<Pharmacy_Model>();
                    for (String s:pharmacyKeysList)
                    {
                        if (dataSnapshot.child(s).child("pharmacy_location").getValue().toString().startsWith(newText))
                        {
                            Pharmacy_Model pmodl = dataSnapshot.child(s).getValue(Pharmacy_Model.class);
                            pmodel_list.add(pmodl);
                        }

                    }
                    Adapter adapter = new Adapter(Result_Activity.this,pmodel_list,drug_model_list);
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Result_ProgressBar_Container.setVisibility(View.GONE);
                }
                else
                {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    class Adapter  extends BaseAdapter
    {
        Context mContext;
        LayoutInflater inflater;
        List<Pharmacy_Model> pharmacy_inf;
        List<Drug_Model> Drug_info;

        public Adapter(Context mContext, List<Pharmacy_Model> pharmacy_inf, List<Drug_Model> drug_info) {
            this.mContext     = mContext;
            this.pharmacy_inf = pharmacy_inf;
            Drug_info         = drug_info;
        }

        @Override
        public int getCount() {
            return pharmacy_inf.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup)
        {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listdrugs_row,viewGroup,false);
            ImageView      DrugImage       = (ImageView) view.findViewById(R.id.result_Drugimage);
            TextView       Drugname        = (TextView) view.findViewById(R.id.result_Drugname);
            TextView       Drugconcentrate = (TextView) view.findViewById(R.id.result_Drugconentrate);
            TextView       Drugtype        = (TextView) view.findViewById(R.id.result_Drugtype);
            TextView       pharmacyname    = (TextView) view.findViewById(R.id.result_pharmacyname);
            TextView       pharmacyphone   = (TextView) view.findViewById(R.id.result_pharmacyphone);
            TextView       pharmacylocation= (TextView) view.findViewById(R.id.result_pharmacylocation);
            Pharmacy_Model pmodel          = pharmacy_inf.get(i);
            Drug_Model drug_model = Drug_info.get(i);
            Picasso.with(mContext).load(drug_model.getDrug_image()).into(DrugImage);
            Drugname.setText(drug_model.getDrug_name().toString());
            Drugconcentrate.setText(drug_model.getDrug_concentration().toString());
            Drugtype.setText(drug_model.getDrug_type().toString());
            pharmacyname.setText(pmodel.getPharmacy_name().toString());
            pharmacyphone.setText(pmodel.getPharmacy_phone());
            pharmacylocation.setText(pmodel.getPharmacy_location().toString());

            DrugImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drug_Model drug_model = Drug_info.get(i);
                    Intent intent= new Intent(Result_Activity.this,DisplayImage.class);
                    intent.putExtra("image",drug_model.getDrug_image().toString());
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
