package com.social.makefriends.manage.userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.squareup.picasso.Picasso;

public class ViewProfileImage extends AppCompatActivity {
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private String Value;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_image);

        getSupportActionBar().setTitle("Profile Pic");

        firebaseAuth = FirebaseAuth.getInstance();
        Value = getIntent().getExtras().get("Value").toString();

        imageView = (ImageView)findViewById(R.id.ProfileImage);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String ProfileImage = snapshot.child("userProfileImageUrl").getValue().toString();
                    if (ProfileImage.equals("None")){
                       imageView.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(ProfileImage).fit().placeholder(R.drawable.profile_image).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfileImage.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
        intent.putExtra("UserFriendsValue",Value);
        startActivity(intent);
        finish();
    }
}