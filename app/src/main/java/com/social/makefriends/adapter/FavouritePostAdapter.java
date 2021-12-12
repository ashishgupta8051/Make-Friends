package com.social.makefriends.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.manage.userpost.ViewPost;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.FavPost;
import com.social.makefriends.model.UserDetails;

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

                if (allPost.getPostType().equals("photo")){
                    Glide.with(activity).load(allPost.getPostImage()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.playVideo.setVisibility(View.GONE);
                            return false;
                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.playVideo.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.imageView);
                }else {
                    Glide.with(activity).load(allPost.getPostImage()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.playVideo.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.playVideo.setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into(holder.imageView);
                }

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
                                intent.putExtra("postType",allPost.getPostType());
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
        ImageView imageView,playVideo;
        ProgressBar progressBar;
        public FavouritePostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
            playVideo = itemView.findViewById(R.id.playVideo);
            progressBar = itemView.findViewById(R.id.profileViewPostProgress);
        }
    }

}
