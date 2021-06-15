package com.social.makefriends.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.social.makefriends.friendrequest.SendFriendRequest;
import com.social.makefriends.model.SearchFriendHistory;
import com.social.makefriends.model.UserDetails;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendsAdapter extends FirebaseRecyclerAdapter<UserDetails, SearchFriendsAdapter.SearchViewHolder> {
    private FirebaseAuth firebaseAuth;
    private long countProfile = 0;
    private Activity activity;
    private DatabaseReference userRef;

    public SearchFriendsAdapter(@NonNull FirebaseRecyclerOptions<UserDetails> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull UserDetails model) {
        String ProfileImageUri = model.getUserProfileImageUrl();
        String UserId = model.getUserUid();
        String name = model.getUserName();
        String userName = model.getUsersName();

        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserUId = firebaseAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(CurrentUserUId);

        holder.Cross2.setVisibility(View.INVISIBLE);
        holder.Online2.setVisibility(View.GONE);

        if (ProfileImageUri.equals("None")){
            holder.ProfileImage.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(ProfileImageUri).fit().placeholder(R.drawable.profile_image).into(holder.ProfileImage);
        }
        holder.Name.setText(model.getUserName());
        holder.UserName.setText(model.getUsersName());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Search Friend History").child(CurrentUserUId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countProfile = snapshot.getChildrenCount();
                }else {
                    countProfile = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                        String Image = userDetails.getChatBackgroundWall();
                        if (UserId.equals(CurrentUserUId)){
                            Intent intent = new Intent(v.getContext(),UserProfile.class);
                            intent.putExtra("UserFriendsValue","S");
                            v.getContext().startActivity(intent);
                        }else {
                            Intent intent2 = new Intent(v.getContext(),SendFriendRequest.class);
                            intent2.putExtra("UserId",UserId);
                            intent2.putExtra("Value","SF");
                            intent2.putExtra("ChatBackground",Image);
                            v.getContext().startActivity(intent2);
                        }

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(UserId).exists()){
                                    Log.e("Error","Already Added in database");
                                }else {
                                    String Total = String.valueOf(countProfile);
                                    SearchFriendHistory searchFriendHistory = new SearchFriendHistory(name,userName,ProfileImageUri,UserId,Total);
                                    databaseReference.child(UserId).setValue(searchFriendHistory);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        ((AppCompatActivity)v.getContext()).finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list,parent,false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(view);
        return searchViewHolder;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        CircleImageView ProfileImage;
        TextView Name,UserName;
        ConstraintLayout constraintLayout;
        ImageView Cross2,Online2;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ProfileImage = (CircleImageView) itemView.findViewById(R.id.user_search_profile_image);
            Name = (TextView) itemView.findViewById(R.id.user_search_name);
            UserName = (TextView) itemView.findViewById(R.id.user_search_username);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.search_friend_const);
            Cross2 = (ImageView) itemView.findViewById(R.id.cross);
            Online2 = (ImageView) itemView.findViewById(R.id.online_status);
        }
    }
}
