package com.social.friends.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.social.friends.R;
import com.social.friends.activity.UserProfile;
import com.social.friends.friendrequest.SendFriendRequest;
import com.social.friends.model.SearchFriendHistory;
import com.social.friends.model.UserDetails;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendHistoryAdapter extends FirebaseRecyclerAdapter<SearchFriendHistory, SearchFriendHistoryAdapter.SearchFriendHistoryHolder> {

    private FirebaseAuth firebaseAuth;
    private Activity activity;
    private DatabaseReference userRef;

    public SearchFriendHistoryAdapter(@NonNull FirebaseRecyclerOptions<SearchFriendHistory> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchFriendHistoryHolder holder, int position, @NonNull SearchFriendHistory model) {
        holder.Online.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(CurrentUserId);

        String UserId = model.getSearch_UserId();
        String Image = model.getSearch_ProfilePic();
        if (Image.equals("None")){
            holder.ProfileImage.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(holder.ProfileImage);
        }
        holder.Name.setText(model.getSearch_Name());
        holder.UserName.setText(model.getSearch_UserName());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                String Image = userDetails.getChatBackgroundWall();
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CurrentUserId.equals(UserId)){
                            Intent intent = new Intent(v.getContext(), UserProfile.class);
                            intent.putExtra("UserFriendsValue","S");
                            v.getContext().startActivity(intent);
                        }else {
                            Intent intent2 = new Intent(v.getContext(), SendFriendRequest.class);
                            intent2.putExtra("UserId",UserId);
                            intent2.putExtra("Value","SF");
                            intent2.putExtra("ChatBackground",Image);
                            v.getContext().startActivity(intent2);
                        }
                        ((AppCompatActivity)v.getContext()).finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.Cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Search Friend History").child(CurrentUserId);
                deleteRef.child(UserId).removeValue();
            }
        });
    }

    @NonNull
    @Override
    public SearchFriendHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list,parent,false);
        SearchFriendHistoryHolder searchFriendHistoryHolder = new SearchFriendHistoryHolder(view);
        return searchFriendHistoryHolder;
    }

    public class SearchFriendHistoryHolder extends RecyclerView.ViewHolder{
        CircleImageView ProfileImage;
        TextView Name,UserName;
        ImageView Cross,Online;
        ConstraintLayout constraintLayout;
        public SearchFriendHistoryHolder(@NonNull View itemView) {
            super(itemView);
            ProfileImage = (CircleImageView) itemView.findViewById(R.id.user_search_profile_image);
            Cross = (ImageView) itemView.findViewById(R.id.cross);
            Online = (ImageView) itemView.findViewById(R.id.online_status);
            Name = (TextView) itemView.findViewById(R.id.user_search_name);
            UserName = (TextView) itemView.findViewById(R.id.user_search_username);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.search_friend_const);
        }
    }
}
