package com.social.makefriends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.social.makefriends.adapter.PostLikeDislikeUsersAdapter;
import com.social.makefriends.utils.CheckInternetConnection;

import java.util.ArrayList;

public class PostLikedUsers extends AppCompatActivity {
    private String PostId,UserName,UserPic,UsersName,UserId,Value2,wallpaper,Value;
    private DatabaseReference likedRef;
    private ArrayList<String> strList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PostLikeDislikeUsersAdapter postLikeDislikeUsersAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Intent intent;
    private Bundle extras;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_users);

        getSupportActionBar().setTitle("Likes");

        Value = getIntent().getExtras().get("Value").toString();
        if (Value.equals("H")){
            PostId = getIntent().getExtras().get("PostId").toString();
        }else {
            PostId = getIntent().getExtras().get("PostId").toString();
            UserName = getIntent().getExtras().get("UserName").toString();
            UserPic = getIntent().getExtras().get("UserDp").toString();
            UsersName = getIntent().getExtras().get("UsersName").toString();
            Value = getIntent().getExtras().get("Value").toString();
            Value2 = getIntent().getExtras().get("value").toString();
            UserId = getIntent().getExtras().get("CurrentUserId").toString();
            wallpaper = getIntent().getExtras().get("ChatBackground").toString();
        }

        likedRef = FirebaseDatabase.getInstance().getReference("Likes").child(PostId);

        recyclerView = findViewById(R.id.likeRec);
        progressBar = findViewById(R.id.likeProgress);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLikeDislikeUsersAdapter = new PostLikeDislikeUsersAdapter(strList,PostLikedUsers.this);
        recyclerView.setAdapter(postLikeDislikeUsersAdapter);

        fetchLikedUsers();

    }

    private void fetchLikedUsers() {
        likedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    strList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String key = dataSnapshot.getValue().toString();
                        strList.add(key);
                    }
                    postLikeDislikeUsersAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Error","Not Exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostLikedUsers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("H")){
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }else {
            String postType = getIntent().getExtras().get("postType").toString();
            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
            intent2.putExtra("PostId",PostId);
            intent2.putExtra("UserName",UserName);
            intent2.putExtra("ProfilePic",UserPic);
            intent2.putExtra("CurrentUserId",UserId);
            intent2.putExtra("UsersName",UsersName);
            intent2.putExtra("value",Value2);
            intent2.putExtra("postType",postType);
            intent2.putExtra("ChatBackground",wallpaper);
            startActivity(intent2);
        }
        finish();
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