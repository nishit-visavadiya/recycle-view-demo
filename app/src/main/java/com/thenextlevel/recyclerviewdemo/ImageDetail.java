package com.thenextlevel.recyclerviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ImageDetail extends AppCompatActivity {

    TextView textViewName;
    ImageView imageViewUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        textViewName = findViewById(R.id.text_view_iamge_name);
        imageViewUpload = findViewById(R.id.image_upload);

        String imageTitle = getIntent().getStringExtra("image title");
        String imageUrl = getIntent().getStringExtra("image url");

        textViewName.setText(imageTitle);
        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .centerCrop()
                .into(imageViewUpload);
    }
}