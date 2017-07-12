package com.example.omd.my3qaqer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class All_Posts extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RelativeLayout post_progress_bar_container;
    private ImageView post_back;
    private DatabaseReference dRef;
    private TextView nopost_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__posts);
        init_View();
        GetAll_Posts();
    }



    private void init_View() {
        mToolbar = (Toolbar) findViewById(R.id.posts_toolBar);
        setSupportActionBar(mToolbar);
        dRef                        = FirebaseDatabase.getInstance().getReference();
        //////////////////////////////////////////////////////
        mRecyclerView               = (RecyclerView) findViewById(R.id.posts_RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        post_progress_bar_container = (RelativeLayout) findViewById(R.id.post_progress_bar_container);
        post_progress_bar_container.setVisibility(View.VISIBLE);
        post_back                   = (ImageView) findViewById(R.id.post_back);
        nopost_txt                  = (TextView) findViewById(R.id.nopost_txt);
        //////////////////////////////////////////////////////
        post_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(All_Posts.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }
    private void GetAll_Posts() {


            DatabaseReference postsRef = dRef.child(Firebase_DataBase_Holder.posts);
            postsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        List<PostModel> postsList = new ArrayList<PostModel>();
                        List<PostModel> postsList_Inverse = new ArrayList<PostModel>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            for (DataSnapshot ds2:ds.getChildren()) {
                                for (DataSnapshot ds3:ds2.getChildren())
                                {
                                    PostModel postModel = ds3.getValue(PostModel.class);
                                    postsList.add(postModel);
                                }
                            }
                        }
                        if (postsList.size() == 0) {
                            mRecyclerView.setVisibility(View.GONE);
                            post_progress_bar_container.setVisibility(View.GONE);
                            nopost_txt.setVisibility(View.VISIBLE);
                        } else if (postsList.size() > 0) {
                            for (int index =postsList.size()-1;index>=0;index--)
                            {
                                postsList_Inverse.add(postsList.get(index));
                            }
                            Post_Adapter adapter = new Post_Adapter(postsList_Inverse, All_Posts.this);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            post_progress_bar_container.setVisibility(View.GONE);
                        }
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        post_progress_bar_container.setVisibility(View.GONE);
                        nopost_txt.setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addPost:
               startActivity(new Intent(All_Posts.this,AddPost.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return true;
    }
}
