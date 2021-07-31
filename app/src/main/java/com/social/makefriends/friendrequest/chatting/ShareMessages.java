package com.social.makefriends.friendrequest.chatting;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.adapter.ShareMessageAdapter;
import com.social.makefriends.model.Friends;
import com.social.makefriends.utils.CheckInternetConnection;

import java.util.ArrayList;

public class ShareMessages extends AppCompatActivity {
    private ArrayList<Friends> friendsArrayList = new ArrayList<>();
    private DatabaseReference friendRef;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String currentUserId,userId,image,value,backGroundImage,messageType,messageDetails,fileName,fileType;
    private ShareMessageAdapter shareMessageAdapter;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_messages);

        getSupportActionBar().setTitle("Share Message");

        userId = getIntent().getExtras().get("UserId").toString();
        value = getIntent().getExtras().get("Value").toString();
        image = getIntent().getExtras().get("Image").toString();
        backGroundImage = getIntent().getExtras().get("ChatBackground").toString();
        messageType = getIntent().getExtras().get("MessageType").toString();
        messageDetails = getIntent().getExtras().get("MessageDetails").toString();
        fileType = getIntent().getExtras().get("fileType").toString();
        fileName = getIntent().getExtras().get("fileName").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        friendRef = FirebaseDatabase.getInstance().getReference("Friend").child(currentUserId);

        progressBar = findViewById(R.id.share_progress);
        recyclerView = findViewById(R.id.share_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        shareMessageAdapter = new ShareMessageAdapter(friendsArrayList,ShareMessages.this,messageDetails,messageType,fileName,fileType);
        recyclerView.setAdapter(shareMessageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.action_search2);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search your Friends !!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                friendRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Query query1 = friendRef.orderByChild("friend_userName").startAt(query).endAt(query+"\uf8ff");
                            query1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    friendsArrayList.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        Friends friends = dataSnapshot.getValue(Friends.class);
                                        friendsArrayList.add(friends);
                                    }
                                    shareMessageAdapter.notifyDataSetChanged();
                                    shareMessageAdapter = new ShareMessageAdapter(friendsArrayList,ShareMessages.this,messageDetails,messageType,fileName,fileType);
                                    recyclerView.setAdapter(shareMessageAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Log.e("Error","You have no friends");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query == null){
                    friendRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                friendsArrayList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Friends friends = dataSnapshot.getValue(Friends.class);
                                    friendsArrayList.add(friends);
                                }
                                shareMessageAdapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    friendRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Query query1 = friendRef.orderByChild("friend_userName").startAt(query).endAt(query+"\uf8ff");
                                query1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        friendsArrayList.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Friends friends = dataSnapshot.getValue(Friends.class);
                                            friendsArrayList.add(friends);
                                        }
                                        shareMessageAdapter.notifyDataSetChanged();
                                        shareMessageAdapter = new ShareMessageAdapter(friendsArrayList,ShareMessages.this,messageDetails,messageType,fileName,fileType);
                                        recyclerView.setAdapter(shareMessageAdapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Log.e("Error","You have no friends");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);

        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    friendsArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        friendsArrayList.add(friends);
                    }
                    shareMessageAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShareMessages.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShareMessages.this, ChatWithFriends.class);
        intent.putExtra("Value",value);
        intent.putExtra("UserId",userId);
        intent.putExtra("Image",image);
        intent.putExtra("ChatBackground",backGroundImage);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}