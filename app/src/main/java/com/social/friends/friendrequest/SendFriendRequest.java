package com.social.friends.friendrequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.social.friends.R;
import com.social.friends.activity.Chats;
import com.social.friends.activity.Home;
import com.social.friends.adapter.FriendProfilePostImageAdapter;
import com.social.friends.fragment.FriendRequest;
import com.social.friends.friendrequest.chatting.ChatWithFriends;
import com.social.friends.model.Friends;
import com.social.friends.model.Request;
import com.social.friends.model.UserDetails;
import com.social.friends.model.UserPost;
import com.social.friends.notification.Client;
import com.social.friends.notification.Data;
import com.social.friends.notification.MyResponse;
import com.social.friends.notification.Senders;
import com.social.friends.notification.Token;
import com.social.friends.utils.APIService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFriendRequest extends AppCompatActivity {
    private String UserId,Value;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference getUserDetailsRef;
    private TextView Name,UserName,Email,Dob,Address,Bio,PrivacyText;
    private CircleImageView Profile_Image;
    private ImageView PrivacyImage;
    private Button Button1,Button2,Button3,Button4;
    private String CurrentUserId,CURRENT_STATE,CurrentTime,CurrentDate,wallpaper;
    private DatabaseReference SendFriendRequestRef,FriendRef,userPost,UserDetails;
    private RecyclerView recycler;
    private FriendProfilePostImageAdapter friendProfilePostImageAdapter;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private ProgressBar progressBar;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friend_request);

        getSupportActionBar().setTitle("Friend Profile");

        apiService = Client.getClient().create(APIService.class);

        UserId = getIntent().getExtras().get("UserId").toString();
        Value = getIntent().getExtras().get("Value").toString();
        wallpaper = getIntent().getExtras().get("ChatBackground").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        progressBar = findViewById(R.id.progressBar5);

        Name = (TextView)findViewById(R.id.view_name);
        UserName = (TextView)findViewById(R.id.view_users_name);
        Email = (TextView)findViewById(R.id.view_email);
        Dob = (TextView)findViewById(R.id.view_dob);
        Address = (TextView)findViewById(R.id.view_address);
        Bio = (TextView)findViewById(R.id.view_bio);
        PrivacyText = (TextView)findViewById(R.id.privacy_text);

        Button1 = (Button)findViewById(R.id.friend_request_bt2);
        Button3 = (Button)findViewById(R.id.decline_friend_request_bt2);
        Button4 = (Button)findViewById(R.id.send_message);
        Button2 = (Button)findViewById(R.id.unfriend);

        Profile_Image = (CircleImageView)findViewById(R.id.view_profile_Image);
        PrivacyImage = (ImageView)findViewById(R.id.privacy);

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        recycler = (RecyclerView)findViewById(R.id.search_friend_recycler);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(SendFriendRequest.this,3);
        recycler.setLayoutManager(linearLayoutManager);

        friendProfilePostImageAdapter = new FriendProfilePostImageAdapter(userPosts,SendFriendRequest.this,Value,wallpaper);
        recycler.setAdapter(friendProfilePostImageAdapter);

        getUserDetailsRef = FirebaseDatabase.getInstance().getReference("User Details");
        SendFriendRequestRef = FirebaseDatabase.getInstance().getReference("Friend Request");
        FriendRef = FirebaseDatabase.getInstance().getReference("Friend");
        userPost = FirebaseDatabase.getInstance().getReference("Post");
        UserDetails = FirebaseDatabase.getInstance().getReference("User Details");

        CURRENT_STATE = "not_friend";

        getUserDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(UserId).exists()){
                    //User id
                    UserDetails userDetails = snapshot.child(UserId).getValue(UserDetails.class);
                    Name.setText(userDetails.getUserName());
                    UserName.setText(userDetails.getUsersName());
                    Email.setText(userDetails.getUserEmail());
                    Dob.setText(userDetails.getUserDob());
                    Address.setText(userDetails.getUserAddress());
                    Bio.setText(userDetails.getUserBio());
                    String ProfileImage = userDetails.getUserProfileImageUrl();
                    if (ProfileImage.equals("None")){
                        Profile_Image.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(ProfileImage).fit().placeholder(R.drawable.profile_image).into(Profile_Image);
                    }

                    //CurrentUser
                    UserDetails userDetails2 = snapshot.child(CurrentUserId).getValue(UserDetails.class);
                    String CurrentName = userDetails2.getUserName();
                    String CurrentUserName = userDetails2.getUsersName();
                    String name = userDetails.getUserName();
                    String userName = userDetails.getUsersName();
                    Button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CURRENT_STATE.equals("request_received")){
                                AcceptRequest(name,userName,CurrentName,CurrentUserName);
                            }else if (CURRENT_STATE.equals("friend")){
                                UnFriend();
                            }
                        }
                    });

                    Button4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SendFriendRequest.this, ChatWithFriends.class);
                            intent.putExtra("Value",Value);
                            intent.putExtra("UserId",UserId);
                            intent.putExtra("Image",ProfileImage);
                            intent.putExtra("ChatBackground",wallpaper);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    Log.e("Error","No user found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FriendRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(UserId)){
                    CURRENT_STATE = "friend";
                    progressBar.setVisibility(View.VISIBLE);
                    Button2.setVisibility(View.VISIBLE);
                    Button2.setEnabled(true);
                    Button2.setText(" UNFRIEND ");

                    PrivacyImage.setVisibility(View.INVISIBLE);
                    PrivacyText.setVisibility(View.INVISIBLE);

                    Button3.setVisibility(View.INVISIBLE);
                    Button3.setEnabled(false);

                    Button1.setVisibility(View.INVISIBLE);
                    Button1.setEnabled(false);

                    Button4.setVisibility(View.VISIBLE);
                    Button4.setEnabled(true);
                    Button4.setText("MESSAGE");

                    userPost.child(UserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                userPosts.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    UserPost userPost = dataSnapshot.getValue(UserPost.class);
                                    if (userPost.getUserId().equals(UserId)){
                                        userPosts.add(userPost);
                                    }
                                }
                                Collections.reverse(userPosts);
                                friendProfilePostImageAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.INVISIBLE);
                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        SendFriendRequestRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(UserId)){
                    String Type = snapshot.child(UserId).child("request_type").getValue().toString();
                    if (Type.equals("sent")){
                        CURRENT_STATE = "request_sent";
                        Button1.setText(" Cancel Request ");
                        Button1.setTextColor(Color.BLACK);
                        Button1.setBackgroundTintList(ContextCompat.getColorStateList(SendFriendRequest.this, R.color.silver));
                    }else if (Type.equals("received")){
                        CURRENT_STATE = "request_received";
                        Button1.setVisibility(View.INVISIBLE);
                        Button1.setEnabled(false);

                        Button2.setVisibility(View.VISIBLE);
                        Button2.setEnabled(true);
                        Button2.setText("ACCEPT");
                        Button2.setTextColor(Color.WHITE);

                        Button3.setVisibility(View.VISIBLE);
                        Button3.setEnabled(true);
                        Button3.setText("DECLINE");
                        Button3.setTextColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_STATE.equals("not_friend")){
                    SendRequest();
                }else if (CURRENT_STATE.equals("request_sent")){
                    CancelRequest();
                }
            }
        });

        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFriendRequestRef.child(CurrentUserId).child(UserId).removeValue();
                SendFriendRequestRef.child(UserId).child(CurrentUserId).removeValue();
                CURRENT_STATE = "not_friend";

                Button3.setVisibility(View.INVISIBLE);
                Button3.setEnabled(false);
                Button2.setVisibility(View.INVISIBLE);
                Button2.setEnabled(false);

                Button1.setVisibility(View.VISIBLE);
                Button1.setEnabled(true);
                Button1.setText(" ADD FRIEND ");
                Button1.setTextColor(Color.WHITE);
                Button1.setBackgroundTintList(ContextCompat.getColorStateList(SendFriendRequest.this, R.color.purple_500));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Update Token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                            Token token1 = new Token(token);
                            reference.child(CurrentUserId).setValue(token1);
                        }else {
                            Toast.makeText(SendFriendRequest.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void UnFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendFriendRequest.this);
        builder.setMessage("Are you sure you want to unfriend");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FriendRef.child(CurrentUserId).child(UserId).removeValue();
                FriendRef.child(UserId).child(CurrentUserId).removeValue();
                CURRENT_STATE = "not_friend";
                Button2.setVisibility(View.INVISIBLE);
                Button2.setEnabled(false);
                Button4.setVisibility(View.INVISIBLE);
                Button4.setEnabled(false);

                Button1.setVisibility(View.VISIBLE);
                Button1.setEnabled(true);
                Button1.setText("Add Friend");
                Button1.setTextColor(Color.WHITE);
                Button1.setBackgroundTintList(ContextCompat.getColorStateList(SendFriendRequest.this, R.color.purple_500));

                PrivacyText.setVisibility(View.VISIBLE);
                PrivacyImage.setVisibility(View.VISIBLE);
                PrivacyText.setText(" Privacy Policy ");
                PrivacyImage.setImageResource(R.drawable.privacy);

                recycler.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.setCancelable(false).create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        alertDialog.show();
    }

    private void AcceptRequest(String name, String userName, String currentName, String currentUserName) {
        String Status = "friend";
        Friends friends = new Friends(Status,UserId,name,userName);
        FriendRef.child(CurrentUserId).child(UserId).setValue(friends);
        Friends friends1 = new Friends(Status,CurrentUserId,currentName,currentUserName);
        FriendRef.child(UserId).child(CurrentUserId).setValue(friends1);
        SendFriendRequestRef.child(CurrentUserId).child(UserId).removeValue();
        SendFriendRequestRef.child(UserId).child(CurrentUserId).removeValue();
        CURRENT_STATE = "friend";
        Button2.setText(" UNFRIEND ");
        Button3.setVisibility(View.INVISIBLE);
        Button3.setEnabled(false);

        Button1.setVisibility(View.INVISIBLE);
        Button1.setEnabled(false);

        Button4.setVisibility(View.VISIBLE);
        Button4.setEnabled(true);
        Button4.setText("MESSAGE");

        PrivacyText.setVisibility(View.INVISIBLE);
        PrivacyImage.setVisibility(View.INVISIBLE);

        //Send Notification
        String msg = "Accept your friend request.";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(UserDetails.class);
                sendNotification(UserId,userDetails.getUserName(),msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void SendRequest() {
        String Type = "sent";
        String type = "received";
        Request sendRequest = new Request(Type,null);
        Request receiveRequest = new Request(type,CurrentUserId);
        SendFriendRequestRef.child(CurrentUserId).child(UserId).setValue(sendRequest);
        SendFriendRequestRef.child(UserId).child(CurrentUserId).setValue(receiveRequest);

        CURRENT_STATE = "request_sent";
        Button1.setText(" Cancel Request ");
        Button1.setTextColor(Color.BLACK);
        Button1.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.silver));

        //Send Notification
        String msg = "Sent you a friend request.";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(UserDetails.class);
                sendNotification(UserId,userDetails.getUserName(),msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String userId, String name, String msg){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query1 = tokens.orderByKey().equalTo(userId);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(CurrentUserId,name+" : "+msg,"Friend Request",userId,"friendRequest");
                    Senders senders = new Senders(data,token.getToken());

                    apiService.sendNotification(senders).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if (response.body().success != 1){
//                                    Toast.makeText(getApplicationContext(), "Failed!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CancelRequest() {
        SendFriendRequestRef.child(CurrentUserId).child(UserId).removeValue();
        SendFriendRequestRef.child(UserId).child(CurrentUserId).removeValue();
        CURRENT_STATE = "not_friend";
        Button1.setText(" ADD FRIEND ");
        Button1.setTextColor(Color.WHITE);
        Button1.setBackgroundTintList(ContextCompat.getColorStateList(SendFriendRequest.this, R.color.purple_500));
    }

    @Override
    public void onBackPressed() {
        if (Value.contentEquals("SF")){
            startActivity(new Intent(getApplicationContext(),SearchFriends.class));
            finish();
        }else if (Value.contentEquals("C")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(UserId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    String ProfileImage = userDetails.getUserProfileImageUrl();
                    Intent intent = new Intent(SendFriendRequest.this, ChatWithFriends.class);
                    intent.putExtra("Value",Value);
                    intent.putExtra("UserId",UserId);
                    intent.putExtra("Image",ProfileImage);
                    intent.putExtra("ChatBackground",wallpaper);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SendFriendRequest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            startActivity(new Intent(SendFriendRequest.this, Home.class));
            finish();
        }
    }

}