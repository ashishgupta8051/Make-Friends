package com.social.friends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.adapter.FavouritePostAdapter;
import com.social.friends.model.FavPost;
import com.social.friends.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;

public class FavouritePost extends AppCompatActivity {
    private String value;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseReference favPostRef;
    private FavouritePostAdapter favouritePostAdapter;
    private ArrayList<FavPost> favPostArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_post);

        getSupportActionBar().setTitle("Favourite Post");

        value = getIntent().getExtras().get("Value").toString();

        progressBar = findViewById(R.id.progressBar7);

        firebaseAuth = FirebaseAuth.getInstance();

        favPostRef = FirebaseDatabase.getInstance().getReference("Favourite Post").child(firebaseAuth.getUid());

        recyclerView = findViewById(R.id.fav_recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        favouritePostAdapter = new FavouritePostAdapter(favPostArrayList,value,FavouritePost.this);
        recyclerView.setAdapter(favouritePostAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        favPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    favPostArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        FavPost favPost = dataSnapshot.getValue(FavPost.class);
                        favPostArrayList.add(favPost);
                    }
                    favouritePostAdapter.notifyDataSetChanged();
                    Collections.reverseOrder();
                    //Collections.reverse(favPostArrayList);
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavouritePost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (value.contentEquals("A") || value.contentEquals("UP")){
            Intent intent = new Intent(FavouritePost.this, SettingsActivity.class);
            intent.putExtra("UserFriendsValue",value);
            startActivity(intent);
            finish();
        }else if (value.contentEquals("S")){
            Intent intent = new Intent(FavouritePost.this, SettingsActivity.class);
            intent.putExtra("UserFriendsValue",value);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(FavouritePost.this, SettingsActivity.class);
            intent.putExtra("UserFriendsValue",value);
            startActivity(intent);
            finish();
        }
    }
}