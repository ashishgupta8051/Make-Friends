package com.social.makefriends.ui.activity.userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.utils.Connection;

public class ViewProfileImage extends AppCompatActivity {
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private String Value;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_image);

        Connection.checkInternet(ViewProfileImage.this,this);

        getSupportActionBar().setTitle("Profile Pic");

        firebaseAuth = FirebaseAuth.getInstance();
        Value = getIntent().getExtras().get("Value").toString();

        imageView = (ImageView)findViewById(R.id.ProfileImage);
        progressBar = findViewById(R.id.viewProfileImageProgress);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String ProfileImage = snapshot.child("userProfileImageUrl").getValue().toString();
                    if (ProfileImage.equals("None")){
                       imageView.setImageResource(R.drawable.profile_image);
                       progressBar.setVisibility(View.GONE);
                    }else {
                        Glide.with(getApplicationContext()).load(ProfileImage).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @SuppressLint("CheckResult")
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfileImage.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
        intent.putExtra("UserFriendsValue",Value);
        startActivity(intent);
        finish();
    }
}