package com.social.friends.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.manage.userpost.ViewPost;
import com.social.friends.model.AllPost;
import com.social.friends.model.FavPost;
import com.social.friends.model.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FavouritePostAdapter extends RecyclerView.Adapter<FavouritePostAdapter.FavouritePostHolder>{

    private FirebaseAuth firebaseAuth;
    private List<FavPost> favPostList;
    private String Value;
    private Activity activity;
    private DatabaseReference postRef,userDetailsRef;

    public FavouritePostAdapter(List<FavPost> favPostList, String value, Activity activity) {
        this.favPostList = favPostList;
        Value = value;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FavouritePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_view_post_list,parent,false);
        FavouritePostHolder favouritePostHolder = new FavouritePostHolder(view);
        return favouritePostHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritePostHolder holder, int position) {
        FavPost favPost = favPostList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();

        postRef = FirebaseDatabase.getInstance().getReference("All Post");
        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AllPost allPost = snapshot.child(favPost.getFavPostId()).getValue(AllPost.class);
                Glide.with(activity).load(allPost.getPostImage()).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).
                        placeholder(R.drawable.progress).into(holder.imageView);

                userDetailsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, ViewPost.class);
                                intent.putExtra("PostId",allPost.getKey());
                                intent.putExtra("UserName",allPost.getUserName());
                                intent.putExtra("ProfilePic",allPost.getUserProfilePic());
                                intent.putExtra("CurrentUserId",allPost.getCurrentUserId());
                                intent.putExtra("UsersName",allPost.getUsersName());
                                intent.putExtra("value","SE");
                                intent.putExtra("ChatBackground",userDetails.getChatBackgroundWall());
                                v.getContext().startActivity(intent);
                                ((AppCompatActivity)v.getContext()).finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favPostList.size();
    }

    class FavouritePostHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public FavouritePostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
        }
    }

}
