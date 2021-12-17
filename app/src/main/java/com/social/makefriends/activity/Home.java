package com.social.makefriends.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.social.makefriends.R;
import com.social.makefriends.adapter.HomePostAdapter;
import com.social.makefriends.adapter.StatusAdapter;
import com.social.makefriends.databinding.ActivityHomeBinding;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.friendrequest.SearchFriends;
import com.social.makefriends.model.Status;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserStatus;
import com.social.makefriends.utils.CheckInternetConnection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private RecyclerView recyclerView,statusRecyclerView;
    private FirebaseAuth firebaseAuth;
    private HomePostAdapter homePostAdapter;
    private StatusAdapter statusAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference postRef,userDetailsRef,userStatusRef,friendRef;
    private ArrayList<AllPost> arrayList = new ArrayList<>();
    private ArrayList<UserStatus> userStatusArrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private String CurrentUserId;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Home");

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("All Post");
        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        userStatusRef = FirebaseDatabase.getInstance().getReference("All Status");
        friendRef = FirebaseDatabase.getInstance().getReference("Friend").child(firebaseAuth.getUid());

        recyclerView = findViewById(R.id.home_recycle_view);
        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);

        progressBar = findViewById(R.id.progressBar2);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        homePostAdapter = new HomePostAdapter(arrayList,Home.this);
        recyclerView.setAdapter(homePostAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        statusRecyclerView.setLayoutManager(layoutManager);
        statusRecyclerView.setHasFixedSize(true);

        //Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });

        //get Token Id
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                            HashMap<String,Object> addToken = new HashMap<>();
                            addToken.put("token",token);
                            reference.child(firebaseAuth.getCurrentUser().getUid()).setValue(addToken);
                        }else {
                            Toast.makeText(getApplicationContext(), task.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter =  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);

        //GetAll Status
        Query query = friendRef.orderByChild("status").equalTo("friend");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatusArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String key = dataSnapshot.child("friendUid").getValue(String.class);
                        userStatusRef.child(String.valueOf(key)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    UserStatus userStatus = snapshot.getValue(UserStatus.class);
                                    ArrayList<Status> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot1 : snapshot.child("status").getChildren()){
                                        Status status = snapshot1.getValue(Status.class);
                                        arrayList.add(status);
                                    }
                                    assert userStatus != null;
                                    userStatus.setStatusList(arrayList);
                                    userStatusArrayList.add(userStatus);
                                    statusAdapter = new StatusAdapter(Home.this,userStatusArrayList);
                                    statusRecyclerView.setAdapter(statusAdapter);
                                    statusAdapter.notifyDataSetChanged();
                                }else {
                                    Log.e("TAG","Not found any status");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Log.e("TAG","you have no friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get All Post
        postRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        AllPost allPost = dataSnapshot.getValue(AllPost.class);
                        arrayList.add(allPost);
                    }
                    homePostAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
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
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
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