package com.example.omd.my3qaqer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Delta on 07/07/2017.
 */

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.ViewHolder> {
    List<PostModel> post_List;
    Context mContext;
    LayoutInflater inflater;
    DatabaseReference dRef;
    ProgressDialog mDialog;

    public Post_Adapter(List<PostModel> post_List, Context mContext) {
        this.post_List = post_List;
        this.mContext  = mContext;
        inflater       = LayoutInflater.from(mContext);
        dRef           = FirebaseDatabase.getInstance().getReference();
        mDialog        = new ProgressDialog(mContext);
        mDialog.setMessage("Deleting.....");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view         = inflater.inflate(R.layout.posts_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PostModel postModel = post_List.get(position);
        holder.post_date.setText(postModel.getDate().toString());
        holder.post_userName.setText(postModel.getUserName().toString());
        holder.post_userPhone.setText(postModel.getUserPhone().toString());
        holder.post_userAddress.setText(postModel.getUserAddress().toString());
        holder.post_drugName.setText(postModel.getDrugName().toString());
        holder.post_drugconcentrate.setText(postModel.getDrugConcentrate().toString());
        holder.post_drugType.setText(postModel.getDrugType().toString());
        Picasso.with(mContext).load(Uri.parse(postModel.getDrugImage().toString())).into(holder.post_drugImage);
        holder.popmenu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu  = new PopupMenu(mContext,view);
                popupMenu.getMenuInflater().inflate(R.menu.popmenu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.deletePost)
                        {
                            mDialog.show();
                            Flag.setDelete_post(true);
                            PostModel postModel2 = post_List.get(holder.getLayoutPosition());
                            String post_id =postModel2.getDrugName()+"_"+postModel2.getDrugConcentrate()+"_"+postModel2.getDrugType();
                            DeletePost(post_id);
                        }
                        else if (item.getItemId()==R.id.deleteAllPost)
                        {
                            mDialog.show();
                            Flag.setDelete_allposts(true);
                            Delete_AllPosts(PhoneNumber.getPhoneNumber().toString());

                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        if (postModel.getUserPhone().equals(PhoneNumber.getPhoneNumber().toString()))
        {
            holder.callBtn.setVisibility(View.GONE);

        }
        else
            {
                holder.popmenu_icon.setVisibility(View.GONE);
                holder.callBtn.setVisibility(View.VISIBLE);

            }

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostModel postModel = post_List.get(holder.getLayoutPosition());
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+postModel.getUserPhone().toString()));
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return post_List.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView post_date,post_userName,post_userPhone,post_userAddress,post_drugName,post_drugconcentrate,post_drugType;
        ImageView post_drugImage,popmenu_icon;
        Button callBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            post_date            = (TextView)  itemView.findViewById(R.id.post_Date);
            post_userName        = (TextView)  itemView.findViewById(R.id.post_userName);
            post_userPhone       = (TextView)  itemView.findViewById(R.id.post_userPhone);
            post_userAddress     = (TextView)  itemView.findViewById(R.id.post_userAddress);
            post_drugName        = (TextView)  itemView.findViewById(R.id.post_drugName);
            post_drugconcentrate = (TextView)  itemView.findViewById(R.id.post_drugConcentrate);
            post_drugType        = (TextView)  itemView.findViewById(R.id.post_drugType);
            post_drugImage       = (ImageView) itemView.findViewById(R.id.post_drugImage);
            popmenu_icon         = (ImageView) itemView.findViewById(R.id.post_Drop_menu);
            callBtn              = (Button)    itemView.findViewById(R.id.post_callBtn);
        }
    }
    private void DeletePost(final String post_id)
    {
        DatabaseReference postsRef = dRef.child(Firebase_DataBase_Holder.posts);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if (Flag.isDelete_post()==true)
                        {
                            ds.child(PhoneNumber.getPhoneNumber().toString()).child(post_id).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        mDialog.dismiss();
                                        Flag.setDelete_post(false);
                                    }
                                }
                            });
                        }
                        else
                            {
                                Flag.setDelete_post(false);
                            }

                    }
                }
                else
                    {
                        Flag.setDelete_post(false);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Delete_AllPosts(final String userPhone)
    {
        DatabaseReference postsRef = dRef.child(Firebase_DataBase_Holder.posts);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if (Flag.isDelete_allposts()==true) {
                            ds.child(userPhone).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();
                                        Flag.setDelete_allposts(false);
                                    }
                                }
                            });
                        }
                        else
                            {
                                Flag.setDelete_allposts(false);
                            }

                    }
                }
                else
                    {
                        Flag.setDelete_allposts(false);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
