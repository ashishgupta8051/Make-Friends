package com.social.friends.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.model.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostLikeDislikeUsersAdapter extends RecyclerView.Adapter<PostLikeDislikeUsersAdapter.LikedHolder>{
    private List<String> list;
    private Activity activity;
    private DatabaseReference userRef;

    public PostLikeDislikeUsersAdapter(List<String> id, Activity activity) {
        this.list = id;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LikedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list,parent,false);
        LikedHolder likedHolder = new LikedHolder(view);
        return likedHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostLikeDislikeUsersAdapter.LikedHolder holder, int position) {
        String likeId = list.get(position);
        holder.online.setVisibility(View.GONE);
        holder.cross.setVisibility(View.GONE);

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(likeId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    holder.name.setText(userDetails.getUserName());
                    holder.userName.setText(userDetails.getUsersName());
                    if (userDetails.getUserProfileImageUrl().contentEquals("None")){
                        holder.profileImage.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(userDetails.getUserProfileImageUrl()).fit().
                                placeholder(R.drawable.profile_image).into(holder.profileImage);
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Name",userDetails.getUserName());
                        }
                    });
                }else {
                    Log.e("Error","This user not exists.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LikedHolder extends RecyclerView.ViewHolder{
        TextView name,userName;
        CircleImageView profileImage;
        ImageView cross,online;
        public LikedHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_search_name);
            userName = itemView.findViewById(R.id.user_search_username);
            profileImage = itemView.findViewById(R.id.user_search_profile_image);
            cross = itemView.findViewById(R.id.cross);
            online = itemView.findViewById(R.id.online_status);
        }
    }
}
