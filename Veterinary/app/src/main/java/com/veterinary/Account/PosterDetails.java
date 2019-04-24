package com.veterinary.Account;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.veterinary.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PosterDetails extends AppCompatActivity {
   private String mimage,mfname,mlname,mphonenumber;
   private  TextView mPosterDetails;
   private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_details);
        mPosterDetails=(TextView)findViewById(R.id.details);
        imageView=(ImageView)findViewById(R.id.poster_image);


           mfname =getIntent().getExtras().getString("fname");
           mlname=getIntent().getExtras().getString("lname");
           mphonenumber=getIntent().getExtras().getString("phonenumber");
           mimage=getIntent().getExtras().getString("imageUrll");

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.fitCenter();
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(mimage).into(imageView);



           mPosterDetails.setText("Full Name:"+mlname+" "+mlname+"\nPhone Number:"+mphonenumber);


    }
}
