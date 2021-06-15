package com.social.makefriends.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.social.makefriends.fragment.FriendRequest;
import com.social.makefriends.fragment.MyFriends;

import java.util.ArrayList;

public class TabLayoutView extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayoutAdapter tabLayoutAdapter;
    private FirebaseAuth firebaseAuth;
    private String CurrentUserId;
    private DatabaseReference FriendRef,FriendReqRef;
    private int countFriend = 0;
    private int countFriendReq = 0;
    private String Value;
    private Intent intent;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout_view);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (ViewPager)findViewById(R.id.view_pager);

        Value = getIntent().getExtras().get("Value").toString();

        FriendRef = FirebaseDatabase.getInstance().getReference("Friend").child(CurrentUserId);
        FriendReqRef = FirebaseDatabase.getInstance().getReference("Friend Request").child(CurrentUserId);

        FriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
                countFriend = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                FriendReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Query query1 = FriendReqRef.orderByChild("request_type").equalTo("received");
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
                                countFriendReq = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                                String CountFriend = String.valueOf(countFriend);
                                String CountFriendReq = String.valueOf(countFriendReq);
                                if (countFriend == 0){
                                    tabLayoutAdapter.addFragment(new MyFriends(),"My Friends");
                                }else {
                                    tabLayoutAdapter.addFragment(new MyFriends(),"("+CountFriend+")"+" My Friends");
                                }
                                if (countFriendReq == 0){
                                    tabLayoutAdapter.addFragment(new FriendRequest(),"Friend Request");
                                }else {
                                    tabLayoutAdapter.addFragment(new FriendRequest(),"("+CountFriendReq+")"+" Friend Request");
                                }

                                viewPager.setAdapter(tabLayoutAdapter);
                                tabLayout.setupWithViewPager(viewPager);
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

    // Create TabLayoutAdapter class
    class TabLayoutAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentArrayList;
        private ArrayList<String> stringArrayList;

        public TabLayoutAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragmentArrayList = new ArrayList<>();
            this.stringArrayList =  new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragmentArrayList.add(fragment);
            stringArrayList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringArrayList.get(position);
        }
    }
}