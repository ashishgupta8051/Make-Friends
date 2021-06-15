package com.social.friends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.social.friends.R;
import com.social.friends.activity.Home;
import com.social.friends.adapter.PostLikeDislikeUsersAdapter;

import java.util.ArrayList;

public class PostDislikedUsers extends AppCompatActivity {
    private String value,postId;
    private DatabaseReference disLikedRef;
    private ArrayList<String> strList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PostLikeDislikeUsersAdapter postLikeDislikeUsersAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Intent intent;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_disliked_users);

        getSupportActionBar().setTitle("Dislikes");

        intent = getIntent();
        extras = intent.getExtras();

        if (extras != null){
            value = extras.getString("Value");
            postId = extras.getString("PostId");
        }else {
            Log.e("Details","Value is null");
        }

        disLikedRef = FirebaseDatabase.getInstance().getReference("Dislikes").child(postId);

        recyclerView = findViewById(R.id.disLikeRec);
        progressBar = findViewById(R.id.disLikeProgress);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLikeDislikeUsersAdapter = new PostLikeDislikeUsersAdapter(strList,PostDislikedUsers.this);
        recyclerView.setAdapter(postLikeDislikeUsersAdapter);

        fetchLikedUsers();
    }

    private void fetchLikedUsers() {
        disLikedRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(PostDislikedUsers.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (value.contentEquals("H")){
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(getApplicationContext(), ViewPost.class);
            intent.putExtra("value",value);
            intent.putExtra("PostId",postId);
            intent.putExtra("UserName",extras.getString("UserName"));
            intent.putExtra("ProfilePic",extras.getString("ProfilePic"));
            intent.putExtra("UsersName",extras.getString("UsersName"));
            intent.putExtra("CurrentUserId",extras.getString("CurrentUserId"));
            intent.putExtra("ChatBackground",extras.getString("ChatBackground"));
            startActivity(intent);
            finish();
        }
    }
}