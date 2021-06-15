package com.social.makefriends.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.adapter.ProfilePostAdapter;
import com.social.makefriends.friendrequest.SearchFriends;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserPost;
import com.social.makefriends.manage.userprofile.UpdateProfile;
import com.social.makefriends.manage.userprofile.ViewProfileImage;
import com.social.makefriends.settings.SettingsActivity;
import com.social.makefriends.ui.TabLayoutView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView name,email,dob,address,bio,UsersName;
    private Button ViewFriend,updateprofile;
    private CircleImageView ProfileImage;
    private RecyclerView recyclerView;
    private ProfilePostAdapter profilePostAdapter;
    private String Value,CurrentUserId;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private DatabaseReference userPostRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        dob = (TextView)findViewById(R.id.dob);
        address = (TextView)findViewById(R.id.address);
        bio = (TextView)findViewById(R.id.bio);
        UsersName = (TextView)findViewById(R.id.users_name);

        ProfileImage = (CircleImageView)findViewById(R.id.profile_Image);

        ViewFriend = (Button)findViewById(R.id.view_friends);
        updateprofile = (Button)findViewById(R.id.update_profile);

        progressBar = findViewById(R.id.progressBar1);

        Value = getIntent().getExtras().get("UserFriendsValue").toString();

        userPostRef = FirebaseDatabase.getInstance().getReference("Post").child(CurrentUserId);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);

        profilePostAdapter = new ProfilePostAdapter(Value,UserProfile.this,userPosts);
        recyclerView.setAdapter(profilePostAdapter);

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewProfileImage.class);
                intent.putExtra("Value",Value);
                startActivity(intent);
                finish();

            }
        });

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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userPostRef.addValueEventListener(new ValueEventListener() {
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
                    progressBar.setVisibility(View.INVISIBLE);
                }
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
                Intent intent = new Intent(UserProfile.this,SettingsActivity.class);
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