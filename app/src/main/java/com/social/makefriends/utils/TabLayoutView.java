package com.social.makefriends.utils;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.userprofile.UserProfile;
import com.social.makefriends.ui.fragment.FriendRequest;
import com.social.makefriends.ui.fragment.MyFriends;


public class TabLayoutView extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String CurrentUserId;
    private DatabaseReference FriendRef,FriendReqRef;
    private int countFriend = 0;
    private int countFriendReq = 0;
    private TextView friendTxt,friendReqTxt;
    private View friendView,friendReqView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout_view);

        Connection.checkInternet(getApplicationContext(),this);

        String value = getIntent().getExtras().get("Value").toString();

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        FriendRef = FirebaseDatabase.getInstance().getReference("Friend").child(CurrentUserId);
        FriendReqRef = FirebaseDatabase.getInstance().getReference("Friend Request").child(CurrentUserId);

        friendTxt = findViewById(R.id.myFriends);
        friendReqTxt = findViewById(R.id.friendReq);
        friendView = findViewById(R.id.myFriendView);
        friendReqView = findViewById(R.id.friendReqView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new MyFriends()).commit();

        friendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendView.setVisibility(View.VISIBLE);
                friendReqView.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new MyFriends()).commit();
            }
        });

        friendReqTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendView.setVisibility(View.INVISIBLE);
                friendReqView.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new FriendRequest()).commit();
            }
        });

        FriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countFriend = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                FriendReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Query query1 = FriendReqRef.orderByChild("request_type").equalTo("received");
                        query1.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                countFriendReq = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                                String CountFriend = String.valueOf(countFriend);
                                String CountFriendReq = String.valueOf(countFriendReq);
                                if (countFriend == 0){
                                    friendTxt.setText("My Friends");
                                }else {
                                    friendTxt.setText("My Friends "+"("+CountFriend+")");
                                }
                                if (countFriendReq == 0){
                                    friendReqTxt.setText("Friend Request");
                                }else {
                                    friendReqTxt.setText("Friend Request "+"("+CountFriendReq+")");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(TabLayoutView.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TabLayoutView.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TabLayoutView.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
        intent.putExtra("UserFriendsValue","UP");
        startActivity(intent);
        finish();
    }

}