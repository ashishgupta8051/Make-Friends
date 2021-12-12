package com.social.makefriends.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.social.makefriends.R;
import com.social.makefriends.adapter.HomePostAdapter;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.friendrequest.SearchFriends;
import com.social.makefriends.utils.CheckInternetConnection;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Home extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private HomePostAdapter homePostAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference,favPostRef;
    private ArrayList<AllPost> arrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private String CurrentUserId;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

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
//        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

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
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
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

   /* private void showAdSAgain() {
        handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 1000 * 60 * 15); // Show the ad after 15 minutes
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter =  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);

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