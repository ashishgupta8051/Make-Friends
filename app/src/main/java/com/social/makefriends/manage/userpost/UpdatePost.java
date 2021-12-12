package com.social.makefriends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.social.makefriends.utils.CheckInternetConnection;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdatePost extends AppCompatActivity {
    private ImageView postImage,playVideo;
    private EditText postCaption;
    private FirebaseAuth firebaseAuth;
    private String PostId,UserName,ProfilePic,UsersName,UserId,Value,Value2,wallpaper,postType;
    private DatabaseReference databaseReference;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();
    private ProgressBar progressBar;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Update Post");

        firebaseAuth = FirebaseAuth.getInstance();

        postImage = (ImageView)findViewById(R.id.update_post_image);
        postCaption = (EditText)findViewById(R.id.update_post_caption);
        progressBar = findViewById(R.id.updatePostProgress);
        playVideo = findViewById(R.id.playVideo);
        videoView = findViewById(R.id.videoView);

        PostId = getIntent().getExtras().get("PostId").toString();
        postType = getIntent().getExtras().get("postType").toString();
        UserName = getIntent().getExtras().get("UserName").toString();
        ProfilePic = getIntent().getExtras().get("UserDp").toString();
        UsersName = getIntent().getExtras().get("UsersName").toString();
        UserId = getIntent().getExtras().get("CurrentUserId").toString();
        Value = getIntent().getExtras().get("Value").toString();
        Value2 = getIntent().getExtras().get("value").toString();
        wallpaper = getIntent().getExtras().get("ChatBackground").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("All Post").child(PostId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Caption = snapshot.child("caption").getValue().toString();
                    String Image = snapshot.child("postImage").getValue().toString();

                    if (postType.equals("photo")){
                        Glide.with(getApplicationContext()).load(Image).listener(new RequestListener<Drawable>() {
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
                        }).into(postImage);
                    }else {
                        Glide.with(getApplicationContext()).load(Image).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                                progressBar.setVisibility(View.GONE);
                                playVideo.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @SuppressLint("CheckResult")
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                playVideo.setVisibility(View.VISIBLE);
                                return false;
                            }
                        }).into(postImage);

                        playVideo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playVideo.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                videoView.setVisibility(View.VISIBLE);
                                postImage.setVisibility(View.GONE);

                                MediaController mediaController = new MediaController(UpdatePost.this);
                                mediaController.setAnchorView(videoView);
                                mediaController.setMediaPlayer(videoView);
                                mediaController.setPadding(4,0,4,0);
                                videoView.setMediaController(mediaController);
                                videoView.setVideoURI(Uri.parse(Image));
                                videoView.requestFocus();
                                videoView.setOnPreparedListener(mp -> {
                                    videoView.start();
                                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                        @Override
                                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                            progressBar.setVisibility(View.GONE);
                                            mp.start();
                                            mediaController.hide();
                                        }
                                    });
                                });
                            }
                        });
                    }
                    postCaption.setText(Caption);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePost.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("H")){
            startActivity(new Intent(getApplicationContext(), Home.class));
        }else {
            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
            intent2.putExtra("PostId",PostId);
            intent2.putExtra("UserName",UserName);
            intent2.putExtra("ProfilePic",ProfilePic);
            intent2.putExtra("CurrentUserId",UserId);
            intent2.putExtra("UsersName",UsersName);
            intent2.putExtra("value",Value2);
            intent2.putExtra("ChatBackground",wallpaper);
            intent2.putExtra("postType",postType);
            startActivity(intent2);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                updatePost();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePost() {
        String Caption = postCaption.getText().toString();
        if (TextUtils.isEmpty(Caption)){
            postCaption.setError("Enter your Caption");
        }else {
            Map<String,Object> updateCaption = new HashMap<String, Object>();

            updateCaption.put("caption",Caption);
            databaseReference.updateChildren(updateCaption).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UpdatePost.this, "Post Update Successful", Toast.LENGTH_SHORT).show();
                        if (Value.contentEquals("H")){
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else {
                            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
                            intent2.putExtra("PostId",PostId);
                            intent2.putExtra("UserName",UserName);
                            intent2.putExtra("ProfilePic",ProfilePic);
                            intent2.putExtra("CurrentUserId",UserId);
                            intent2.putExtra("UsersName",UsersName);
                            intent2.putExtra("value",Value2);
                            intent2.putExtra("ChatBackground",wallpaper);
                            intent2.putExtra("postType",postType);
                            startActivity(intent2);
                        }
                        finish();
                    }else {
                        Toast.makeText(UpdatePost.this, "Post Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter =  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}