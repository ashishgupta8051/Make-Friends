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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.adapter.HomePostAdapter;
import com.social.makefriends.adapter.NotificationAdapter;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.NotificationModel;
import com.social.makefriends.utils.CheckInternetConnection;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();
    private NotificationAdapter notificationAdapter;
    private DatabaseReference notificationRef;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<NotificationModel> arrayList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notification");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setSelectedItemId(R.id.nav_notification);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.notificationRec);
        progressBar = findViewById(R.id.notificationProgress);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(arrayList);
        recyclerView.setAdapter(notificationAdapter);

        notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
        notificationRef.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NotificationModel notificationModel = dataSnapshot.getValue(NotificationModel.class);
                    arrayList.add(notificationModel);
                }
                notificationAdapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(),Home.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_post:
                    startActivity(new Intent(getApplicationContext(),Post.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_chat:
                    startActivity(new Intent(getApplicationContext(), Chats.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_notification:
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }


}