package com.social.makefriends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.social.makefriends.adapter.CommentAdapter;
import com.social.makefriends.model.Comments;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.utils.CheckInternetConnection;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPostComment extends AppCompatActivity {
    private String PostId,UserName,UserPic,UsersName,UserId,Value2,wallpaper;
    private ImageView Send,emojiImg;
    private EmojiEditText CommentEditBox;
    private CircleImageView ProfilePic;
    private TextView Name,usersName;
    private String CurrentDate,CurrentTime;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private String CommentValue = String.valueOf(1);
    private String CurrentUserId;
    private long postComment = 0;
    private String Comment;
    private String Value;
    private ProgressBar progressBar;
    private DatabaseReference commentRef;
    private ArrayList<Comments> commentsArrayList = new ArrayList<>();
    private Boolean check = false;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_comment);

        getSupportActionBar().setTitle("Comments");

        PostId = getIntent().getExtras().get("PostId").toString();
        UserName = getIntent().getExtras().get("UserName").toString();
        UserPic = getIntent().getExtras().get("UserDp").toString();
        UsersName = getIntent().getExtras().get("UsersName").toString();
        Value = getIntent().getExtras().get("Value").toString();
        Value2 = getIntent().getExtras().get("value").toString();
        UserId = getIntent().getExtras().get("CurrentUserId").toString();
        wallpaper = getIntent().getExtras().get("ChatBackground").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        commentRef = FirebaseDatabase.getInstance().getReference("Comment").child(PostId);

        Send = (ImageView)findViewById(R.id.send_comment);
        CommentEditBox = (EmojiEditText) findViewById(R.id.comment_input_box);

        ProfilePic = (CircleImageView)findViewById(R.id.user_profile_image2);

        Name = (TextView)findViewById(R.id.user_name2);
        usersName = (TextView)findViewById(R.id.users_name2);

        progressBar = findViewById(R.id.commentProgressBar);

        emojiImg = findViewById(R.id.comment_emoji);

        recyclerView = (RecyclerView)findViewById(R.id.comment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(UserPostComment.this,commentsArrayList,Value,wallpaper);
        recyclerView.setAdapter(commentAdapter);

        if (UserPic.equals("None")){
            ProfilePic.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(UserPic).fit().into(ProfilePic);
        }
        Name.setText(UserName);
        usersName.setText(UsersName);

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.comment_const1)).build(CommentEditBox);
        emojiImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check){
                    popup.toggle();
                    emojiImg.setImageResource(R.drawable.keyboard);
                    check = true;
                }else {
                    popup.toggle();
                    emojiImg.setImageResource(R.drawable.emoji);
                    check = false;
                }
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference countPostCommentRef = FirebaseDatabase.getInstance().getReference("Comment").child(PostId);
                countPostCommentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            postComment = snapshot.getChildrenCount();
                        }else {
                            postComment = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserPostComment.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Comment = CommentEditBox.getText().toString();
                if (Comment.isEmpty()){
                    CommentEditBox.setError("Empty");
                }else {
                    Calendar Date = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
                    CurrentDate = currentDate.format(Date.getTime());

                    Calendar Time = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                    CurrentTime = currentTime.format(Time.getTime());

                    DatabaseReference userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(CurrentUserId);
                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comment").child(PostId);
                    String commentId = commentRef.push().getKey();
                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                String Image = userDetails.getUserProfileImageUrl();
                                String userName = userDetails.getUsersName();
                                String TotalPostComment = String.valueOf(postComment);

                                HashMap<String,Object> addComment = new HashMap<>();
                                addComment.put("commentId",commentId);
                                addComment.put("image",Image);
                                addComment.put("name",userName);
                                addComment.put("currentDate",CurrentDate);
                                addComment.put("currentTime",CurrentTime);
                                addComment.put("postComment",Comment);
                                addComment.put("currentUserId",CurrentUserId);
                                addComment.put("postId",PostId);
                                addComment.put("postCommentCount",TotalPostComment);
                                addComment.put("postUserId",UserId);

                                commentRef.child(commentId).updateChildren(addComment);
                                CommentEditBox.setText(null);
                                emojiImg.setImageResource(R.drawable.emoji);
                                check = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UserPostComment.this, error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        fetchAllComments();
    }

    private void fetchAllComments() {
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    commentsArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Comments comments = dataSnapshot.getValue(Comments.class);
                        commentsArrayList.add(comments);
                    }
                    commentAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPostComment.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("H")){
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }else {
            Intent intent2 = new Intent(getApplicationContext(),ViewPost.class);
            intent2.putExtra("PostId",PostId);
            intent2.putExtra("UserName",UserName);
            intent2.putExtra("ProfilePic",UserPic);
            intent2.putExtra("CurrentUserId",UserId);
            intent2.putExtra("UsersName",UsersName);
            intent2.putExtra("value",Value2);
            intent2.putExtra("ChatBackground",wallpaper);
            startActivity(intent2);
        }
        finish();
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
}