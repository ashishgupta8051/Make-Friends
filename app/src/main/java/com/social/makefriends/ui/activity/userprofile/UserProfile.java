package com.social.makefriends.ui.activity.userprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.circularstatusview.CircularStatusView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.adapter.ProfilePostAdapter;
import com.social.makefriends.model.Status;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserPost;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.ui.activity.notification.Notifications;
import com.social.makefriends.ui.activity.home.SearchFriends;
import com.social.makefriends.ui.activity.seetings.SettingsActivity;
import com.social.makefriends.ui.activity.chat.Chats;
import com.social.makefriends.ui.activity.userpost.Post;
import com.social.makefriends.model.UserStatus;
import com.social.makefriends.utils.Connection;
import com.social.makefriends.utils.TabLayoutView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class UserProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView name,email,dob,address,bio,UsersName;
    private Button ViewFriend,updateprofile;
    private CircleImageView ProfileImage;
    private RecyclerView recyclerView;
    private ProfilePostAdapter profilePostAdapter;
    private String Value,CurrentUserId;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private DatabaseReference userPostRef,statusRef;
    private ProgressBar progressBar;
    private ImageView noPostImage;
    private CircularStatusView circularStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Connection.checkInternet(UserProfile.this,this);

        getSupportActionBar().setTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        dob = (TextView)findViewById(R.id.dob);
        address = (TextView)findViewById(R.id.address);
        bio = (TextView)findViewById(R.id.bio);
        UsersName = (TextView)findViewById(R.id.users_name);
        circularStatusView = findViewById(R.id.circular_status_view);

        ProfileImage = (CircleImageView)findViewById(R.id.profile_Image);

        ViewFriend = (Button)findViewById(R.id.view_friends);
        updateprofile = (Button)findViewById(R.id.update_profile);

        progressBar = findViewById(R.id.progressBar1);

        noPostImage = findViewById(R.id.noPost);

        Value = getIntent().getExtras().get("UserFriendsValue").toString();

        userPostRef = FirebaseDatabase.getInstance().getReference("Post").child(CurrentUserId);
        statusRef = FirebaseDatabase.getInstance().getReference("All Status").child(CurrentUserId);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);

        profilePostAdapter = new ProfilePostAdapter(Value,UserProfile.this,userPosts);
        recyclerView.setAdapter(profilePostAdapter);

        ViewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TabLayoutView.class);
                intent.putExtra("Value",Value);
                startActivity(intent);
                finish();
            }
        });

        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                intent.putExtra("Value",Value);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Image = snapshot.child("userProfileImageUrl").getValue().toString();
                    if (Image.equals("None")){
                        ProfileImage.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(ProfileImage);
                    }

                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    name.setText(userDetails.getUserName());
                    UsersName.setText(userDetails.getUsersName());
                    email.setText(userDetails.getUserEmail());
                    dob.setText(userDetails.getUserDob());
                    address.setText(userDetails.getUserAddress());
                    bio.setText(userDetails.getUserBio());
                }else {
                    Log.e("Error","Update Profile");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        statusRef.addValueEventListener(new ValueEventListener() {
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
                    circularStatusView.setVisibility(View.VISIBLE);

                    circularStatusView.setPortionsCount(userStatus.getStatusList().size());

                    ProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<MyStory> myStories = new ArrayList<>();
                            for(Status status: userStatus.getStatusList()){
                                myStories.add(new MyStory(status.getStatusUrl()));
                            }

                            new StoryView.Builder(getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(userStatus.getName())
                                    .setSubtitleText(userStatus.getUserName())// Default is Hidden// Default is Hidden
                                    .setTitleLogoUrl(userStatus.getProfileImage())// Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {

                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {

                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                        }
                    });
                }else {
                    ProfileImage.setBorderWidth(3);
                    ProfileImage.setBorderColor(Color.BLACK);
                    ProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ViewProfileImage.class);
                            intent.putExtra("Value",Value);
                            startActivity(intent);
                            finish();
                        }
                    });
                    Log.e("TAG ","Not found any status");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                    startActivity(new Intent(getApplicationContext(), Chats.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_notification:
                    startActivity(new Intent(getApplicationContext(), Notifications.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_profile:
                    return true;
            }
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userPostRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userPosts.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserPost userPost = dataSnapshot.getValue(UserPost.class);
                        if (CurrentUserId.equals(userPost.getUserId())){
                            userPosts.add(userPost);
                        }
                    }
                    Collections.reverse(userPosts);
                    profilePostAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    Log.e("Error","User does not exists");
                    noPostImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                ViewFriend.setVisibility(View.VISIBLE);
                updateprofile.setVisibility(View.VISIBLE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(UserProfile.this, SettingsActivity.class);
                intent.putExtra("UserFriendsValue",Value);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("S")){
            startActivity(new Intent(getApplicationContext(), SearchFriends.class));
        }else if (Value.contentEquals("A") || Value.contentEquals("UP")){
            startActivity(new Intent(getApplicationContext(),Home.class));
        }else {
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
        finish();
    }


}