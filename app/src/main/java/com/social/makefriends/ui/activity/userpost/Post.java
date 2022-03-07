package com.social.makefriends.ui.activity.userpost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.social.makefriends.R;
import com.social.makefriends.databinding.ActivityPostBinding;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.ui.activity.notification.Notifications;
import com.social.makefriends.ui.activity.userprofile.UserProfile;
import com.social.makefriends.ui.activity.chat.Chats;
import com.social.makefriends.ui.fragment.UploadPhoto;
import com.social.makefriends.ui.fragment.UploadStatus;
import com.social.makefriends.ui.fragment.UploadVideo;
import com.social.makefriends.utils.Connection;

public class Post extends AppCompatActivity  {
    private ActivityPostBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Connection.checkInternet(Post.this,this);

        getSupportActionBar().hide();

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UploadPhoto()).commit();

        binding.uploadPhoto.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UploadPhoto()).commit();
            binding.uploadPhoto.setBackgroundResource(R.drawable.adddrwable);
            binding.uploadPhotoTxt.setTextColor(Color.WHITE);
            binding.uploadVideo.setBackgroundResource(R.drawable.borderline);
            binding.uploadVideoTxt.setTextColor(Color.BLACK);
            binding.uploadStatus.setBackgroundResource(R.drawable.borderline);
            binding.uploadStatusTxt.setTextColor(Color.BLACK);

        });

        binding.uploadVideo.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UploadVideo()).commit();
            binding.uploadPhoto.setBackgroundResource(R.drawable.borderline);
            binding.uploadPhotoTxt.setTextColor(Color.BLACK);
            binding.uploadVideo.setBackgroundResource(R.drawable.adddrwable);
            binding.uploadVideoTxt.setTextColor(Color.WHITE);
            binding.uploadStatus.setBackgroundResource(R.drawable.borderline);
            binding.uploadStatusTxt.setTextColor(Color.BLACK);
        });

        binding.uploadStatus.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UploadStatus()).commit();
            binding.uploadPhoto.setBackgroundResource(R.drawable.borderline);
            binding.uploadPhotoTxt.setTextColor(Color.BLACK);
            binding.uploadVideo.setBackgroundResource(R.drawable.borderline);
            binding.uploadVideoTxt.setTextColor(Color.BLACK);
            binding.uploadStatus.setBackgroundResource(R.drawable.adddrwable);
            binding.uploadStatusTxt.setTextColor(Color.WHITE);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setSelectedItemId(R.id.nav_post);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_post:
                    return true;
                case R.id.nav_chat:
                    startActivity(new Intent(getApplicationContext(), Chats.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_notification:
                    startActivity(new Intent(getApplicationContext(), Notifications.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_profile:
                    Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                    intent.putExtra("UserFriendsValue","A");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);
                    return true;
            }
            return true;
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }



}