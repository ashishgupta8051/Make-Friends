package com.social.makefriends.manage.userpost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.social.makefriends.R;
import com.social.makefriends.activity.Home;

public class PlayPostVideo extends AppCompatActivity {
    private String value,url;
    private String PostId,UserName,ProfilePic,UsersName,UserId,Value,Value2,wallpaper,postType;
    private VideoView videoview;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_post_video);

        value = getIntent().getExtras().get("value").toString();
        url = getIntent().getExtras().get("url").toString();

        if (!value.contentEquals("H")){
            PostId = getIntent().getExtras().get("PostId").toString();
            postType = getIntent().getExtras().get("postType").toString();
            UserName = getIntent().getExtras().get("UserName").toString();
            ProfilePic = getIntent().getExtras().get("UserDp").toString();
            UsersName = getIntent().getExtras().get("UsersName").toString();
            UserId = getIntent().getExtras().get("CurrentUserId").toString();
            Value = getIntent().getExtras().get("Value").toString();
            Value2 = getIntent().getExtras().get("value").toString();
            wallpaper = getIntent().getExtras().get("ChatBackground").toString();
        }


        getSupportActionBar().hide();

        videoview = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.ProgressBar);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoview);
        mediaController.setMediaPlayer(videoview);
        videoview.setMediaController(mediaController);
        videoview.setVideoURI(Uri.parse(url));
        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoview.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                        mediaController.hide();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (value.contentEquals("H")){
            startActivity(new Intent(getApplicationContext(), Home.class));
        }else {
            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
            intent2.putExtra("PostId",PostId);
            intent2.putExtra("UserName",UserName);
            intent2.putExtra("ProfilePic",ProfilePic);
            intent2.putExtra("CurrentUserId",UserId);
            intent2.putExtra("UsersName",UsersName);
            intent2.putExtra("value",Value);
            intent2.putExtra("ChatBackground",wallpaper);
            intent2.putExtra("postType",postType);
            startActivity(intent2);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoview.pause();
    }
}