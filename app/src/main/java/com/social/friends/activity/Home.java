package com.social.friends.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.adapter.HomePostAdapter;
import com.social.friends.model.AllPost;
import com.social.friends.friendrequest.SearchFriends;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private HomePostAdapter homePostAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference,favPostRef;
    private ArrayList<AllPost> arrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Home");

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("All Post");
        favPostRef = FirebaseDatabase.getInstance().getReference("Favourite").child(firebaseAuth.getUid());

        recyclerView = (RecyclerView) findViewById(R.id.home_recycle_view);
        linearLayoutManager = new LinearLayoutManager(this);

        progressBar = findViewById(R.id.progressBar2);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        homePostAdapter = new HomePostAdapter(arrayList,Home.this);
        recyclerView.setAdapter(homePostAdapter);

        //Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_post:
                        startActivity(new Intent(getApplicationContext(),Post.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_notification:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(getApplicationContext(), Chats.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                        intent.putExtra("UserFriendsValue","A");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });

        fetchData();
    }

    private void fetchData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        AllPost allPost = dataSnapshot.getValue(AllPost.class);
                        arrayList.add(allPost);
                    }
                    homePostAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                startActivity(new Intent(Home.this, SearchFriends.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}