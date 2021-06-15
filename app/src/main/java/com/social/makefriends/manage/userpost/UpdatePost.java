package com.social.makefriends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdatePost extends AppCompatActivity {
    private ImageView postImage;
    private EditText postCaption;
    private FirebaseAuth firebaseAuth;
    private String PostId,UserName,ProfilePic,UsersName,UserId,Value,Value2,wallpaper;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Update Post");

        firebaseAuth = FirebaseAuth.getInstance();

        postImage = (ImageView)findViewById(R.id.update_post_image);
        postCaption = (EditText)findViewById(R.id.update_post_caption);

        PostId = getIntent().getExtras().get("PostId").toString();
        UserName = getIntent().getExtras().get("UserName").toString();
        ProfilePic = getIntent().getExtras().get("UserDp").toString();
        UsersName = getIntent().getExtras().get("UsersName").toString();
        UserId = getIntent().getExtras().get("CurrentUserId").toString();
        Value = getIntent().getExtras().get("Value").toString();
        Value2 = getIntent().getExtras().get("value").toString();
        wallpaper = getIntent().getExtras().get("ChatBackground").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("All Post").child(PostId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Caption = snapshot.child("caption").getValue().toString();
                    String Image = snapshot.child("postImage").getValue().toString();
                    Picasso.get().load(Image).fit().placeholder(R.drawable.progress).into(postImage);
                    postCaption.setText(Caption);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePost.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("H")){
            startActivity(new Intent(getApplicationContext(), Home.class));
        }else {
            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
            intent2.putExtra("PostId",PostId);
            intent2.putExtra("UserName",UserName);
            intent2.putExtra("ProfilePic",ProfilePic);
            intent2.putExtra("CurrentUserId",UserId);
            intent2.putExtra("UsersName",UsersName);
            intent2.putExtra("value",Value2);
            intent2.putExtra("ChatBackground",wallpaper);
            startActivity(intent2);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                updatePost();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePost() {
        String Caption = postCaption.getText().toString();
        if (TextUtils.isEmpty(Caption)){
            postCaption.setError("Enter your Caption");
        }else {
            Map<String,Object> updateCaption = new HashMap<String, Object>();

            updateCaption.put("caption",Caption);
            databaseReference.updateChildren(updateCaption).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UpdatePost.this, "Post Update Successful", Toast.LENGTH_SHORT).show();
                        if (Value.contentEquals("H")){
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else {
                            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
                            intent2.putExtra("PostId",PostId);
                            intent2.putExtra("UserName",UserName);
                            intent2.putExtra("ProfilePic",ProfilePic);
                            intent2.putExtra("CurrentUserId",UserId);
                            intent2.putExtra("UsersName",UsersName);
                            intent2.putExtra("value",Value2);
                            intent2.putExtra("ChatBackground",wallpaper);
                            startActivity(intent2);
                        }
                        finish();
                    }else {
                        Toast.makeText(UpdatePost.this, "Post Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}