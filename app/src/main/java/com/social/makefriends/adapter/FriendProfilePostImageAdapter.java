package com.social.makefriends.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
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
import com.social.makefriends.ui.activity.userpost.ViewPost;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.UserPost;

import java.util.List;

public class FriendProfilePostImageAdapter extends RecyclerView.Adapter<FriendProfilePostImageAdapter.ViewHolder> {
    private Activity context;
    private String Value,chatWallpaper;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private List<UserPost> userPosts;

    public FriendProfilePostImageAdapter(List<UserPost> userPosts, Activity context, String Value, String chatWallpaper) {
        this.userPosts = userPosts;
        this.context = context;
        this.Value = Value;
        this.chatWallpaper = chatWallpaper;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPost model = userPosts.get(position);
        String postId = model.getPostId();
        String UserId = model.getUserId();

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(UserId).child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserPost userPost = snapshot.getValue(UserPost.class);
                    String image = userPost.getPostImage();

                    if (model.getPostType().equals("photo")) {
                        Glide.with(context).load(image).listener(new RequestListener<Drawable>() {
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
                        }).into(holder.PostImage);
                    }else {
                        Glide.with(context).load(image).listener(new RequestListener<Drawable>() {
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
                        }).into(holder.PostImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("All Post").child(postId);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AllPost allPost = snapshot.getValue(AllPost.class);
                    String UserName = allPost.getUserName();
                    String ProfilePic = allPost.getUserProfilePic();
                    String UserId = allPost.getCurrentUserId();
                    String UsersName = allPost.getUsersName();

                    holder.PostImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ViewPost.class);
                            intent.putExtra("PostId",postId);
                            intent.putExtra("UserName",UserName);
                            intent.putExtra("postType",model.getPostType());
                            intent.putExtra("ProfilePic",ProfilePic);
                            intent.putExtra("CurrentUserId",UserId);
                            intent.putExtra("UsersName",UsersName);
                            intent.putExtra("value",Value);
                            intent.putExtra("ChatBackground",chatWallpaper);
                            v.getContext().startActivity(intent);
                            ((AppCompatActivity)v.getContext()).finish();
                        }
                    });
                }else {
                    Log.e("Error", "Upload Post");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_view_post_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView PostImage,playVideo;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostImage = (ImageView)itemView.findViewById(R.id.post_image);
            playVideo = (ImageView)itemView.findViewById(R.id.playVideo);
            progressBar = itemView.findViewById(R.id.profileViewPostProgress);
        }
    }
}
