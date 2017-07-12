package com.example.omd.my3qaqer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DisplayImage extends AppCompatActivity {

    private ImageView display_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        display_image = (ImageView) findViewById(R.id.display_image);
        Intent intent = getIntent();
        if (intent != null)
        {
            String image_uri = intent.getStringExtra("image");
            if (!image_uri.isEmpty())
            {
                Picasso.with(this).load(image_uri).into(display_image);
            }
        }
    }


}
