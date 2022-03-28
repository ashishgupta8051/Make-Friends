package com.social.makefriends.ui.activity.chat;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.adapter.FriendChatListAdapter;
import com.social.makefriends.model.ExistsChatUser;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.ui.activity.notification.Notifications;
import com.social.makefriends.ui.activity.userpost.Post;
import com.social.makefriends.ui.activity.userprofile.UserProfile;
import com.social.makefriends.utils.Connection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Chats extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    private String CurrentUserId,CurrentDate,CurrentTime;
    private FriendChatListAdapter friendChatListAdapter;
    private DatabaseReference databaseReference,userRef;
    private ArrayList<ExistsChatUser> existsChatUsers = new ArrayList<>();
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ProgressBar progressBar;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Connection.checkInternet(Chats.this,this);
        getSupportActionBar().setTitle("Chats");

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Exists Chat User").child(firebaseAuth.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        progressBar = findViewById(R.id.progressBar4);

        recyclerView = findViewById(R.id.chat_list_recycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        friendChatListAdapter = new FriendChatListAdapter(existsChatUsers, Chats.this,stringArrayList);
        //recyclerView.setAdapter(friendChatListAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        //bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_post:
                    startActivity(new Intent(getApplicationContext(), Post.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_chat:
                    return true;
                case R.id.nav_notification:
                    startActivity(new Intent(getApplicationContext(), Notifications.class));
                    finish();
                    overridePendingTransition(0,0);
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

    private void checkOnlineStatus(String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        HashMap<String,Object> updateStatus = new HashMap<String, Object>();
        updateStatus.put("onlineStatus",status);
        updateStatus.put("onlineDate",CurrentDate);
        updateStatus.put("onlineTime",CurrentTime);
        databaseReference.updateChildren(updateStatus);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("Online");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Query query1 = databaseReference.orderByChild("firstPosition");
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        existsChatUsers.clear();
                        stringArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ExistsChatUser existsChatUser = dataSnapshot.getValue(ExistsChatUser.class);
                            existsChatUsers.add(existsChatUser);
                            stringArrayList.add(existsChatUser.getExistsUserId());
                        }
                        friendChatListAdapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Chats.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chats.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnlineStatus("Offline");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Chats.this,Home.class));
        finish();
    }

}