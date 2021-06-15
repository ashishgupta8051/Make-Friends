package com.social.friends.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.friendrequest.SendFriendRequestDuplicate;
import com.social.friends.model.Friends;
import com.social.friends.model.Request;
import com.social.friends.model.UserDetails;
import com.social.friends.notification.Client;
import com.social.friends.notification.Data;
import com.social.friends.notification.MyResponse;
import com.social.friends.notification.Senders;
import com.social.friends.notification.Token;
import com.social.friends.utils.APIService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendRequestAdapter extends FirebaseRecyclerAdapter<Request, MyFriendRequestAdapter.RequestHolder> {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference getUserDatabaseReference,currentUserDatabaseReference,makeFriend,SendFriendRequestRef;
    private Activity activity;
    private APIService apiService;

    public MyFriendRequestAdapter(@NonNull FirebaseRecyclerOptions<Request> options , Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder holder, int position, @NonNull Request model) {
        apiService = Client.getClient().create(APIService.class);
        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        String UserId = model.getSenderUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(UserId);
        currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(CurrentUserId);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Image = snapshot.child("userProfileImageUrl").getValue().toString();
                    String Name = snapshot.child("userName").getValue().toString();
                    String UserName = snapshot.child("usersName").getValue().toString();

                    if (Image.contentEquals("None")){
                        holder.ProfileImage.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(holder.ProfileImage);
                    }
                    holder.Name.setText(Name);
                    holder.UserName.setText(UserName);

                    currentUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            String CurrentName = userDetails.getUserName();
                            String CurrentUserName = userDetails.getUsersName();
                            holder.Accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AcceptRequest(UserId,CurrentUserId,Name,UserName,CurrentName,CurrentUserName);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Log.e("Error","not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFriendRequestRef = FirebaseDatabase.getInstance().getReference("Friend Request");
                SendFriendRequestRef.child(CurrentUserId).child(UserId).removeValue();
                SendFriendRequestRef.child(UserId).child(CurrentUserId).removeValue();
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), SendFriendRequestDuplicate.class);
                intent2.putExtra("UserId",UserId);
                intent2.putExtra("Value","TLB");
                v.getContext().startActivity(intent2);
                ((AppCompatActivity) activity).finish();
            }
        });
    }

    private void AcceptRequest(String userId, String currentUserId, String name, String userName, String currentName, String currentUserName) {
        makeFriend = FirebaseDatabase.getInstance().getReference("Friend");
        SendFriendRequestRef = FirebaseDatabase.getInstance().getReference("Friend Request");
        String Status = "friend";
        Friends friends = new Friends(Status,userId,name,userName);
        makeFriend.child(currentUserId).child(userId).setValue(friends);
        Friends friends1 = new Friends(Status,currentUserId,currentName,currentUserName);
        makeFriend.child(userId).child(currentUserId).setValue(friends1);
        SendFriendRequestRef.child(currentUserId).child(userId).removeValue();
        SendFriendRequestRef.child(userId).child(currentUserId).removeValue();

        //Send Notification
        String msg = "Accept your friend request.";
        DatabaseReference UserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(currentUserId).getValue(UserDetails.class);
                sendNotification(userId,userDetails.getUserName(),msg,currentUserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String currentUserId, String userId, String name, String msg){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query1 = tokens.orderByKey().equalTo(userId);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(currentUserId,name+" : "+msg,"Friend Request",userId,"friendRequest");
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
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_status_list,parent,false);
        RequestHolder requestHolder =new RequestHolder(view);
        return requestHolder;
    }

    class RequestHolder extends RecyclerView.ViewHolder{
        private CircleImageView ProfileImage;
        private TextView Name,UserName;
        private Button Accept,Decline;
        private ConstraintLayout constraintLayout;
        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            ProfileImage = (CircleImageView)itemView.findViewById(R.id.user_search_profile_image2);

            Name = (TextView)itemView.findViewById(R.id.user_search_name2);
            UserName = (TextView)itemView.findViewById(R.id.user_search_username2);

            Accept = (Button)itemView.findViewById(R.id.accept_friend_request_btn);
            Decline = (Button)itemView.findViewById(R.id.decline_friend_request_btn);

            constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.const_request2);
        }
    }

}
